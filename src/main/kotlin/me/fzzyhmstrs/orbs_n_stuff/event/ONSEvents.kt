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

package me.fzzyhmstrs.orbs_n_stuff.event

import me.fzzyhmstrs.orbs_n_stuff.ONS
import me.fzzyhmstrs.orbs_n_stuff.config.ONSConfig
import me.fzzyhmstrs.orbs_n_stuff.config.ONSDebugConfig
import me.fzzyhmstrs.orbs_n_stuff.entity.OrbEntity
import me.fzzyhmstrs.orbs_n_stuff.entity.variant.OrbVariant
import me.fzzyhmstrs.orbs_n_stuff.registry.EntityRegistry
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.minecraft.server.network.ServerPlayerEntity


object ONSEvents {

    fun init() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register { world, killer, killed ->
            if (killer !is ServerPlayerEntity) return@register
            if (ONSConfig.INSTANCE.willDropHp(killed) || ONSDebugConfig.INSTANCE.guaranteeHpDrops.get()) {
                val orb = OrbEntity(EntityRegistry.HP_ORB.get(), world, OrbVariant.HP)
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val vel = ONSConfig.INSTANCE.orbSpawnVelocity.get()
                val xDelta = (ONS.random().nextDouble() - 0.5) * vel
                val zDelta = (ONS.random().nextDouble() - 0.5) * vel
                orb.setVelocity(killed.velocity.x + xDelta, killed.velocity.y + vel, killed.velocity.z + zDelta)
                world.spawnEntity(orb)
            }
            if (ONSConfig.INSTANCE.willDropXp(killed) || ONSDebugConfig.INSTANCE.guaranteeXpDrops.get()) {
                val orb = OrbEntity(EntityRegistry.XP_ORB.get(), world, OrbVariant.XP)
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val vel = ONSConfig.INSTANCE.orbSpawnVelocity.get()
                val xDelta = (ONS.random().nextDouble() - 0.5) * vel
                val zDelta = (ONS.random().nextDouble() - 0.5) * vel
                orb.setVelocity(killed.velocity.x + xDelta, killed.velocity.y + vel, killed.velocity.z + zDelta)
                world.spawnEntity(orb)
            }
            if (ONSConfig.INSTANCE.willDropStatus(killed, killer) || ONSDebugConfig.INSTANCE.guaranteeStatusDrops.get()) {
                val orb = OrbEntity(EntityRegistry.STATUS_ORB.get(), world, OrbVariant.STATUS)
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val vel = ONSConfig.INSTANCE.orbSpawnVelocity.get()
                val xDelta = (ONS.random().nextDouble() - 0.5) * vel
                val zDelta = (ONS.random().nextDouble() - 0.5) * vel
                orb.setVelocity(killed.velocity.x + xDelta, killed.velocity.y + vel, killed.velocity.z + zDelta)
                world.spawnEntity(orb)
            }
            if (ONSConfig.INSTANCE.willDropBoss(killed, killer) || ONSDebugConfig.INSTANCE.guaranteeBossDrops.get()) {
                val orb = OrbEntity(EntityRegistry.BOSS_ORB.get(), world, OrbVariant.BOSS)
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val vel = ONSConfig.INSTANCE.orbSpawnVelocity.get()
                val xDelta = (ONS.random().nextDouble() - 0.5) * vel
                val zDelta = (ONS.random().nextDouble() - 0.5) * vel
                orb.setVelocity(killed.velocity.x + xDelta, killed.velocity.y + vel, killed.velocity.z + zDelta)
                world.spawnEntity(orb)
            }
        }
    }
}