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
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.util.Linkify
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.santtu.materialcarrot.databinding.ActivityMainBinding
import xyz.santtu.materialcarrot.databinding.AddProfileBinding
import java.security.SecureRandom
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

// TODO: Add change all dialogs to fragment dialogs to preserve their states on rotate.


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var timeout: CountDownTimer? = null
    private var profileSelected = 0
    private var timeCountDownStart: Long = 0
    private var profilePin: String = ""
    private var selectedProfileItem: String = ""
    private var allProfiles: List<Profile> = emptyList()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val model: MainScreenViewModel by viewModels<MainScreenViewModel>()
        val profileModel: ProfileViewModel by viewModels<ProfileViewModel>()
        model.getOnetimePassword().observe(this, { password -> binding.otpView.text = password })
        model.getUtcOffset().observe(this, { utcOffset -> binding.utcView.text = utcOffset})
        model.getSelectedProfile().observe(this, { profSelected -> profileSelected = profSelected })
        model.getSelectedProfileString().observe(this,
            { selectedProfString -> selectedProfileItem = selectedProfString })
        model.getCountdownStart().observe(this, { timeStart -> timeCountDownStart = timeStart
            if (timeStart != 0L) {
                timeCountDownStart = countDownStart(timeStart)
                binding.otpView.visibility = View.VISIBLE
            }
        })
        model.getPasswordPin().observe(this, { pin -> profilePin = pin })
        profileModel.allProfiles.observe(this, {
                profileList -> allProfiles = profileList
            populateProfileSpinner(model)
            if (profileList.isEmpty()){
                invalidateOptionsMenu()
            }
        })

        /// UI-bindings ///
        binding.buttonOk.setOnClickListener {
            Log.wtf("GenerateButton", allProfiles[profileSelected].profileName)
            Log.wtf("GenerateButton", toHex(allProfiles[profileSelected].profileSecret))
            model.setOnetimePassword(generateOtp(profilePin, toHex(allProfiles[profileSelected].profileSecret)))
            model.setCountdownStart(countDownStart(0))
            model.setCountdownStart(countDownStart(timeCountDownStart))
            binding.otpView.visibility = View.VISIBLE
        }
        binding.otpView.setOnClickListener { otpView -> copyToClipboard(otpView as TextView?) }
        binding.enterPin.editText?.let {
            it.doOnTextChanged(action = { text, _, _, _ ->
                if (text?.length == 4) {
                    model.setPasswordPin((text as SpannableStringBuilder).toString())
                } else{
                    model.setPasswordPin("")
                }
            })
            it.setOnEditorActionListener { textView, actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_DONE && textView.text.length == 4){
                    Log.wtf("GenerateIMEButton", allProfiles[profileSelected].profileName)
                    Log.wtf("GenerateIMEButton", toHex(allProfiles[profileSelected].profileSecret))
                    model.setOnetimePassword(generateOtp(profilePin, toHex(allProfiles[profileSelected].profileSecret)))
                    model.setCountdownStart(countDownStart(0))
                    model.setCountdownStart(countDownStart(timeCountDownStart))
                    binding.otpView.visibility = View.VISIBLE
                    false
                } else {
                    Toast.makeText(applicationContext, "Pin needs to be 4 digits long!", Toast.LENGTH_LONG).show()
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
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
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

                model.setSelectedProfileString(binding.profileSelector.selectedItem as String)
                model.setSelectedProfile(position)
                if (position != profileSelected) { // The selected profile really has changed
                    clearSensitiveData()
                }
            }
        }
        populateProfileSpinner(model)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (allProfiles.isEmpty() && menu != null){
            menu.findItem(R.id.action_delete).setEnabled(false)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    /**
     * Handle menu selections, present dialog boxes in response to menu
     * selections, etc.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handle item selection
        val builder: MaterialAlertDialogBuilder
        val alert: AlertDialog
        val model: MainScreenViewModel by viewModels<MainScreenViewModel>()
        val profileModel: ProfileViewModel by viewModels<ProfileViewModel>()
        return when (item.itemId) {
            R.id.action_add -> {

                val addProfFragment = AddProfileDialogFragment()
                addProfFragment.callback = object : AddProfileDialogFragment.SetOnPositiveListener{
                    override fun onAddProfile(name: String, secret: ByteArray) {
                        profileModel.insert(Profile(0, name, secret))
                        populateProfileSpinner(model)
                    }
                }
                addProfFragment.show(supportFragmentManager, "AddProfile")
                true
            }
            R.id.action_import -> {
                val addProfFragment = ImportProfileDialogFragment()
                addProfFragment.callback = object : ImportProfileDialogFragment.SetOnPositiveListener{
                    override fun onAddProfile(name: String, secret: ByteArray) {
                        profileModel.insert(Profile(0, name, secret))
                        populateProfileSpinner(model)
                    }
                }
                addProfFragment.show(supportFragmentManager, "ImportProfile")
                true
            }
            R.id.action_delete -> {
                // Log.i("action_delete", "delete current profile");
                Log.i("yes", allProfiles[profileSelected].toString())
                if (allProfiles.isNullOrEmpty()) {
                    return false
                }
                builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(getString(R.string.profile_delete))
                    .setMessage(getString(R.string.profile_delete_confirm))
                    .setCancelable(true)
                    .setPositiveButton(
                        getString(R.string.profile_delete_confirm_button)
                    ) { _, _ ->
                        // click listener on the alert box
                        // The button was clicked
                        // Remove the currently selected profile
                        Log.i("yes", allProfiles[profileSelected].toString())
                        profileModel.delete(allProfiles[profileSelected])
                        clearSensitiveData()
                        populateProfileSpinner(model)
                    }
                alert = builder.create()
                alert.show()
                true
            }
            R.id.action_about -> {
                builder = MaterialAlertDialogBuilder(this)
                val textAbout = SpannableString(String.format(getString(R.string.app_about)))
                Linkify.addLinks(textAbout, Linkify.WEB_URLS)
                builder.setTitle("About").setMessage(textAbout)
                alert = builder.create()
                alert.show()
                true
            }
            else -> true
        }
    }

    /**
     * Populate the profileSpinner, and select the correct profile
     */
    fun populateProfileSpinner(model: MainScreenViewModel): Int {
        val profileList: ArrayList<String>
        if (allProfiles.isNullOrEmpty()) {
            profileList = ArrayList()
            profileList.add("(no profile)")
        } else {
            profileList = allProfiles.map { it.profileName } as ArrayList<String>
        }
        val aa = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item, ArrayList(profileList)
        )
        // Specify the layout to use when the list of choices appears
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.profileSelector.adapter = aa
        // Log.i("setSelectedProfile", "Set the currently selected profile");
        if (!allProfiles.isNullOrEmpty()){
            profileSelected =
                if (profileSelected == profileList.size) profileSelected - 1 else profileSelected
            profileSelected = if (profileSelected < 0) 0 else profileSelected
            model.setSelectedProfile(profileSelected)
        }
        // Disable pin-entry, delete-button, and profilespinner.
        // if there are no profiles.
        if (allProfiles.isNullOrEmpty()) {
            binding.enterPin.isEnabled = false
            binding.profileSelector.isEnabled = false
        } else {
            binding.profileSelector.setSelection(profileSelected)
            binding.enterPin.isEnabled = true
            binding.profileSelector.isEnabled = true
        }
        return profileSelected
    }

    /**
     * Copy the current one-time-password to the clipboard. This is a callback
     * for onclick on the password TextView.
     *
     * @param view
     */
    fun copyToClipboard(view: TextView?) { // Gets a handle to the clipboard service.
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        val otpClip = ClipData.newPlainText("text", view?.text.toString())
        cm?.primaryClip?.addItem(otpClip.getItemAt(0))
        Toast.makeText(
            this, "One-time-password copied to clipboard",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Start the countdown timer after the otp has been generated. When the
     * timer runs down, all sensitive fields are cleared.
     */
    fun countDownStart(timeStart: Long): Long { // Log.i("countDownStart", "Start the countdown.");
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
                clearSensitiveData()
            }
        }.start()
        binding.progressBar.visibility = View.VISIBLE
        return timecdStart
    }

    /**
     * Clear all fields of sensitive data.
     *
     */
    fun clearSensitiveData() {
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