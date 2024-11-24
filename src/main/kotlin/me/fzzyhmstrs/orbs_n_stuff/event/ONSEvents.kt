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
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents


object ONSEvents {

    fun init() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register { world, killer, killed ->
            if (killer !is ServerPlayerEntity) return@register
            val looting = killer.registryManager.getOptional(RegistryKeys.ENCHANTMENT).map { reg ->
                reg.getEntry(Enchantments.LOOTING).map { enchant ->
                    EnchantmentHelper.getEquipmentLevel(enchant, killer)
                }.orElse(0)
            }.orElse(0)
            var bl = false
            if (ONSConfig.INSTANCE.willDropHp(killed, killer, looting) || ONSDebugConfig.INSTANCE.guaranteeHpDrops.get()) {
                val orb = OrbEntity(EntityRegistry.HP_ORB.get(), world, OrbVariant.HP)
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/1.5f).toDouble(), killed.pos.z)
                val vel = ONSConfig.INSTANCE.orbSpawnVelocity.get()
                val vel2 = vel * 1.5
                val xDelta = (ONS.random().nextDouble() - 0.5) * vel2
                val zDelta = (ONS.random().nextDouble() - 0.5) * vel2
                orb.setVelocity(xDelta, vel, zDelta)
                world.spawnEntity(orb)
                bl = true
            }
            if (ONSConfig.INSTANCE.willDropXp(killed, killer, looting) || ONSDebugConfig.INSTANCE.guaranteeXpDrops.get()) {
                val orb = OrbEntity(EntityRegistry.XP_ORB.get(), world, OrbVariant.XP)
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val vel = ONSConfig.INSTANCE.orbSpawnVelocity.get()
                val vel2 = vel * 1.5
                val xDelta = (ONS.random().nextDouble() - 0.5) * vel2
                val zDelta = (ONS.random().nextDouble() - 0.5) * vel2
                orb.setVelocity(xDelta, vel, zDelta)
                world.spawnEntity(orb)
                bl = true
            }
            if (ONSConfig.INSTANCE.willDropStatus(killed, killer, looting) || ONSDebugConfig.INSTANCE.guaranteeStatusDrops.get()) {
                val orb = OrbEntity(EntityRegistry.STATUS_ORB.get(), world, OrbVariant.STATUS)
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val vel = ONSConfig.INSTANCE.orbSpawnVelocity.get()
                val vel2 = vel * 1.5
                val xDelta = (ONS.random().nextDouble() - 0.5) * vel2
                val zDelta = (ONS.random().nextDouble() - 0.5) * vel2
                orb.setVelocity(xDelta, vel, zDelta)
                world.spawnEntity(orb)
                bl = true
            }
            if (ONSConfig.INSTANCE.willDropBoss(killed, killer, looting) || ONSDebugConfig.INSTANCE.guaranteeBossDrops.get()) {
                val orb = OrbEntity(EntityRegistry.BOSS_ORB.get(), world, OrbVariant.BOSS)
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val vel = ONSConfig.INSTANCE.orbSpawnVelocity.get()
                val vel2 = vel * 1.5
                val xDelta = (ONS.random().nextDouble() - 0.5) * vel2
                val zDelta = (ONS.random().nextDouble() - 0.5) * vel2
                orb.setVelocity(xDelta, vel, zDelta)
                world.spawnEntity(orb)
                bl = true
            }
            if (bl) {
                world.playSound(null, killed.x, killed.y, killed.z, SoundEvents.BLOCK_SNIFFER_EGG_PLOP, SoundCategory.PLAYERS, 0.5f, (killer.random.nextFloat() - killer.random.nextFloat()) * 1.4f + 2.0f)
            }
        }
    }
}