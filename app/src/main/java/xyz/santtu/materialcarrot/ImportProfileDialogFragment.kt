package xyz.santtu.materialcarrot

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.santtu.materialcarrot.databinding.ImportProfileBinding
import xyz.santtu.materialcarrotutils.hexStringToByteArray
import java.security.SecureRandom


class ImportProfileDialogFragment(): AppCompatDialogFragment(){
    val prng: SecureRandom by lazy { SecureRandom.getInstance("SHA1PRNG") }
    private var _binding: ImportProfileBinding? = null
    private val dialogBinding get() = _binding!!
    internal var callback: SetOnPositiveListener? = null

    fun SetOnPositiveListener(callback: SetOnPositiveListener){
        this.callback = callback
    }

    interface SetOnPositiveListener {
        fun onAddProfile(name:String, secret: ByteArray)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            //Setting up view
            _binding = ImportProfileBinding.inflate(requireActivity().layoutInflater)
            val view = dialogBinding.root

            //setting ui up



            val builder = MaterialAlertDialogBuilder(it)
            builder.setView(view)
                .setTitle(getString(R.string.title_activity_import_profile))
                .setPositiveButton(R.string.profile_import) { _, _ ->
                    run {
                        callback?.onAddProfile(
                            dialogBinding.profileEnterName.editText?.text.toString(),
                            dialogBinding.profileImportSecret.editText?.text.toString().hexStringToByteArray()
                        )
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog?.cancel() }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}