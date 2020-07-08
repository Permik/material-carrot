package xyz.santtu.materialcarrot

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.santtu.materialcarrot.databinding.AddProfileBinding
import java.security.SecureRandom
import java.util.*


class AddProfileDialogFragment(): AppCompatDialogFragment(){
    val prng: SecureRandom by lazy { SecureRandom.getInstance("SHA1PRNG") }
    private val ownModel: AddProfileFragmentViewModel by activityViewModels()
    private var _binding: AddProfileBinding? = null
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
            _binding = AddProfileBinding.inflate(requireActivity().layoutInflater)
            val view = dialogBinding.root

            //setting ui up

            val builder = MaterialAlertDialogBuilder(it)
            builder.setView(view)
                .setTitle(getString(R.string.title_activity_add_profile))
                .setPositiveButton(R.string.profile_save) { dialog, _ ->
                    callback?.onAddProfile(dialogBinding.profileEnterName.editText?.text.toString(), ownModel.getProfileSecret().value!!)
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
        ownModel.getProfileSecret().observe(this.requireActivity(), Observer { randomBytes ->
            dialogBinding.profileSecret.text = String.format(
                getString(R.string.secret_here)+ formatAddHexReadability(toHex(randomBytes))
            ) })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}