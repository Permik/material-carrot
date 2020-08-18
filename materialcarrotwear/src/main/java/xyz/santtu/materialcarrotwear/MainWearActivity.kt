package xyz.santtu.materialcarrotwear

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import xyz.santtu.materialcarrotwear.databinding.ActivityMainWearBinding
import java.util.ArrayList


class MainWearActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider{

    private lateinit var binding: ActivityMainWearBinding
    private lateinit var ambientController: AmbientModeSupport.AmbientController
    lateinit var pinOrigColor: ColorStateList
    lateinit var circleOrigColor: IntArray

    var codeVisible: Boolean = false

    // TODO: store all values in viewmodels
    // When leaving activity, store the timer start time
    // When returning then add the displaytime to the start time -> finished time
    // then reduce the current time from finished time -> time left
    // then divide the time left by the displaytime ->
    //      float that tells what the timers start position should be
    //      if over 1, discard
    //      if 0, discard
    // then also set the totaltime to the time left remaining
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainWearBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.progressCircular.setOnTimerFinishedListener {
            binding.code.text = ""
            binding.text.visibility = View.VISIBLE
            binding.pinButton.visibility = View.VISIBLE
            binding.progressCircular.stopTimer()
            codeVisible = false
        }

        pinOrigColor = binding.pinButton.backgroundTintList!!
        circleOrigColor = binding.progressCircular.colorSchemeColors

        binding.pinButton.setOnClickListener {pin.launch(this)}

        // Enables Always-on
        ambientController = AmbientModeSupport.attach(this)
    }

    private val pin = registerForActivityResult(GetPin()) {
        if (!it.isNullOrBlank()){
            binding.code.text = it
            setTimer()
            codeVisible = true
        }
    }

    fun setTimer(){
        binding.pinButton.visibility = View.GONE
        binding.text.visibility = View.GONE
        binding.code.visibility = View.VISIBLE
        binding.progressCircular.totalTime = 60000L
        binding.progressCircular.startTimer()
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = MyAmbientCallback()

    inner class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {

        override fun onEnterAmbient(ambientDetails: Bundle?) {
            super.onEnterAmbient(ambientDetails)
            binding.text.paint.isAntiAlias = false
            binding.progressCircular.colorSchemeColors
            binding.progressCircular.setColorSchemeColors(Color.WHITE)
            binding.pinButton.visibility = View.GONE
        }

        override fun onExitAmbient() {
            // Handle exiting ambient mode
            binding.text.paint.isAntiAlias = true
            binding.progressCircular.setColorSchemeColors(circleOrigColor.first())
            if (!codeVisible){
                binding.pinButton.visibility = View.VISIBLE
            }
        }

        override fun onUpdateAmbient() {
            // Update the content
            super.onUpdateAmbient()
        }
    }

    inner class GetPin : ActivityResultContract<Context, String?>() {
        override fun createIntent(
            context: Context,
            input: Context?
        ): Intent =
            Intent(context, PinWearActivity::class.java)

        override fun parseResult(resultCode: Int, result: Intent?) : String? {
            if (resultCode != Activity.RESULT_OK) {
                return null
            }
            return result?.getStringExtra(PIN_CODE_ENTER)
        }
    }

}

