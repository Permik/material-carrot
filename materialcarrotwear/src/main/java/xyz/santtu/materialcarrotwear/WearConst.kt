package xyz.santtu.materialcarrotwear

import kotlin.time.ExperimentalTime
import kotlin.time.seconds

object WearConst{
    @ExperimentalTime
    val ACTIVE_INTERVAL_MS = 1.seconds.inMilliseconds
    @ExperimentalTime
    val AMBIENT_INTERVAL_MS = 10.seconds.inMilliseconds
    const val BURN_IN_OFFSET_PX = 10
    const val AMBIENT_UPDATE_ACTION = "xyz.santtu.materialcarrotwear.action.AMBIENT_UPDATE"
}