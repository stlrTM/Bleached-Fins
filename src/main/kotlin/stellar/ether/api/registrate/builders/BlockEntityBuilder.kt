package stellar.ether.api.registrate.builders

import net.minecraft.client.renderer.blockentity.*
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.*
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.registries.DeferredHolder
import stellar.ether.api.registrate.*

class BlockEntityBuilder<T : BlockEntity, P>(
    owner: AbstractRegistrate<*>,
    parent: P,
    name: String,
    val factory: BlockEntityType.BlockEntitySupplier<T>
) : AbstractBuilder<BlockEntityType<*>, BlockEntityType<T>, P, BlockEntityBuilder<T, P>>(
    owner,
    parent,
    name,
    Registries.BLOCK_ENTITY_TYPE
) {

    private val validBlocks = mutableSetOf<() -> Block>()
    private var renderer: (() -> (BlockEntityRendererProvider.Context) -> BlockEntityRenderer<in T>)? = null

    fun validBlock(block: () -> Block){
        validBlocks.add(block)
    }

    fun validBlocks(vararg blocks: () -> Block) {
        validBlocks.addAll(blocks)
    }

    fun renderer(
        renderer: () -> (BlockEntityRendererProvider.Context) -> BlockEntityRenderer<in T>
    ) {
        if (this.renderer == null) {
            whenDist(Dist.CLIENT) {
                registerRenderer()
            }
        }
        this.renderer = renderer
    }

    private fun registerRenderer() {
        addEventListener(FMLClientSetupEvent::class.java) {
            val currentRenderer = renderer
            if (currentRenderer != null) {
                BlockEntityRenderers.register(entry!!.get()) { context ->
                    currentRenderer()(context)
                }
            }
        }
    }

    override fun createEntry(): BlockEntityType<T> {
        return BlockEntityType(
            factory,
            validBlocks.map { it() }.toSet(),
            null
        )
    }

    override fun createEntryWrapper(
        holder: DeferredHolder<BlockEntityType<*>, BlockEntityType<T>>
    ): BlockEntityEntry<T> {
        return BlockEntityEntry(owner, holder.key!!)
    }
}

object BlockEntityBuilders {
    fun <T : BlockEntity,S : AbstractRegistrate<S>> AbstractRegistrate<S>.blockEntity(
        name: String,
        factory: BlockEntityType.BlockEntitySupplier<T>,
        action: BlockEntityBuilder<T, AbstractRegistrate<S>>.() -> Unit
    ): BlockEntityEntry<T> = blockEntity(
        name,
        factory,
        this,
        action
    )

    fun <T : BlockEntity,P> AbstractRegistrate<*>.blockEntity(
        name: String,
        factory: BlockEntityType.BlockEntitySupplier<T>,
        parent: P,
        action: BlockEntityBuilder<T, P>.() -> Unit
    ): BlockEntityEntry<T> = action(BlockEntityBuilder(this,parent,name,factory),action) as BlockEntityEntry<T>
}

class BlockEntityEntry<T : BlockEntity>(
    owner: AbstractRegistrate<*>,
    key: ResourceKey<BlockEntityType<*>>,
) : LazyEntry<BlockEntityType<*>, BlockEntityType<T>>(
    owner,
    key
) {
    fun create(pos: BlockPos,state: BlockState): T = get().create(pos,state)!!
}