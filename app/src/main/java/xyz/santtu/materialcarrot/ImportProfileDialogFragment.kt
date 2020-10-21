package xyz.santtu.materialcarrot

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.santtu.materialcarrot.databinding.ImportProfileBinding
import xyz.santtu.materialcarrotrepository.Profile
import xyz.santtu.materialcarrotrepository.ProfileViewModel
import xyz.santtu.materialcarrotutils.hexStringToByteArray


class ImportProfileDialogFragment(): AppCompatDialogFragment(){
    private var _binding: ImportProfileBinding? = null
    private val profileModel: ProfileViewModel by activityViewModels()
    private val dialogBinding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            //Setting up view
            _binding = ImportProfileBinding.inflate(requireActivity().layoutInflater)
            val view = dialogBinding.root

            val builder = MaterialAlertDialogBuilder(it)
            builder.setView(view)
                .setTitle(getString(R.string.title_activity_import_profile))
                .setPositiveButton(R.string.profile_import) { _, _ ->
                    run {
                            profileModel.delete(Profile(
                                0,
                                dialogBinding.profileEnterName.editText?.text.toString(),
                                dialogBinding.profileImportSecret.editText?.text.toString().hexStringToByteArray()
                            ))
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog?.cancel() }
            val dialog = builder.create()

            //setting ui up
            dialog.setOnShowListener { dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false }
            dialogBinding.profileImportSecret.editText?.let { editText ->
                editText.filters = arrayOf(HexInputFilter())
                editText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                            (editText.text.toString().length == 16)
                    }
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                })
            }
            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}