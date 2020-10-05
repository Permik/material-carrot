package xyz.santtu.materialcarrotwear

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import xyz.santtu.materialcarrotwear.WearConst.ACTIVE_INTERVAL_MS
import xyz.santtu.materialcarrotwear.WearConst.AMBIENT_INTERVAL_MS
import xyz.santtu.materialcarrotwear.WearConst.AMBIENT_UPDATE_ACTION
import xyz.santtu.materialcarrotwear.WearConst.BURN_IN_OFFSET_PX
import xyz.santtu.materialcarrotwear.databinding.ActivityMainWearBinding
import java.lang.ref.WeakReference
import java.time.Instant
import kotlin.time.ExperimentalTime

const val MSG_UPDATE_SCREEN = 0

@ExperimentalTime
class MainWearActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider{



    private lateinit var binding: ActivityMainWearBinding
    private lateinit var ambientController: AmbientModeSupport.AmbientController
    private lateinit var ambientUpdateAlarmManager: AlarmManager
    private lateinit var ambientUpdatePendingIntent: PendingIntent
    private lateinit var ambientUpdateBroadcastReceiver: BroadcastReceiver
    lateinit var pinOrigColor: ColorStateList
    lateinit var circleOrigColor: IntArray
    private var isLowBitAmbient: Boolean = false
    private var doBurnInProtection: Boolean = false
    private var codeVisible: Boolean = false

    private val activeModeUpdateHandler: Handler = ActiveModeUpdateHandler(this)

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
        ambientUpdateAlarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java)!!

        val ambientUpdateIntent = Intent(AMBIENT_UPDATE_ACTION)

        ambientUpdatePendingIntent = PendingIntent.getBroadcast(this, 0, ambientUpdateIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        ambientUpdateBroadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                refreshDisplayAndSetNextUpdate()
            }

        }

    }

    override fun onResume() {
        super.onResume()

        val filter = IntentFilter(AMBIENT_UPDATE_ACTION)
        registerReceiver(ambientUpdateBroadcastReceiver, filter)

        refreshDisplayAndSetNextUpdate()
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(ambientUpdateBroadcastReceiver)

        activeModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN)
        ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent)
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

    fun refreshDisplayAndSetNextUpdate(){

        loadDataAndUpdateScreen()

        val timeMs = Instant.now().toEpochMilli()

        if (ambientController.isAmbient){
            val delayMs = AMBIENT_INTERVAL_MS - (timeMs % AMBIENT_INTERVAL_MS)
            val triggerTimeMs = timeMs + delayMs

            ambientUpdateAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                triggerTimeMs.toLong(), ambientUpdatePendingIntent)
        }else{
            val delayMs = ACTIVE_INTERVAL_MS - (timeMs % ACTIVE_INTERVAL_MS)

            activeModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN)
            activeModeUpdateHandler.sendEmptyMessageDelayed(MSG_UPDATE_SCREEN, delayMs.toLong())
        }

    }

    fun loadDataAndUpdateScreen(){

    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = MyAmbientCallback()

    inner class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {

        override fun onEnterAmbient(ambientDetails: Bundle?) {
            super.onEnterAmbient(ambientDetails)
            ambientDetails?.getBoolean(AmbientModeSupport.EXTRA_LOWBIT_AMBIENT) ?: isLowBitAmbient
            ambientDetails?.getBoolean(AmbientModeSupport.EXTRA_BURN_IN_PROTECTION) ?: doBurnInProtection

            activeModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN)


            binding.progressCircular.colorSchemeColors
            binding.progressCircular.setColorSchemeColors(Color.WHITE)
            binding.pinButton.visibility = View.GONE

            if (isLowBitAmbient){
                binding.text.paint.isAntiAlias = false
            }

            refreshDisplayAndSetNextUpdate()
        }

        override fun onExitAmbient() {
            // Handle exiting ambient mode

            ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent)

            binding.progressCircular.setColorSchemeColors(circleOrigColor.first())
            if (!codeVisible){
                binding.pinButton.visibility = View.VISIBLE
            }
            if (isLowBitAmbient){
                binding.text.paint.isAntiAlias = true
            }

            if (doBurnInProtection){
                binding.contentView.setPadding(0,0,0,0)
            }
        }

        override fun onUpdateAmbient() {
            // Update the content
            super.onUpdateAmbient()
            if(doBurnInProtection){
                val x = (Math.random() * 2 * BURN_IN_OFFSET_PX - BURN_IN_OFFSET_PX).toInt()
                val y = (Math.random() * 2 * BURN_IN_OFFSET_PX - BURN_IN_OFFSET_PX).toInt()
                binding.contentView.setPadding(x, y, 0 ,0)
            }
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

    inner class ActiveModeUpdateHandler(activity: MainWearActivity) : Handler() {
        private val mainActivityReference: WeakReference<MainWearActivity> = WeakReference<MainWearActivity>(
            activity
        )

        override fun handleMessage(msg: Message) {
            val mainWearActivity = mainActivityReference.get()

            if (mainWearActivity != null){
                if (msg.what == MSG_UPDATE_SCREEN){
                    refreshDisplayAndSetNextUpdate()
                }
            }
        }
    }

}

