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

import android.content.Context
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.os.CountDownTimer
import android.text.*
import android.text.util.Linkify
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.santtu.materialcarrot.databinding.ActivityMainBinding
import xyz.santtu.materialcarrot.databinding.AddProfileBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*

// TODO: Add change all dialogs to fragment dialogs to preserve their states on rotate.


class MainActivity : AppCompatActivity() {
    private lateinit var dialogBinding: AddProfileBinding
    private lateinit var binding: ActivityMainBinding
    private lateinit var profileTree: TreeMap<String, String>
    private var timeout: CountDownTimer? = null
    private var profileSelected = 0
    private var timeCountDownStart: Long = 0
    val prng: SecureRandom by lazy { SecureRandom.getInstance("SHA1PRNG") }
//    val model: MainScreenViewModel by viewModels<MainScreenViewModel>()


    public override fun onCreate(savedInstanceState: Bundle?) { // Log.i("onCreate", "Create main activity");
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val model: MainScreenViewModel by viewModels<MainScreenViewModel>()
        model.getOnetimePassword().observe(this, Observer { password -> binding.otpView.text = password } )
        model.getUtcOffset().observe(this, Observer { utcOffset -> binding.utcView.text = utcOffset})
        model.getProfileList().apply{
            this.observe(this@MainActivity, Observer { profiless -> profileTree = profiless; populateProfileSpinner(model) } )
            profileTree = this.value!!
        }
        model.getSelectedProfile().observe(this, Observer { profSelected -> profileSelected = profSelected } )
        model.getCountdownStart().observe(this, Observer { timeStart -> timeCountDownStart = timeStart } )
        binding.otpView.setOnClickListener { otpView -> copyToClipboard(otpView) }
        loadPreferences()
        populateProfileSpinner(model)
        addProfileSpinnerListener(model)
        addPinListener(model)
    }

    public override fun onStop() {
        super.onStop()
        // Log.i("onStop", "App has been stopped");
    }

    public override fun onPause() {
        super.onPause()
        // Log.i("onPause", "App has been sent to background");
    }

    public override fun onResume() {
        super.onResume()
        // Log.i("onResume", "The app is coming back");
    }

//    public override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        // Save whatever we need to persist
//// Log.i("onSaveInstanceState", "save pin and otp, etc.");
//        //outState.putString("pin", binding.enterPin.editText?.text.toString())
//        outState.putString("otp", binding.otpView.text.toString())
//        outState.putLong("timeCountDownStart", timeCountDownStart)
//    }

//    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        // Log.i("onRestoreInstanceState", "restore pin and otp, etc.");
//// Retrieve the data stored onSaveInstanceState
//        timeCountDownStart = savedInstanceState.getLong("timeCountDownStart")
//        if (timeCountDownStart != 0L) {
//            //binding.enterPin.editText?.text ?: savedInstanceState.getString("pin")
//            binding.otpView.text = savedInstanceState.getString("otp")
//            binding.otpView.visibility = View.VISIBLE
//            countDownStart(timeCountDownStart)
//        }
//    }

    /**
     * Commenting this out. Doesn't update the action bar...
     *
     * @Override public boolean onPrepareOptionsMenu(Menu menu) { MenuItem
     * actionDelete = menu.findItem(R.id.action_delete);
     * actionDelete.setEnabled(!profileTree.isEmpty());
     *
     * super.onPrepareOptionsMenu(menu); return true; }
     */
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
        return when (item.itemId) {
            R.id.action_add -> {

                val addProfFragment = AddProfileDialogFragment()
                addProfFragment.callback = object : AddProfileDialogFragment.SetOnPositiveListener{
                    override fun onAddProfile(name: String, secret: ByteArray) {
                        model.setProfileList(profileTree.apply { this[name] = toHex(secret)})
                        populateProfileSpinner(model)
                    }
                }
                addProfFragment.show(supportFragmentManager, "AddProfile")
                true
            }
            R.id.action_delete -> {
                // Log.i("action_delete", "delete current profile");
                if (profileTree.isNullOrEmpty()) {
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
                        model.setProfileList(profileTree.apply { this.remove(binding.profileSelector.selectedItem) })
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
     * Define and open the add profile dialog
     */
    fun dialogAddProfile(model: MainScreenViewModel) {
        val dialog = AppCompatDialog(this)
        dialogBinding = AddProfileBinding.inflate(LayoutInflater.from(baseContext))
        val view = dialogBinding.root
        dialog.setContentView(view)
        dialog.setTitle(getString(R.string.title_activity_add_profile))
        // Generate secret
        val randomBytes = ByteArray(8)
        prng.nextBytes(randomBytes)
        // Display secret in ui
        dialogBinding.profileSecret.text = String.format(
            getString(R.string.secret_here)+formatAddHexReadability(toHex(randomBytes))
        )
        dialog.show()
    }

    /**
     * Listen to changes in the profileSpinner
     */
    fun addProfileSpinnerListener(model: MainScreenViewModel) { // Log.i("addProfileSpinnerListener",
// "Add listener to profile spinner");
        binding.profileSelector.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?, view: View?,
                pos: Int, id: Long
            ) { // Log.i("onItemSelectedListener", "Profile " +
// Integer.toString(pos) + " was selected");
// This is triggered a lot, but not always when a new item
// has been selected. Need to check for that:
                if (pos != profileSelected) { // The selected profile really has changed
                    model.setSelectedProfile(pos)
                    saveProfileSelected()
                    clearSensitiveData()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) { // Log.i("onNothingSelected", "No profile was selected");
// enterPin.setEnabled(false);
            }
        }
    }

    /**
     * Set a listener on the enterPin EditText to only enable the Ok-button once
     * a four-digit PIN has been entered.
     */
    fun addPinListener(model: MainScreenViewModel) {
        binding.enterPin.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                /**
                 * checking profileTree.isEmpty() to work around a bug in older
                 * versions of Android where enterPin.enable(false) has no
                 * effect.
                 */
                binding.buttonOk.isEnabled = (binding.enterPin.editText!!.text.toString().length == 4
                        && !profileTree.isEmpty())

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) { // TODO Auto-generated method stub
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) { // TODO Auto-generated method stub
            }
        })
        binding.buttonOk.setOnClickListener { generateOtp(binding.buttonOk, model) }
        binding.enterPin.editText?.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                generateOtp(binding.buttonOk, model)
                true
            } else {
                false
            }
        }
    }

    /**
     * Fetch profiles and the selected profile from saved prefs
     */
    fun loadPreferences() { // Log.i("loadPreferences", "Loading preferences from disk");
        var preferences = getSharedPreferences(
            "Profiles",
            Context.MODE_PRIVATE
        )
//        profileTree = TreeMap()
//        val profileDump = preferences.all
//        for ((key, value) in profileDump) {
//            profileTree!![key] = value as String
//        }
        /**
         * temporary hard-coded profiles if there's nothing stored
         *
         * if (profileTree.isEmpty()) { profileTree.put("Earth",
         * "b0bbf8eb606254dd"); profileTree.put("Mercury", "b0bbf8eb606254db");
         * profileTree.put("Venus", "b0bbf8eb606254dc"); }
         */
        // Open the main app preferences
        preferences = getSharedPreferences("Main", Context.MODE_PRIVATE)
        //profileSelected = preferences.getInt("profileSelected", 0)
    }

    /**
     * Save profiles to prefs
     */
    fun saveProfiles() {
        val preferences = getSharedPreferences(
            "Profiles",
            Context.MODE_PRIVATE
        )
        val prefEditor = preferences.edit()
        prefEditor.clear()
        for (k in profileTree.keys) {
            prefEditor.putString(k, profileTree[k])
        }
        prefEditor.apply()
    }

    /**
     * Populate the profileSpinner, and select the correct profile
     */
    fun populateProfileSpinner(model: MainScreenViewModel) { // Log.i("populateProfileSpinner", "Add all profiles to spinner");
        val profileList: ArrayList<String>
        if (profileTree.isNullOrEmpty()) {
            profileList = ArrayList()
            profileList.add("(no profile)")
        } else {
            profileList = ArrayList(profileTree.keys)
        }
        val aa = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item, ArrayList(
                profileList
            )
        )
        // Specify the layout to use when the list of choices appears
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.profileSelector.adapter = aa
        // Log.i("setSelectedProfile", "Set the currently selected profile");
        if (!profileTree.isNullOrEmpty()){
            profileSelected =
                if (profileSelected == profileTree.size) profileSelected - 1 else profileSelected
            profileSelected = if (profileSelected < 0) 0 else profileSelected
            model.setSelectedProfile(profileSelected)
        }
        // Disable pin-entry, delete-button, and profilespinner
// if there are no profiles
        if (profileTree.isNullOrEmpty()) {
            binding.enterPin.isEnabled = false
            binding.profileSelector.isEnabled = false
        } else {
            binding.profileSelector.setSelection(profileSelected)
            binding.enterPin.isEnabled = true
            binding.profileSelector.isEnabled = true
        }
    }

    /**
     * Save the currently selected profile to prefs
     */
    fun saveProfileSelected() {
        val profileSettings = getSharedPreferences(
            "Main",
            Context.MODE_PRIVATE
        )
        val prefEditor = profileSettings.edit()
        prefEditor.putInt("profileSelected", profileSelected)
        prefEditor.apply()
    }

    /**
     * Copy the current one-time-password to the clipboard. This is a callback
     * for onclick on the password TextView.
     *
     * @param view
     */
    fun copyToClipboard(view: View?) { // Gets a handle to the clipboard service.
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        val otpClip = ClipData.newPlainText("text", binding.otpView.text.toString())
        cm?.primaryClip?.addItem(otpClip.getItemAt(0))
        Toast.makeText(
            this, "One-time-password copied to clipboard",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Perform all the necessary motp calculations called when the user clicks
     * the Ok button
     */
    fun generateOtp(view: View?, model: MainScreenViewModel) {
        val pin = binding.enterPin.editText?.text.toString()
        val now = Calendar.getInstance()
        val selection = binding.profileSelector.selectedItem as String
        var epoch = now.timeInMillis.toString()
        epoch = epoch.substring(0, epoch.length - 4)
        val hash = md5(epoch + profileTree[selection] + pin)
        val otp = hash.substring(0, 6)
        model.setOnetimePassword(otp)
        binding.otpView.text = otp
        binding.otpView.visibility = View.VISIBLE
        countDownStart(0L, model)
    }

    /**
     * Start the countdown timer after the otp has been generated. When the
     * timer runs down, all sensitive fields are cleared.
     */
    fun countDownStart(timeStart: Long, model: MainScreenViewModel) { // Log.i("countDownStart", "Start the countdown.");
        try {
            timeout!!.cancel()
        } catch (e: NullPointerException) { // ignore
        }
        model.setCountdownStart(timeCountDownStart.apply { Calendar.getInstance().timeInMillis } )
        var secondsLeft = 60
        if (timeStart != 0L) { // Resume the timer, likely after a screen rotate.
// Adjust values accordingly
            secondsLeft = (60 - (timeCountDownStart - timeStart) / 1000).toInt()
            model.setCountdownStart(timeCountDownStart.apply { timeStart } )
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
    }

    /**
     * Clear all fields of sensitive data.
     */
    fun clearSensitiveData() { // Log.i("clearSensitiveData",
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