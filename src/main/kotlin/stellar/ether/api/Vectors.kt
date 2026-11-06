package stellar.ether.api

import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3

fun Vec3.rotate(rotation: Vec3): Vec3 {
    return this.rotate(
        rotation.x,
        rotation.y,
        rotation.z
    )
}

fun Vec3.rotate(xRot: Double,yRot: Double,zRot: Double): Vec3 {
    return this.rotate(
        xRot,
        Direction.Axis.X
    ).rotate(
        yRot,
        Direction.Axis.Y
    ).rotate(
        zRot,
        Direction.Axis.Z
    )
}

fun Vec3.rotate(deg: Double,axis: Direction.Axis): Vec3 {
    if (deg == 0.0) return this
    if (this === Vec3.ZERO) return this

    val angle = (deg / 180f * Math.PI).toFloat()
    val sin = Mth.sin(angle).toDouble()
    val cos = Mth.cos(angle).toDouble()
    val x: Double = x
    val y: Double = y
    val z: Double = z

    if (axis === Direction.Axis.X) return Vec3(x, y * cos - z * sin, z * cos + y * sin)
    if (axis === Direction.Axis.Y) return Vec3(x * cos + z * sin, y, z * cos - x * sin)
    if (axis === Direction.Axis.Z) return Vec3(x * cos - y * sin, y * cos + x * sin, z)
    return this
}