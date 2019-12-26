class Gear(val gearNumber: Int, val maxSpeed: Int, val minSpeed: Int, val isNeutral: Boolean,
           val eLimitedSpeed: Int, val minRPMs: Int, val redline: Int) {

    val speeds: HashMap<Int, Int>
    val rpms: HashMap<Int, Int>

    init {
        speeds = if (isNeutral) getSpeedsPrivateNeutral() else getSpeedsPrivate(redline, maxSpeed, minSpeed)
        rpms = if (isNeutral) getSpeedsPrivateNeutral() else getRPMsPrivate(redline, maxSpeed)
        println("[DEBUG] Loaded gear number $gearNumber")
    }

    fun getSpeedsPrivate(redline: Int, maxSpeed: Int, minSpeed: Int): HashMap<Int, Int> {
        val speeds = HashMap<Int, Int>()
        val speedRange = (maxSpeed - minSpeed).also(::println)
        for (i in 0..speedRange) {
            speeds[maxSpeed - i] = (redline / speedRange * i)
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

    fun getRPMsPrivate(redline: Int, maxSpeed: Int): HashMap<Int, Int> {
        val rpms = HashMap<Int, Int>()
        for (i in 750..redline) {
            rpms[i] = (maxSpeed / (redline / i))
        }
        return rpms
    }

//    TODO maybe change this later
    fun getRPMsPrivateNeutral(redline: Int, maxSpeed: Int): HashMap<Int, Int> {
        val rpms = HashMap<Int, Int>()
        for (i in 750..redline) {
            rpms[i] = (0)
        }
        return rpms
    }

}