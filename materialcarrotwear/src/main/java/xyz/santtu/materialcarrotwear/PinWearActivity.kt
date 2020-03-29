package xyz.santtu.materialcarrotwear

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import xyz.santtu.materialcarrotwear.databinding.ActivityMainWearBinding
import xyz.santtu.materialcarrotwear.databinding.PinInputBinding

const val PIN_CODE_ENTER = "xyz.permik.materialcarrotwear.PIN_CODE"

class PinWearActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private lateinit var binding: PinInputBinding
    private lateinit var ambientController: AmbientModeSupport.AmbientController
    lateinit var pinOrigColor: ColorStateList
    lateinit var circleOrigColor: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PinInputBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.pinOk.setOnClickListener { view -> pinOk(view) }
        binding.pinCancel.setOnClickListener { view -> pinCancel(view) }
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
            super.onEnterAmbient(ambientDetails);

        }

        override fun onExitAmbient() {
            // Handle exiting ambient mode

        }

        override fun onUpdateAmbient() {
            // Update the content
        }
    }


}
