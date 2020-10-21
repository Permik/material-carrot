package xyz.santtu.materialcarrot

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.santtu.materialcarrot.databinding.AddProfileBinding
import xyz.santtu.materialcarrotrepository.Profile
import xyz.santtu.materialcarrotrepository.ProfileViewModel
import xyz.santtu.materialcarrotutils.formatAddHexReadability
import xyz.santtu.materialcarrotutils.toHex

// TODO: Should be merged with [ImportProfileDialogFragment]
class AddProfileDialogFragment: AppCompatDialogFragment(){
    private val ownModel: AddProfileFragmentViewModel by activityViewModels()
    private val profileModel: ProfileViewModel by activityViewModels()
    private var _binding: AddProfileBinding? = null
    private val dialogBinding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            //Setting up view
            _binding = AddProfileBinding.inflate(requireActivity().layoutInflater)
            val view = dialogBinding.root

            //setting ui up



            val builder = MaterialAlertDialogBuilder(it)
            builder.setView(view)
                .setTitle(getString(R.string.title_activity_add_profile))
                .setPositiveButton(R.string.profile_save) { _, _ ->
                    run {
                        profileModel.insert(
                            Profile(
                                0,
                                dialogBinding.profileEnterName.editText?.text.toString(),
                                ownModel.getProfileSecret().value!!
                            )
                        )
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog?.cancel() }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ownModel.getProfileSecret().observe(requireActivity(), { randomBytes ->
            dialogBinding.profileSecret.text = formatAddHexReadability(toHex(randomBytes))
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}