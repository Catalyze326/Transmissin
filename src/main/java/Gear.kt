open class Gear(val gearNumber: Int, val minRPMS: Int, val maxSpeed: Int, val minSpeed: Int) : Car() {

    val speeds: HashMap<Int, Int>

    init {
        speeds = getSpeedsPrivate(redline, maxSpeed, minSpeed)
        println("[DEBUG] Loaded gear number $gearNumber")
    }

    protected open fun getSpeedsPrivate(redline: Int, maxSpeed: Int, minSpeed: Int): HashMap<Int, Int> {
        val speeds = HashMap<Int, Int>()
        val speedRange = maxSpeed - minSpeed
        for (i in speedRange..0) {
            speeds.put(maxSpeed - i, redline * (i / speedRange))
        }
        return speeds
    }
}