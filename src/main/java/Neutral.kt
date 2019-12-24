/**
 * Max speed is the electronically limited speed
 */
class Neutral : Gear(0, 750, 125, 0) {

    override fun getSpeedsPrivate(redline: Int, maxSpeed: Int, minSpeed: Int): HashMap<Int, Int> {
        val speeds = HashMap<Int, Int>()
        for(i in 0..eLimitedSpeed){
            speeds[i] = minRPMS
        }
        return speeds
    }
}