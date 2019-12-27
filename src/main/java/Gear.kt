class Gear(val gearNumber: Int, val maxSpeed: Int, val minSpeed: Int, val isNeutral: Boolean,
           val eLimitedSpeed: Double, val minRPMs: Int, val redline: Int) {

    val speeds: HashMap<Int, Double>
    val rpms: HashMap<Int, Double>

    init {
        speeds = if (isNeutral) getSpeedsPrivateNeutral() else getSpeedsPrivate(redline)
        rpms = if (isNeutral) getSpeedsPrivateNeutral() else getRPMsPrivate(redline)
        println("[DEBUG] Loaded gear number $gearNumber")
    }

    private fun getSpeedsPrivate(redline: Int): HashMap<Int, Double> {
        val speeds = HashMap<Int, Double>()
        val speedRange = (maxSpeed - minSpeed)
        var counter = 0
        for (i in maxSpeed downTo minSpeed) {
            speeds[i] = 1000 + (redline.toDouble() / counter++.toDouble())
        }
        return speeds
    }

    private fun getSpeedsPrivateNeutral(): HashMap<Int, Double> {
        val speeds = HashMap<Int, Double>()
        for (i in 0..eLimitedSpeed.toInt()) {
            speeds[i] = minRPMs.toDouble()
        }
        return speeds
    }

    private fun getRPMsPrivate(redline: Int): HashMap<Int, Double> {
        val rpms = HashMap<Int, Double>()
        for (i in 750..redline) {
            rpms[i] = (maxSpeed / (redline.toDouble() / i.toDouble()))
        }
        return rpms
    }

//    TODO maybe change this later
    fun getRPMsPrivateNeutral(redline: Int, maxSpeed: Int): HashMap<Int, Double> {
        val rpms = HashMap<Int, Double>()
        for (i in 750..redline) {
            rpms[i] = (0.00)
        }
        return rpms
    }

}