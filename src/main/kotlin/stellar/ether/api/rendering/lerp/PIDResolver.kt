package stellar.ether.api.rendering.lerp

class PIDResolver(
    var kp: Float,
    var ki: Float,
    var kd: Float,
    var maxIntegral: Float = 50f,
    var maxOutput: Float = 0f,
    var errorSum: Float = 0f,
    var lastError: Float = 0f,
    var hasPriorError: Boolean = false
) : LerpResolver {

    override fun resolve(lerp: Lerp, delta: Float) {
        if (delta <= 0f) return

        val error = lerp.target - lerp.value

        val pTerm = this.kp * error

        this.errorSum += error * delta
        if (this.maxIntegral != 0f) {
            this.errorSum = this.errorSum.coerceIn(-this.maxIntegral, this.maxIntegral)
        }
        val iTerm = this.ki * this.errorSum

        var dTerm = 0f
        if (this.hasPriorError) {
            val errorDerivative = (error - this.lastError) / delta
            dTerm = this.kd * errorDerivative
        } else {
            this.hasPriorError = true
        }
        this.lastError = error

        var output = pTerm + iTerm + dTerm
        if (this.maxOutput != 0f) {
            output = output.coerceIn(-this.maxOutput, this.maxOutput)
        }

        lerp.velocity = output
        lerp.value += lerp.velocity * delta
    }
}