package stellar.ether.api.registrate.builders

import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.*
import net.minecraft.world.level.ItemLike
import stellar.ether.api.registrate.AbstractRegistrate
import stellar.ether.api.registrate.LazyEntry

open class ItemLikeEntry<R : ItemLike, T : R>(
    owner: AbstractRegistrate<*>,
    key: ResourceKey<R>
) : LazyEntry<R, T>(owner,key), ItemLike {
    fun create(): ItemStack = ItemStack(asItem())
    fun create(amount: Int): ItemStack = ItemStack(asItem(),amount)

    fun isSame(other: Item): Boolean = asItem() == other
    fun isSame(other: ItemStack): Boolean = asItem() == other.item

    override fun asItem(): Item = get().asItem()
}