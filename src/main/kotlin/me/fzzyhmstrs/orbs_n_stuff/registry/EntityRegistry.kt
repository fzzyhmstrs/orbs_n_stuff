/*
 *
 *  Copyright (c) 2024 Fzzyhmstrs
 *
 *  This file is part of Orbs 'n' Stuff , a mod made for minecraft; as such it falls under the license of Orbs 'n' Stuff.
 *
 *  Orbs 'n' Stuff is free software provided under the terms of the Timefall Development License - Modified (TDL-M).
 *  You should have received a copy of the TDL-M with this software.
 *  If you did not, see <https://github.com/fzzyhmstrs/Timefall-Development-Licence-Modified>.
 *
 */

package me.fzzyhmstrs.orbs_n_stuff.registry

import me.fzzyhmstrs.orbs_n_stuff.ONS
import me.fzzyhmstrs.orbs_n_stuff.entity.OrbEntity
import me.fzzyhmstrs.orbs_n_stuff.entity.variant.OrbVariant
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import java.util.function.Supplier

object EntityRegistry {

    private fun <T: Entity> register(builder: EntityType.Builder<T>, name: String): EntityType<T> {
        return Registry.register(Registries.ENTITY_TYPE, ONS.identity(name), builder.build(null))
    }

    private val hpOrbRegister = register(EntityType.Builder.create({ t, w -> OrbEntity(t, w, OrbVariant.HP) }, SpawnGroup.MISC)
        .dimensions(0.25F, 0.25F)
        .eyeHeight(0.2125F)
        .maxTrackingRange(6)
        .trackingTickInterval(20), "hp")

    val HP_ORB: Supplier<EntityType<OrbEntity>> = Supplier { hpOrbRegister }

    private val xpOrbRegister = register(EntityType.Builder.create({ t, w -> OrbEntity(t, w, OrbVariant.XP) }, SpawnGroup.MISC)
        .dimensions(0.25F, 0.25F)
        .eyeHeight(0.2125F)
        .maxTrackingRange(6)
        .trackingTickInterval(20), "xp")

    val XP_ORB: Supplier<EntityType<OrbEntity>> = Supplier { xpOrbRegister }

    private val statusOrbRegister = register(EntityType.Builder.create({ t, w -> OrbEntity(t, w, OrbVariant.STATUS) }, SpawnGroup.MISC)
        .dimensions(0.25F, 0.25F)
        .eyeHeight(0.2125F)
        .maxTrackingRange(6)
        .trackingTickInterval(20), "status")

    val STATUS_ORB: Supplier<EntityType<OrbEntity>> = Supplier { statusOrbRegister }

    private val bossOrbRegister = register(EntityType.Builder.create({ t, w -> OrbEntity(t, w, OrbVariant.BOSS) }, SpawnGroup.MISC)
        .dimensions(0.25F, 0.25F)
        .eyeHeight(0.2125F)
        .maxTrackingRange(6)
        .trackingTickInterval(20), "boss")

    val BOSS_ORB: Supplier<EntityType<OrbEntity>> = Supplier { bossOrbRegister }

    fun init() {}
}