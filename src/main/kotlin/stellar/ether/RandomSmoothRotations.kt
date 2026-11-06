//package stellar.ether
//
//import net.minecraft.util.*
//import net.minecraft.world.phys.Vec3
//import org.joml.Quaternionf
//import kotlin.math.*
//
//object RandomSmoothRotations {
//
//    private fun randomVec(random: RandomSource): Vec3 {
//        while (true) {
//            val x = Mth.nextFloat(random, -1f, 1f)
//            val y = Mth.nextFloat(random, -1f, 1f)
//            val z = Mth.nextFloat(random, -1f, 1f)
//            val magSq = x * x + y * y + z * z
//            if (magSq in 0.0001..1.0) {
//                return Vec3(x.toDouble(), y.toDouble(), z.toDouble()).normalize()
//            }
//        }
//    }
//
//    private fun getSmoothCycles(time: Double, period: Float): Double {
//        return sin(2.0 * PI * (time / period))
//    }
//
//    fun getRandomSmoothRotation(seed: Long, time: Double, speed: Float = 1.0f): Quaternionf {
//        val random = RandomSource.create(seed)
//        val rotation = Quaternionf()
//
//        for (i in 0 until 6) {
//            val period = Mth.nextFloat(random, 30f, 60f)
//            val cycles = getSmoothCycles(time * speed, period)
//            val angleRadians = Math.toRadians(cycles * 360.0).toFloat()
//
//            val axis = randomVec(random)
//
//            val stepRotation = Quaternionf().rotationAxis(
//                angleRadians,
//                axis.x.toFloat(),
//                axis.y.toFloat(),
//                axis.z.toFloat()
//            )
//
//            rotation.mul(stepRotation)
//        }
//
//        return rotation
//    }
//}