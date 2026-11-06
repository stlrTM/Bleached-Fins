package stellar.aquatic.client.handler

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Pose
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.lwjgl.glfw.GLFW
import stellar.aquatic.content.entity.swordfish.SwordfishEntity
import stellar.aquatic.content.network.C2SSwordfishBoostStop
import stellar.ether.api.rendering.lerp.Lerp
import stellar.ether.api.rendering.lerp.PIDResolver
import stellar.ether.api.rotate

@EventBusSubscriber(value = [Dist.CLIENT], modid = "ether_aquatic")
object SwordfishBoostHandler {
    val targetPos = Vec3(0.0, 0.25, 2.0)

    var fish: SwordfishEntity? = null
    var shouldJump: Boolean = false
    var rideTime: Int = 0

    private fun build(): Lerp {
        return Lerp(
            PIDResolver(
                .4f,
                .1f,
                .8f
            )
        )
    }

    var X: Lerp = build()
    var Y: Lerp = build()
    var Z: Lerp = build()

    fun resetLerp() {
        X = build()
        Y = build()
        Z = build()
    }

    @SubscribeEvent
    fun input(event: InputEvent.Key) {
        if (checkValid() && event.action == GLFW.GLFW_PRESS) {
            val sneakKeyCode = Minecraft.getInstance().options.keyShift.key.value
            if (event.key == sneakKeyCode) {
                stop()
            }
        }
    }

    fun stop() {
        val currentFish = fish
        if (currentFish != null) {
            PacketDistributor.sendToServer(
                C2SSwordfishBoostStop(currentFish.id)
            )
        }
        fish = null
        rideTime = 0
        shouldJump = false
        resetLerp()
        val player = Minecraft.getInstance().player ?: return
        player.forcedPose = null
    }

    @SubscribeEvent
    fun tick(event: ClientTickEvent.Pre) {
        val player = Minecraft.getInstance().player ?: return

        if (!checkValid()) {
            stop()
            return
        }

        val activeFish = fish ?: return

        val targetOffset = targetPos.rotate(
            Vec3(
                -player.xRot.toDouble(),
                -player.yRot.toDouble() - 180.0,
                0.0
            )
        )

        val targetPos = activeFish.position().add(targetOffset)
        val target = targetPos.subtract(player.getPosition(0f)).scale(0.25)

        if (shouldJump) {
            resetLerp()
            X.jumpTo(target.x.toFloat())
            Y.jumpTo(target.y.toFloat())
            Z.jumpTo(target.z.toFloat())
            shouldJump = false
        }

        X.target = target.x.toFloat()
        Y.target = target.y.toFloat()
        Z.target = target.z.toFloat()

        if (activeFish.isInWater || !activeFish.canDismountRider()) {
            rideTime++
            val delta = Minecraft.getInstance().timer.getGameTimeDeltaPartialTick(false)
            player.deltaMovement = player.deltaMovement
                .scale(0.75)
                .add(
                    Vec3(
                        X.update(delta).toDouble(),
                        Y.update(delta).toDouble(),
                        Z.update(delta).toDouble()
                    )
                )
        } else {
            stop()
        }
    }

    fun checkValid(): Boolean {
        val activeFish = fish ?: return false
        return activeFish.isAlive && !activeFish.isRemoved && !activeFish.canDismountRider() && rideTime <= 20 * 60 * 2
    }
}