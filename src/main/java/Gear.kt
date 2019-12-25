class Gear(val gearNumber: Int, val maxSpeed: Int, val minSpeed: Int, val isNeutral: Boolean,
           val eLimitedSpeed: Int, val minRPMs: Int, val redline: Int) {

    val speeds: HashMap<Int, Int>

    init {
        speeds = if (isNeutral) getSpeedsPrivateNeutral() else getSpeedsPrivate(redline, maxSpeed, minSpeed)
        println("[DEBUG] Loaded gear number $gearNumber")
    }

    fun getSpeedsPrivate(redline: Int, maxSpeed: Int, minSpeed: Int): HashMap<Int, Int> {
        val speeds = HashMap<Int, Int>()
        val speedRange = maxSpeed - minSpeed
        for (i in speedRange..0) {
            speeds[maxSpeed - i] = redline * (i / speedRange)
        }
        return speeds
    }

    fun getSpeedsPrivateNeutral(): HashMap<Int, Int> {
        val speeds = HashMap<Int, Int>()
        for (i in 0..eLimitedSpeed) {
            speeds[i] = minRPMs
        }
        return speeds
    }
}