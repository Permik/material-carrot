package xyz.santtu.materialcarrotwear

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import xyz.santtu.materialcarrotwear.databinding.PinInputBinding

const val PIN_CODE_ENTER = "xyz.permik.materialcarrotwear.PIN_CODE"

class PinWearActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private lateinit var binding: PinInputBinding
    private lateinit var ambientController: AmbientModeSupport.AmbientController
    lateinit var pinOrigColor: ColorStateList
    lateinit var circleOrigColor: IntArray
    private var isLowBitAmbient: Boolean = false
    private var doBurnInProtection: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PinInputBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.pinOk.setOnClickListener { okButton -> pinOk(okButton) }
        binding.pinCancel.setOnClickListener { cancelButton -> pinCancel(cancelButton) }
        // Enables Always-on
        ambientController = AmbientModeSupport.attach(this)
    }

    fun pinOk(v: View){
        val msg: String = binding.outlinedTextField.editText?.text.toString()

        Intent(PIN_CODE_ENTER).apply {
            putExtra(PIN_CODE_ENTER, msg)
        }.also { result ->
            setResult(Activity.RESULT_OK, result)
        }
        finish()

    }

    fun pinCancel(v: View){
        Intent(PIN_CODE_ENTER).also { result ->
            setResult(Activity.RESULT_CANCELED, result)
        }
        finish()
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = MyAmbientCallback()

    inner class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {

        override fun onEnterAmbient(ambientDetails: Bundle?) {
            super.onEnterAmbient(ambientDetails)
            ambientDetails?.getBoolean(AmbientModeSupport.EXTRA_LOWBIT_AMBIENT) ?: isLowBitAmbient
            ambientDetails?.getBoolean(AmbientModeSupport.EXTRA_BURN_IN_PROTECTION) ?: doBurnInProtection


        }

        override fun onExitAmbient() {
            // Handle exiting ambient mode

        }

        override fun onUpdateAmbient() {
            // Update the content
        }
    }


}
