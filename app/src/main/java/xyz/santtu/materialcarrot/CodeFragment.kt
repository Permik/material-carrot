/**
 * Copyright 2011 SECTRA Imtec AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author      Markus Berg <markus @ kelvin.nu>
 * @version     1.1
 * @since       2012-10-18
</markus> */
package xyz.santtu.materialcarrot

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.util.Linkify
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.santtu.materialcarrot.databinding.ActivityMainBinding
import xyz.santtu.materialcarrot.databinding.CodeGenerationBinding
import xyz.santtu.materialcarrot.databinding.ImportProfileBinding
import xyz.santtu.materialcarrotrepository.Profile
import xyz.santtu.materialcarrotrepository.ProfileViewModel
import xyz.santtu.materialcarrotutils.generateOtp
import xyz.santtu.materialcarrotutils.toHex
import java.time.Instant
import kotlin.properties.Delegates


// TODO: Add change all dialogs to fragment dialogs to preserve their states on rotate.


class CodeFragment : Fragment() {

    private var _binding: CodeGenerationBinding? = null
    private val dialogBinding get() = _binding!!
    private var timeout: CountDownTimer? = null
    private var profileSelected = 0
    private var timeCountDownStart: Long = 0
    private var profilePin: String = ""
    private var allProfiles: List<Profile> = emptyList()
    private var firstLoad: Boolean = true

    private val model: MainScreenViewModel by activityViewModels()
    private val profileModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = CodeGenerationBinding.inflate(requireActivity().layoutInflater)
        val binding = _binding!!

        model.setup()
        model.getOnetimePassword().observe(viewLifecycleOwner, { password -> binding.otpView.text = password })
        model.utcOffset.observe(viewLifecycleOwner, { utcOffset -> binding.utcView.text = utcOffset })
        model.selectedProfile.observe(viewLifecycleOwner, { profSelected -> profileSelected = profSelected
            Log.i("profSel", profileSelected.toString())
        })
        model.getCountdownStart().observe(viewLifecycleOwner, { timeStart ->
            timeCountDownStart = timeStart
            Log.i("cdStart", timeStart.toString())
            if (timeStart != 0L && timeStart+60000 > Instant.now().toEpochMilli()) {
                timeCountDownStart = countDownStart(timeStart, binding)
                binding.otpView.visibility = View.VISIBLE
            }
        })
        model.passwordPin.observe(viewLifecycleOwner, { pin -> profilePin = pin })
        profileModel.allProfiles.observe(viewLifecycleOwner, { profileList ->
            allProfiles = profileList
            populateProfileSpinner(requireActivity(),binding)
            if (firstLoad && profileList.size >= profileSelected){
                Log.i("profSel", profileSelected.toString())
                binding.profileSelector.setSelection(profileSelected)
                if (profileList.isNotEmpty()){
                    firstLoad = false
                }
            }
        })

        /// UI-bindings ///
        binding.buttonOk.setOnClickListener {
            Log.wtf("GenerateButton", allProfiles[profileSelected].profileName)
            Log.wtf(
                "GenerateButton",
                toHex(allProfiles[profileSelected].profileSecret)
            )
            model.setOnetimePassword(
                generateOtp(
                    profilePin,
                    toHex(allProfiles[profileSelected].profileSecret)
                )
            )
            model.setCountdownStart(countDownStart(0, binding))
            model.setCountdownStart(countDownStart(timeCountDownStart, binding))
            binding.otpView.visibility = View.VISIBLE
        }
        binding.otpView.setOnClickListener { otpView -> copyToClipboard(otpView as TextView?) }
        binding.enterPin.editText?.let {
            it.doOnTextChanged(action = { text, _, _, _ ->
                if (text?.length == 4) {
                    model.setPasswordPin((text as SpannableStringBuilder).toString())
                } else {
                    model.setPasswordPin("")
                }
            })
            it.setOnEditorActionListener { textView, actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_DONE && textView.text.length == 4){
                    Log.wtf("GenerateIMEButton", allProfiles[profileSelected].profileName)
                    Log.wtf(
                        "GenerateIMEButton",
                        toHex(allProfiles[profileSelected].profileSecret)
                    )
                    model.setOnetimePassword(
                        generateOtp(
                            profilePin,
                            toHex(allProfiles[profileSelected].profileSecret)
                        )
                    )
                    model.setCountdownStart(Instant.now().toEpochMilli())
                    binding.otpView.visibility = View.VISIBLE
                    false
                } else {
                    Toast.makeText(
                        context,
                        "Pin needs to be 4 digits long!",
                        Toast.LENGTH_LONG
                    ).show()
                    true
                }
            }
            it.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    /**
                     * checking profileTree.isEmpty() to work around a bug in older
                     * versions of Android where enterPin.enable(false) has no
                     * effect.
                     */
                    binding.buttonOk.isEnabled =
                        (binding.enterPin.editText!!.text.toString().length == 4
                                && allProfiles.isNotEmpty())
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })

        }
        binding.profileSelector.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.wtf("test", binding.profileSelector.selectedItem as String)
                model.setSelectedProfile(position)
                if (position != profileSelected) { // The selected profile really has changed
                    clearSensitiveData(binding)
                }
            }
        }
        populateProfileSpinner(requireActivity(),binding)

        return dialogBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





    /**
     * Populate the profileSpinner, and select the correct profile
     */
    fun populateProfileSpinner(context: Context, binding: CodeGenerationBinding){
        val profileList: ArrayList<String>
        if (allProfiles.isNullOrEmpty()) {
            profileList = ArrayList()
            profileList.add("(no profile)")
        } else {
            profileList = allProfiles.map { it.profileName } as ArrayList<String>
        }
        val aa = ArrayAdapter(
            context,
            R.layout.support_simple_spinner_dropdown_item, ArrayList(profileList)
        )
        // Specify the layout to use when the list of choices appears
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.profileSelector.adapter = aa
        // Disable pin-entry, delete-button, and profilespinner.
        // if there are no profiles.
        if (allProfiles.isNullOrEmpty()) {
            binding.enterPin.isEnabled = false
            binding.profileSelector.isEnabled = false
        } else {
            binding.enterPin.isEnabled = true
            binding.profileSelector.isEnabled = true
        }
    }

    /**
     * Copy the current one-time-password to the clipboard. This is a callback
     * for onclick on the password TextView.
     *
     * @param view
     */
    fun copyToClipboard(view: TextView?) { // Gets a handle to the clipboard service.
        val cm = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        val otpClip = ClipData.newPlainText("text", view?.text.toString())
        cm?.primaryClip?.addItem(otpClip.getItemAt(0))
        Toast.makeText(
            requireActivity(), "One-time-password copied to clipboard",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Start the countdown timer after the otp has been generated. When the
     * timer runs down, all sensitive fields are cleared.
     */
    fun countDownStart(timeStart: Long, binding: CodeGenerationBinding): Long { // Log.i("countDownStart", "Start the countdown.");
        try {
            timeout!!.cancel()
        } catch (e: NullPointerException) { // ignore
        }
        var timecdStart = Instant.now().toEpochMilli()
        var secondsLeft = 60
        if (timeStart != 0L) {
            // Resume the timer, likely after a screen rotate.
            // Adjust values accordingly
            secondsLeft = (60 - (timecdStart - timeStart) / 1000).toInt()
            timecdStart = timeStart
        }
        binding.progressBar.progress = secondsLeft * 2
        timeout = object : CountDownTimer((secondsLeft * 1000).toLong(), 500) {
            override fun onTick(millisUntilFinished: Long) {
                binding.progressBar.progress = millisUntilFinished.toInt() / 500
            }

            override fun onFinish() { // Log.i("onFinish", "Countdown timer has finished");
                clearSensitiveData(binding)
            }
        }.start()
        binding.progressBar.visibility = View.VISIBLE
        return timecdStart
    }

    /**
     * Clear all fields of sensitive data.
     *
     */
    fun clearSensitiveData(binding: CodeGenerationBinding) {
        // Log.i("clearSensitiveData",
        // "wipe pin, current otp, countdownbar, etc.");
        binding.enterPin.editText?.setText("")
        binding.otpView.text = ""
        binding.otpView.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        timeCountDownStart = 0L
        try {
            timeout!!.cancel()
        } catch (e: NullPointerException) { // ignore
        }
    }
}