package xyz.santtu.materialcarrot

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.santtu.materialcarrot.databinding.AddProfileBinding
import java.security.SecureRandom
import java.util.*


class AddProfileDialogFragment(vm: MainScreenViewModel): AppCompatDialogFragment(){
    private lateinit var dialogBinding: AddProfileBinding
    private lateinit var profileTree: TreeMap<String, String>
    private var profileSelected = 0
    val model = vm
    val prng: SecureRandom by lazy { SecureRandom.getInstance("SHA1PRNG") }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        model.getProfileList().observe(this, Observer { profiless -> profileTree = profiless } )
        model.getSelectedProfile().observe(this, Observer { profSelected -> profileSelected = profSelected } )

        return activity?.let {
            //Setting up view
            dialogBinding = AddProfileBinding.inflate(requireActivity().layoutInflater)
            val view = dialogBinding.root

            // Setting up things...
            val randomBytes = ByteArray(8)
            prng.nextBytes(randomBytes)

            //setting ui up
            dialogBinding.profileSecret.text = String.format(
                getString(R.string.secret_here)+ MainActivity.readable(
                    MainActivity.toHex(
                        randomBytes
                    )
                )
            )
            val builder = MaterialAlertDialogBuilder(it)
            builder.setView(view)
                .setTitle(getString(R.string.title_activity_add_profile))
                .setPositiveButton(R.string.profile_save) { dialog, _ ->
                    val name = dialogBinding.profileEnterName.editText?.text.toString()
                    model.setProfileList(profileTree.apply {
                        this[name] = MainActivity.toHex(randomBytes)
                        Log.i("woowoo", this.toString())
                    })
                    model.setSelectedProfile(profileTree.headMap(name).size)
//                    clearSensitiveData()
//                    populateProfileSpinner()
//                    saveProfiles()
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog?.cancel() }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")



    }
}