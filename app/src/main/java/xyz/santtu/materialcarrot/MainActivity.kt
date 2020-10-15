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

import android.os.Bundle
import android.text.SpannableString
import android.text.util.Linkify
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.santtu.materialcarrot.databinding.ActivityMainBinding
import xyz.santtu.materialcarrotrepository.Profile
import xyz.santtu.materialcarrotrepository.ProfileViewModel


// TODO: Add change all dialogs to fragment dialogs to preserve their states on rotate.


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var profileSelected = 0
    private var allProfiles: List<Profile> = emptyList()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        val profileModel: ProfileViewModel by viewModels()

        profileModel.allProfiles.observe(this, { profileList -> allProfiles = profileList
            invalidateOptionsMenu() })

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (allProfiles.isEmpty() && menu != null){
            menu.findItem(R.id.action_delete).isVisible = false
            menu.findItem(R.id.action_delete).isEnabled = false
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
        val profileModel: ProfileViewModel by viewModels()
        return when (item.itemId) {
            R.id.action_add -> {
                binding.navHostFragment.findNavController()
                    .navigate(CodeFragmentDirections.actionCodeFragment2ToAddProfileDialogFragment())
                true
            }
            R.id.action_import -> {
                binding.navHostFragment.findNavController()
                    .navigate(CodeFragmentDirections.actionCodeFragment2ToImportProfileDialogFragment())
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


}