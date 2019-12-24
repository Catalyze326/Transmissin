import kotlin.properties.Delegates

open class Car {
//    TODO have these values pull from json file

    //    Values for my car
    private var rpms = 1000
    private var speed: Int by Delegates.observable(0) { _, _, _ ->
        if (clutchPercent < 1) changePercentClutch(rpms.toDouble() / gears[currentGear].speeds[speed]!!)
    }

    private var gasPercent: Double by Delegates.observable(0.00) { _, _, _ ->
//        if (gasPercent > 0) {
//            hitGasShifting(newValue)
//        }
    }

    var eLimitedSpeed = 125
    var redline = 6750

    private val breakingValue = 20

    //  Gear 0 is neutural gear 7 is reverse
    private var gears = arrayOf(Neutral()
            , Gear(1, redline, 35, 0)
            , Gear(2, redline, 65, 8)
            , Gear(3, redline, 95, 25)
            , Gear(4, redline, 110, 35)
            , Gear(5, redline, 125, 45)
            , Gear(6, redline, 125, 45)
            , Gear(7, redline, -30, 0))

    private var clutchPercent = 1.00

    private var currentGear: Int by Delegates.observable(0) { _, _, newValue ->
        changePercentClutch(0.00)
        newValue.changeShifter()
        Thread {
            while (clutchPercent < 1) {
                changePercentClutch(rpms.toDouble() / gears[currentGear].speeds[speed]!!)
            }
        }.start()
    }

    private fun Int.changeShifter() {
        println("Changing gear to ${this}")
    }

    private fun changePercentClutch(percent: Double) {
        clutchPercent = percent
    }

    fun setRPMs(newRPMs: Int): Boolean {
        return if (rpms <= redline) {
            false
        } else {
            rpms = newRPMs
            true
        }
    }

    fun setSpeed(newSpeed: Int): Boolean {
        return if (newSpeed > eLimitedSpeed) {
            false
        } else {
            if (newSpeed > speed) {
//                Placeholder percent gas
                hitGasShifting(newSpeed)
                true
            } else {
//                placeholder percent break and target speed
                hitBrakesShifting(.3, newSpeed)
                true
            }
        }
    }

    //  TODO work out when to downshift to acceleration
    //  TODO work out when to wait longer at higher rpms. Maybe get custom rpm data for best fuel efficency vs max speed
    //  TODO make speed mode vs eco mode
    //  TODO add a thing for how far the gas is pushed down
    //  TODO find a way to calculate what the rpms are going to be for a gas level in a given gear
    private fun hitGasShifting(newSpeed: Int) {
        Thread {
            while (speed < newSpeed) {
                if (gasPercent > .3 && rpms < redline / 2) {
                    downshiftShortly()
                }
            }
        }
    }

    private fun downshiftShortly(){
        currentGear--
        if (gasPercent < .7) {
            gasPercent += .2
            while (rpms < (4 / 5) * redline) {
                Thread.sleep(10)
            }
            currentGear++
            gasPercent -= .2
        }
    }

    fun downshift(){
        currentGear--
        if (gasPercent < .7) {
            gasPercent += .05
            while (rpms < (4 / 5) * redline) {
                Thread.sleep(10)
            }
            currentGear++
            gasPercent -= .05
        }
    }

    private fun hitBrakesShifting(percentBrakes: Double, targetSpeed: Int) {
        Thread {
            while (speed > targetSpeed) {
                if (gears[currentGear].speeds[speed]!! < rpms) {
                    currentGear = 0
                }
            }
        }.start()
        while (speed > targetSpeed) {
            speed -= ((percentBrakes * breakingValue) / 100).toInt()
            Thread.sleep(10)
        }
    }

    // TODO get the percentage of break to hit in order to stop in time what I have is wrong
    fun comeToStop(distance: Int) {
        val fps = speed * 1.467
        hitBrakesShifting(((breakingValue / fps) * (fps / 2)) / distance, 0)
        currentGear = 0
    }
}