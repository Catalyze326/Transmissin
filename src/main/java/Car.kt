import kotlin.properties.Delegates
import kotlin.system.exitProcess

class Car {
    //    TODO have these values pull from json file
//  TODO the readme
    private val eLimitedSpeed: Double
    private val minRPMs: Int
    private val redline: Int
    private val breakingValue = 20

    private var targetSpeed: Double by Delegates.observable(0.00) { _, _, _ ->
        accelerate()
    }

    private var clutchPercent = 1.00

    private var accelCoef: Double
    private var otherFactorsVar: Double

    init {
        accelCoef = 1.0
        eLimitedSpeed = 125.00
        minRPMs = 750
        redline = 6750
        otherFactorsVar = 1.0
        accelerate()
        Runtime.getRuntime().addShutdownHook(Thread {
            println("the new speed is $speed\n" +
                    "the rpms are $rpms \n" +
                    "the gear is $currentGear\n" +
                    "\n" +
                    "Goodbye World")
        })
    }

    //    Values for my car
    private var rpms: Double by Delegates.observable(1000.00) { _, _, newRPMs ->
        speed = gears[currentGear].rpms[newRPMs.toInt()]!!
        println("the new speed is $speed\nthe rpms are $rpms \nthe gear is $currentGear\n\n\n")
    }

    private var speed: Double by Delegates.observable(0.00) { _, _, newSpeed ->
        if (newSpeed > eLimitedSpeed) speed = eLimitedSpeed
        println("the new speed is $speed\nthe rpms are $rpms \nthe gear is $currentGear\n\n\n")
//        if (clutchPercent < 1) changePercentClutch(rpms.toDouble() / gears[currentGear].speeds[newSpeed]!!)
        hitGasShifting()
        if (newSpeed >= targetSpeed) exitProcess(1)
    }

    private var currentGear: Int by Delegates.observable(1) { _, _, newValue ->
        println("the new speed is $speed\nthe rpms are $rpms \nthe gear is $currentGear\n\n\n")
        setRPMs(gears[newValue].speeds[gears[newValue].minSpeed]!!)
        newValue.changeShifter()
    }

    private var gasPercent: Double by Delegates.observable(0.00) { _, _, newValue ->
        println("the new speed is $speed\nthe rpms are $rpms \nthe gear is $currentGear\n\n\n")
    }

    fun updateSpeed(newSpeed: Double) {
        Thread {
            speed = newSpeed
        }.start()
    }

    //  Gear 0 is neutural gear 7 is reverse
    var gears = arrayOf(
            Gear(0, eLimitedSpeed.toInt(), 0, true, eLimitedSpeed, minRPMs, redline),
            Gear(1, 35, 0, false, eLimitedSpeed, minRPMs, redline),
            Gear(2, 65, 8, false, eLimitedSpeed, minRPMs, redline),
            Gear(3, 95, 30, false, eLimitedSpeed, minRPMs, redline),
            Gear(4, 110, 45, false, eLimitedSpeed, minRPMs, redline),
            Gear(5, 125, 60, false, eLimitedSpeed, minRPMs, redline),
            Gear(6, 155, 85, false, eLimitedSpeed, minRPMs, redline),
            Gear(-1, -30, 0, false, eLimitedSpeed, minRPMs, redline))

    private fun Int.changeShifter() {
        println("Changing gear to $this the rpms are at $rpms and the speed is $speed")
    }

    private fun changePercentClutch(percent: Double) {
        clutchPercent = percent
    }

    private fun setRPMs(newRPMs: Double) {
        rpms = if (newRPMs <= redline) newRPMs else redline - 500.00
    }

    fun setSpeed(newSpeed: Double): Boolean {
        targetSpeed = if (newSpeed < eLimitedSpeed) newSpeed else eLimitedSpeed
        return true
    }

    //  TODO work out when to downshift to acceleration
    //  TODO work out when to wait longer at higher rpms. Maybe get custom rpm data for best fuel efficency vs max speed
    //  TODO make speed mode vs eco mode
    //  TODO add a thing for how far the gas is pushed down
    //  TODO find a way to calculate what the rpms are going to be for a gas level in a given gear
    private fun hitGasShifting() {
        if (currentGear != 6) {
            if (gears[currentGear].maxSpeed < speed)
                currentGear++
            else if (gears[currentGear].maxSpeed < speed)
                currentGear--
            if (rpms > redline - 1000)
                currentGear++
        }
    }


    private fun accelerate() {
        Thread {
            while (true) {
                while (targetSpeed > speed) {
                    setRPMs(rpms + (redline * otherFactorsVar * accelCoef))
                }
                while (targetSpeed < speed) {
                    setRPMs(rpms - (redline * otherFactorsVar * accelCoef))
                }
            }
        }.start()
    }

    // TODO get the percentage of break to hit in order to stop in time what I have is wrong
    fun comeToStop(distance: Int) {
        val fps = speed * 1.467
        currentGear = 0
    }
}