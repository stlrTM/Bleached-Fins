package stellar.ether.api.rendering.lerp

interface LerpResolver {
    fun resolve(lerp: Lerp,delta: Float)
}

data class Lerp(
    val resolver: LerpResolver,
    var value: Float = 0f,
    var target: Float = 0f,
    var velocity: Float = 0f
) {
    fun update(delta: Float,target: Float = this.target): Float {
        this.target = target
        resolver.resolve(this,delta)
        return value
    }

    fun jumpTo(target: Float) {
        value = target
        this.target = target
        velocity = 0f
    }
}