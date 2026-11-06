package stellar.ether.api.rendering.lerp

import kotlin.math.pow

class ExponentialResolver(
    val speed: Float = 0.05f
) : LerpResolver {
    override fun resolve(lerp: Lerp, delta: Float) {
        val distance: Float = lerp.target - lerp.value

        lerp.value += (distance * (1 - (1 - this.speed).pow(delta)))

        lerp.value += (lerp.velocity * delta)
        lerp.velocity *= 0.5f.pow(delta)
    }
}