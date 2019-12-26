import kotlin.properties.Delegates

class Car {
//    TODO have these values pull from json file

    //    Values for my car
    private var rpms: Int by Delegates.observable(1000) { _, _, newRPMs ->
        println("The new rpms are $newRPMs")
        speed = gears[currentGear].rpms[newRPMs]!!.also(::println)
    }

    private var speed: Int by Delegates.observable(0) { _, _, newSpeed ->
        println("the new speed is $newSpeed")
        if (clutchPercent < 1) changePercentClutch(rpms.toDouble() / gears[currentGear].speeds[newSpeed]!!)
        hitGasShifting()
        if (newSpeed >= targetSpeed) {
            Runtime.getRuntime().exit(1)
        }
    }

    private var gasPercent: Double by Delegates.observable(0.00) { _, _, newValue ->
    }

    init {
        accelerate()
    }

    val eLimitedSpeed = 125
    val minRPMs = 750
    val redline = 6750
    private var targetSpeed: Int = 0

    private val breakingValue = 20

    //  Gear 0 is neutural gear 7 is reverse
    var gears = arrayOf(
            Gear(0, eLimitedSpeed, 0, true, eLimitedSpeed, minRPMs, redline),
            Gear(1, 35, 0, false, eLimitedSpeed, minRPMs, redline),
            Gear(2, 65, 8, false, eLimitedSpeed, minRPMs, redline),
            Gear(3, 95, 25, false, eLimitedSpeed, minRPMs, redline),
            Gear(4, 110, 35, false, eLimitedSpeed, minRPMs, redline),
            Gear(5, 125, 45, false, eLimitedSpeed, minRPMs, redline),
            Gear(6, 125, 55, false, eLimitedSpeed, minRPMs, redline),
            Gear(-1, -30, 0, false, eLimitedSpeed, minRPMs, redline))

    private var clutchPercent = 1.00

    private var currentGear: Int by Delegates.observable(1) { _, _, newValue ->
        rpms = 1000
        gasPercent = 0.00
//        changePercentClutch(0.00)
        newValue.changeShifter()
//        Thread.sleep(100)
//        TODO uncomment this after you get reving working
//        Thread {
//            while (clutchPercent < 1) {
//                clutchPercent = (gears[currentGear].speeds[speed]!! / rpms.toDouble()).also(::println)
//            }
//        }.start()
    }

    private fun Int.changeShifter() {
        println("Changing gear to $this the rpms are at $rpms and the speed is $speed")
    }

    private fun changePercentClutch(percent: Double) {
        clutchPercent = percent
    }

    private fun setRPMs(newRPMs: Int): Boolean {
        return if (newRPMs >= redline) {
            println("setRPMs was asked to set the rpms to $newRPMs which is above the redline of $redline")
            false
        } else {
            println("the rpms are $rpms")
            rpms = newRPMs
            true
        }
    }

    fun setSpeed(newSpeed: Int): Boolean {
        targetSpeed = newSpeed
        return if (newSpeed > eLimitedSpeed) {
            false
        } else {
            if (newSpeed > speed) {
//                Placeholder percent gas
                Thread {
                    while (newSpeed > speed) {
                        if (gasPercent < 0.99) gasPercent += .01
                        Thread.sleep(100)
                    }
                }.start()
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
    private fun hitGasShifting() {
        if (currentGear != 6) {
            if (gears[currentGear].minSpeed < speed)
                currentGear++
            if (rpms > redline - 500)
                currentGear++
        }
    }

    private fun downshiftShortly() {
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

    fun downshift() {
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

    fun accelerate() {
        Thread {
            while (true) {
                if (speed != targetSpeed && gasPercent > neededToAcclerate()) {
                    println("${neededToAcclerate()},     $gasPercent")
                    setRPMs(((((gasPercent - neededToAcclerate()) * redline)) + 750).toInt().also(::println))
                    Thread.sleep(10)
                }
            }
        }.start()
    }

    fun neededToAcclerate(incline: Double = 1.00): Double {
        return when (currentGear) {
            -1 -> (((gears[currentGear].maxSpeed / (speed + 1)) * (redline / rpms + 1)) * 1.0 * incline) / 3000
            1 -> (((gears[currentGear].maxSpeed / (speed + 1)) * (redline / rpms + 1)) * 1.0 * incline) / 3000
            2 -> (((gears[currentGear].maxSpeed / (speed + 1)) * (redline / rpms + 1)) * 1.3 * incline) / 3000
            3 -> (((gears[currentGear].maxSpeed / (speed + 1)) * (redline / rpms + 1)) * 1.6 * incline) / 3000
            4 -> (((gears[currentGear].maxSpeed / (speed + 1)) * (redline / rpms + 1)) * 2.7 * incline) / 3000
            5 -> (((gears[currentGear].maxSpeed / (speed + 1)) * (redline / rpms + 1)) * 3.9 * incline) / 3000
            6 -> (((gears[currentGear].maxSpeed / (speed + 1)) * (redline / rpms + 1)) * 7.0 * incline) / 3000
            else -> 0.00
        }
    }

    // TODO get the percentage of break to hit in order to stop in time what I have is wrong
    fun comeToStop(distance: Int) {
        val fps = speed * 1.467
        hitBrakesShifting(((breakingValue / fps) * (fps / 2)) / distance, 0)
        currentGear = 0
    }
}