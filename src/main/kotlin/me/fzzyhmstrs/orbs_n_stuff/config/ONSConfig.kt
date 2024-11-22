/*
 *
 *  Copyright (c) 2024 Fzzyhmstrs
 *
 *  This file is part of Orbs 'n' Stuff, a mod made for minecraft; as such it falls under the license of Orbs 'n' Stuff.
 *
 *  Orbs 'n' Stuff is free software provided under the terms of the Timefall Development License - Modified (TDL-M).
 *  You should have received a copy of the TDL-M with this software.
 *  If you did not, see <https://github.com/fzzyhmstrs/Timefall-Development-Licence-Modified>.
 *
 */

package me.fzzyhmstrs.orbs_n_stuff.config

import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier
import me.fzzyhmstrs.fzzy_config.validation.number.*
import me.fzzyhmstrs.orbs_n_stuff.ONS
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.Monster
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity

@IgnoreVisibility
class ONSConfig: Config(ONS.identity("config")) {

    companion object {
        val INSTANCE = ConfigApi.registerAndLoadConfig({ ONSConfig() })

        fun init(){}
    }

    private var orbOwnerTime = ValidatedInt(0, 72000, 0, ValidatedNumber.WidgetType.TEXTBOX)
    private var orbDespawnTime = ValidatedInt(6000, 72000, 0, ValidatedNumber.WidgetType.TEXTBOX)

    private var hpSettings = Hp()
    
    @IgnoreVisibility
    private class Hp: ConfigSection() {
        private var hpDropChance = ValidatedFloat(0.1f, 1f)
        private var hpBlacklist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
        private var hpWhitelist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
    }

    private var xpSettings = Xp()
    
    @IgnoreVisibility
    private class Xp: ConfigSection() {
        private var xpDropChance = ValidatedFloat(0.05f, 1f)
        private var xpBlacklist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
        private var xpWhitelist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
    }

    private var statusSettings = Status()
    
    @IgnoreVisibility
    private class Status: ConfigSection() {
        private var statusDropChance = ValidatedFloat(0.075f, 1f)
        private var statusAdvancements = ValidatedIdentifier(Identifier.of("minecraft", "story/enter_the_nether")).toSet()
        private var statusBlacklist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
        private var statusWhitelist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
    }

    private var bossSettings = Boss()

    @IgnoreVisibility
    private class Boss: ConfigSection() {
        private var bossKillCount = ValidatedInt(1, Int.MAX_VALUE, 0, ValidatedNumber.WidgetType.TEXTBOX)
        private var bossKillCountPerType = ValidatedIdentifierMap.Builder()
                .keyHandler(ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE))
                .valueHandler(ValidatedInt(1, Int.MAX_VALUE, 0, ValidatedNumber.WidgetType.TEXTBOX))
                .build()
        private var bossBlacklist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
        private var bossWhitelist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
    }

    private var globalBlacklist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
    private var globalWhitelist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
    
    fun willDropHp(entity: Entity): Boolean {
        if (ONS.random().nextFloat() > hpSettings.hpDropChance.get()) return false
        val typeId = EntityType.getId(entity.type)
        if (globalBlacklist.contains(typeId)) return false
        if (globalWhitelist.contains(typeId)) return true
        if (hpSettings.hpBlacklist.contains(typeId)) return false
        return entity is Monster || hpSettings.hpWhitelist.contains(typeId)
    }

    fun willDropXp(entity: Entity): Boolean {
        if (ONS.random().nextFloat() > xpSettings.xpDropChance.get()) return false
        val typeId = EntityType.getId(entity.type)
        if (globalBlacklist.contains(typeId)) return false
        if (globalWhitelist.contains(typeId)) return true
        if (xpSettings.xpBlacklist.contains(typeId)) return false
        return entity is Monster || xpSettings.xpWhitelist.contains(typeId)
    }

    fun willDropStatus(entity: Entity, playerEntity: ServerPlayerEntity): Boolean {
        if (ONS.random().nextFloat() > statusSettings.statusDropChance.get()) return false
        if (statusSettings.statusAdvancements.any { advancement ->
            val adv = player.serverWorld.server.advancementLoader.get(advancement)
            if (adv == null) {
                ONS.LOGGER.error("Advancement $advancement couldn't be found. Check contents of the Orbs 'n' Stuff 'statusAdvancements' config list")
                true
            } else {
                !player.advancementTracker.getProgress(adv).isDone()
            }
        }) return false
        val typeId = EntityType.getId(entity.type)
        if (globalBlacklist.contains(typeId)) return false
        if (globalWhitelist.contains(typeId)) return true
        if (statusSettings.statusBlacklist.contains(typeId)) return false
        return entity is Monster || statusSettings.statusWhitelist.contains(typeId)
    }

    private val BOSS_TAG = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("c", "bosses"))
    
    fun willDropBoss(entity: Entity, playerEntity: ServerPlayerEntity): Boolean {
        if (hasKilled(entity, playerEntity)) return false
        if (globalBlacklist.contains(typeId)) return false
        if (globalWhitelist.contains(typeId)) return true
        if (bossSettings.bossBlacklist.contains(typeId)) return false
        return entity.type.isIn(BOSS_TAG) || bossSettings.bossWhitelist.contains(typeId)
    }

    private fun hasKilled(entity: Entity, playerEntity: ServerPlayerEntity): Boolean {
        TODO()
    }
}
