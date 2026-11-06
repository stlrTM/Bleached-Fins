package stellar.ether.api.registrate.builders

import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.attachment.IAttachmentHolder
import net.neoforged.neoforge.registries.NeoForgeRegistries
import stellar.ether.api.registrate.AbstractRegistrate
import stellar.ether.api.registrate.LazyEntry

class AttachmentBuilder<T,P>(
    owner: AbstractRegistrate<*>,
    parent: P,
    name: String,
    initial: () -> AttachmentType.Builder<T>
) : BuilderWithSettings<AttachmentType.Builder<T>, AttachmentType<*>, AttachmentType<T>, P, AttachmentBuilder<T,P>>(
    owner,
    parent,
    name,
    NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
    initial,
    { it.build() }
)

object AttachmentBuilders {

    fun <T,S : AbstractRegistrate<S>> AbstractRegistrate<S>.attachment(
        name: String,
        factory: (IAttachmentHolder) -> T,
        action: AttachmentBuilder<T, AbstractRegistrate<S>>.() -> Unit = {}
    ): LazyEntry<AttachmentType<*>, AttachmentType<T>> = attachment(
        name,
        factory,
        this,
        action
    )

    fun <T,P> AbstractRegistrate<*>.attachment(
        name: String,
        factory: (IAttachmentHolder) -> T,
        parent: P,
        action: AttachmentBuilder<T,P>.() -> Unit = {}
    ): LazyEntry<AttachmentType<*>, AttachmentType<T>> = action(AttachmentBuilder(
        this,
        parent,
        name
    ) { AttachmentType.builder(factory) },action)
}
