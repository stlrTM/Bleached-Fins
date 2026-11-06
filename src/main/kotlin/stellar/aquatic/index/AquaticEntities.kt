package stellar.aquatic.index

import com.mojang.logging.LogUtils
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.*
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.animal.AbstractFish
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.storage.loot.*
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.providers.number.*
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent
import net.neoforged.neoforge.registries.NeoForgeRegistries
import stellar.aquatic.Aquatic
import stellar.aquatic.Aquatic.registrate
import stellar.aquatic.client.renderer.entity.piranha.*
import stellar.aquatic.client.renderer.entity.sturgeon.SturgeonRenderer
import stellar.aquatic.client.renderer.entity.sturgeon.model.*
import stellar.aquatic.client.renderer.entity.swordfish.SwordfishRenderer
import stellar.aquatic.client.renderer.entity.swordfish.model.SwordfishModel
import stellar.aquatic.content.entity.piranha.PiranhaEntity
import stellar.aquatic.content.entity.piranha.arrow.PiranhaArrowEntity
import stellar.aquatic.content.entity.sturgeon.SturgeonEntity
import stellar.aquatic.content.entity.swordfish.SwordfishEntity
import stellar.aquatic.loc
import stellar.ether.api.Loadable
import stellar.ether.api.registrate.builders.EntityBuilders.entity
import stellar.ether.api.registrate.datagen.Providers
import stellar.ether.api.registrate.datagen.providers.*

fun Entity.positionVelocity(): Vec3 = position().subtract(xo, yo, zo)

object AquaticEntities : Loadable {

    val STURGEON_BIOMES: TagKey<Biome> = TagKey.create(
        Registries.BIOME,
        loc("has_spawn/sturgeon")
    )

    val SWORDFISH_BIOMES: TagKey<Biome> = TagKey.create(
        Registries.BIOME,
        loc("has_spawn/swordfish")
    )

    val PIRANHA_BIOMES: TagKey<Biome> = TagKey.create(
        Registries.BIOME,
        loc("has_spawn/piranha")
    )

    fun BootstrapContext<BiomeModifier>.addEntitySpawns(
        datagen: Datapack,
        id: String,
        data: SpawnerData,
        biomeTag: TagKey<Biome>
    ) {
        val holders = lookup(Registries.BIOME)
        register(
            ResourceKey.create(
                NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                ResourceLocation.fromNamespaceAndPath(
                    datagen.ctx.parent.modId,
                    id
                )
            ),
            AddSpawnsBiomeModifier(
                holders.getOrThrow(biomeTag),
                listOf(data)
            )
        )
    }

    fun BootstrapContext<BiomeModifier>.addEntitySpawns(
        datagen: Datapack,
        id: String,
        data: SpawnerData,
        tagLocation: ResourceLocation
    ) {
        addEntitySpawns(datagen, id, data, TagKey.create(Registries.BIOME, tagLocation))
    }

    val sturgeon = registrate.entity(
        "sturgeon",
        ::SturgeonEntity,
        MobCategory.WATER_CREATURE
    ) {
        layer(SturgeonEntityModel::LAYER_LOCATION, SturgeonEntityModel::createBodyLayer)
        layer(BabySturgeonEntityModel::LAYER_LOCATION, BabySturgeonEntityModel::createBodyLayer)

        spawnPlacement(
            SpawnPlacementTypes.IN_WATER,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            { ent, a, b, c, d -> a.getBlockState(c).`is`(Blocks.WATER) },
            RegisterSpawnPlacementsEvent.Operation.OR
        )

        renderer {
            { ctx ->
                SturgeonRenderer(
                    ctx,
                    SturgeonEntityModel(ctx.bakeLayer(SturgeonEntityModel.LAYER_LOCATION)),
                    BabySturgeonEntityModel(ctx.bakeLayer(BabySturgeonEntityModel.LAYER_LOCATION))
                )
            }
        }

        properties {
            sized(0.7F, 0.4F)
            eyeHeight(0.26F)
            clientTrackingRange(4)
        }

        attributes {
            AbstractFish.createAttributes()
        }

        spawnEgg(
            0x5a6139,
            0x9c8c58,
        ) {
            tab(Aquatic.tab)
            addData(Providers.LANG) { ctx ->
                add(ctx(), "Sturgeon Spawn Egg")
            }
        }

        addData(Providers.LANG) { ctx ->
            add(ctx(), "Sturgeon")
        }

        addData(Providers.LOOT) { ctx ->
            addLootAction(Loot.LootType.ENTITY) {
                add(
                    ctx(),
                    LootTable.lootTable()
                        .withPool(
                            LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1f))
                                .add(
                                    LootItem.lootTableItem(AquaticItems.sturgeonItem)
                                )
                        ).withPool(
                            LootPool.lootPool()
                                .setRolls(UniformGenerator.between(0f, 1f))
                                .add(
                                    LootItem.lootTableItem(AquaticItems.sturgeonCaviarItem).apply(
                                        SetItemCountFunction.setCount(
                                            UniformGenerator.between(0f, 1f)
                                        )
                                    )
                                )
                        )
                )
            }
        }

        addData(Providers.BIOME_TAGS) { ctx ->
            addOptional(
                STURGEON_BIOMES,
                *SturgeonEntity.biomesToVariant.keys.flatten().toTypedArray()
            )
        }

        addData(Providers.DATAPACK) { ctx ->
            add(
                NeoForgeRegistries.Keys.BIOME_MODIFIERS
            ) { boot ->
                boot.addEntitySpawns(
                    this@addData,
                    "sturgeon",
                    SpawnerData(
                        ctx(),
                        100,
                        1,
                        3
                    ),
                    STURGEON_BIOMES
                )
            }
        }
    }

    val swordfish = registrate.entity(
        "swordfish",
        ::SwordfishEntity,
        MobCategory.WATER_CREATURE
    ) {
        layer(SwordfishModel::LAYER_LOCATION, SwordfishModel::createBodyLayer)

        renderer {
            ::SwordfishRenderer
        }

        properties {
            sized(1.2f, 0.7f)
            eyeHeight(0.26F)
            clientTrackingRange(4)
        }

        attributes {
            AbstractFish.createAttributes()
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 1.1)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1.0)
        }

        spawnEgg(
            0x2f3c43,
            0xcad1df,
        ) {
            tab(Aquatic.tab)
            addData(Providers.LANG) { ctx ->
                add(ctx(), "Swordfish Spawn Egg")
            }
        }

        spawnPlacement(
            SpawnPlacementTypes.IN_WATER,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            { ent, a, b, c, d -> a.getBlockState(c).`is`(Blocks.WATER) },
            RegisterSpawnPlacementsEvent.Operation.OR
        )

        addData(Providers.LANG) { ctx ->
            add(ctx(), "Swordfish")
        }

        addData(Providers.LOOT) { ctx ->
            addLootAction(Loot.LootType.ENTITY) {
                add(
                    ctx(),
                    LootTable.lootTable()
                        .withPool(
                            LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1f))
                                .add(LootItem.lootTableItem(AquaticItems.swordfish))
                        )
                )
            }
        }

        addData(Providers.BIOME_TAGS) { ctx ->
            addOptional(
                SWORDFISH_BIOMES,
                "minecraft:frozen_ocean",
                "minecraft:ocean",
                "minecraft:cold_ocean",
                "minecraft:deep_frozen_ocean",
                "minecraft:deep_cold_ocean",
                "minecraft:deep_ocean"
            )
        }

        addData(Providers.DATAPACK) { ctx ->
            add(
                NeoForgeRegistries.Keys.BIOME_MODIFIERS
            ) { boot ->
                boot.addEntitySpawns(
                    this@addData,
                    "swordfish",
                    SpawnerData(
                        ctx(),
                        100,
                        1,
                        2
                    ),
                    SWORDFISH_BIOMES
                )
            }
        }
    }

    val piranha = registrate.entity(
        "piranha",
        ::PiranhaEntity,
        MobCategory.WATER_CREATURE
    ) {
        layer(PiranhaEntityModel::LAYER_LOCATION, PiranhaEntityModel::createBodyLayer)
        renderer {
            ::PiranhaRenderer
        }
        attributes {
            Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 1.1)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1.0)
        }
        spawnPlacement(
            SpawnPlacementTypes.IN_WATER,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            { ent, a, b, c, d -> a.getBlockState(c).`is`(Blocks.WATER) },
            RegisterSpawnPlacementsEvent.Operation.OR
        )
        properties {
            sized(0.6f, 0.3f)
            eyeHeight(0.26f)
            clientTrackingRange(4)
        }
        addData(Providers.LANG) { ctx ->
            add(ctx(), "Piranha")
        }
        addData(Providers.LOOT) { ctx ->
            addLootAction(Loot.LootType.ENTITY) {
                add(
                    ctx(), LootTable.lootTable()
                        .withPool(
                            LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1f))
                                .add(
                                    LootItem.lootTableItem(AquaticItems.piranha.get())
                                )
                        )
                )
            }
        }

        addData(Providers.BIOME_TAGS) { ctx ->
            addOptional(
                PIRANHA_BIOMES,
                "minecraft:jungle",
                "minecraft:mangrove_swamp",
                "nomansland:bayou",
                "nomansland:lush_river"
            )
        }

        addData(Providers.DATAPACK) { ctx ->
            add(
                NeoForgeRegistries.Keys.BIOME_MODIFIERS
            ) { boot ->
                boot.addEntitySpawns(
                    this@addData,
                    "piranha",
                    SpawnerData(
                        ctx(),
                        100,
                        1,
                        2
                    ),
                    PIRANHA_BIOMES
                )
            }
        }

        spawnEgg(
            0x96e4df,
            0xd25783,
        ) {
            tab(Aquatic.tab)
            addData(Providers.LANG) { ctx ->
                add(ctx(), "Piranha Spawn Egg")
            }
        }
    }

    val piranhaArrow = registrate.entity(
        "piranha_arrow",
        ::PiranhaArrowEntity,
        MobCategory.MISC
    ) {
        layer(PiranhaArrowModel::LAYER_LOCATION, PiranhaArrowModel::createBodyLayer)
        renderer {
            ::PiranhaArrowRenderer
        }
        addData(Providers.LANG) { ctx ->
            add(ctx(), "Piranha Arrow")
        }
        properties {
            sized(0.5F, 0.5F)
            eyeHeight(0.13F)
            clientTrackingRange(4)
            updateInterval(20)
        }
    }


}