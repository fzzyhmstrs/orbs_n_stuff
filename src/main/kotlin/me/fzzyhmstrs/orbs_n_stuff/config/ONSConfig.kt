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
import me.fzzyhmstrs.fzzy_config.annotations.NonSync
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.util.FcText
import me.fzzyhmstrs.fzzy_config.util.FcText.translate
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedIdentifierMap
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import me.fzzyhmstrs.orbs_n_stuff.ONS
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.Stats
import net.minecraft.util.Identifier
import kotlin.math.exp

@IgnoreVisibility
class ONSConfig: Config(ONS.identity("config")) {

    companion object {
        val INSTANCE = ConfigApi.registerAndLoadConfig({ ONSConfig() })

        fun init(){}
    }

    var orbOwnerTime = ValidatedInt(0, 72000, 0, ValidatedNumber.WidgetType.TEXTBOX)
    var orbDespawnTime = ValidatedInt(6000, 72000, -1, ValidatedNumber.WidgetType.TEXTBOX)
    var orbSpawnVelocity = ValidatedDouble(0.15, 0.5, 0.0, ValidatedNumber.WidgetType.TEXTBOX)


    private var hpSettings = Hp()

    class Hp: ConfigSection() {
        var hpDropChance = ValidatedFloat(0.15f, 1f, 0f, ValidatedNumber.WidgetType.TEXTBOX)
        var lowHpChanceBoost = ValidatedBoolean(true)
        var lowHpChanceFraction = ValidatedFloat(0.3f, 1f, 0f)
            .toCondition(lowHpChanceBoost) { 0.3f }
            .withFailTitle("orbs_n_stuff.config.hpSettings.lowHpChanceFraction.fail".translate())
        var lootingChanceBoost = ValidatedFloat(0.025f, 0.25f, 0f, ValidatedNumber.WidgetType.TEXTBOX)
        var hpBlacklist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
        var hpWhitelist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
    }

    private var xpSettings = Xp()

    class Xp: ConfigSection() {
        var xpDropChance = ValidatedFloat(0.05f, 1f, 0f, ValidatedNumber.WidgetType.TEXTBOX)
        var uniqueKillsChanceBoost = ValidatedBoolean(true)
        var lootingChanceBoost = ValidatedFloat(0.025f, 0.25f, 0f, ValidatedNumber.WidgetType.TEXTBOX)
        var xpBlacklist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
        var xpWhitelist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
    }

    private var statusSettings = Status()

    class Status: ConfigSection() {
        var statusDropChance = ValidatedFloat(0.075f, 1f, 0f, ValidatedNumber.WidgetType.TEXTBOX)
        var statusAdvancements = ValidatedIdentifier().toSet(Identifier.of("minecraft", "story/enter_the_nether"))
        var killedStatusCountBoost = ValidatedBoolean(true)
        var perStatusBoostAmount = ValidatedFloat(0.025f, 0.1f, 0f, ValidatedNumber.WidgetType.TEXTBOX)
            .toCondition(killedStatusCountBoost) { 0f }
            .withFailTitle("orbs_n_stuff.config.hpSettings.lowHpChanceFraction.fail".translate())
        var lootingChanceBoost = ValidatedFloat(0.025f, 0.25f, 0f, ValidatedNumber.WidgetType.TEXTBOX)
        var statusBlacklist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
        var statusWhitelist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
    }

    private var bossSettings = Boss()

    private class Boss: ConfigSection() {
        var bossKillCount = ValidatedInt(1, Int.MAX_VALUE, 0, ValidatedNumber.WidgetType.TEXTBOX)
        var bossKillCountPerType = ValidatedIdentifierMap.Builder<Int>()
                .keyHandler(ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE))
                .valueHandler(ValidatedInt(1, Int.MAX_VALUE, 0, ValidatedNumber.WidgetType.TEXTBOX))
                .build()
        var bossBlacklist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
        var bossWhitelist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()

        fun getBossKillCount(entity: Entity): Int {
            return bossKillCountPerType[EntityType.getId(entity.type)] ?: bossKillCount.get()
        }
    }

    private var globalBlacklist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()
    private var globalWhitelist = ValidatedIdentifier.ofRegistryKey(RegistryKeys.ENTITY_TYPE).toSet()

    @NonSync
    var clientSettings = Client()

    class Client: ConfigSection() {
        @NonSync
        var showOrbBeams = ValidatedBoolean(true)
            .toCondition({ particleCount.get() > 0 }, FcText.translatable("orbs_n_stuff.config.clientSettings.showOrbBeams.condition1"), { false })
            .withFailTitle(FcText.translatable("orbs_n_stuff.config.clientSettings.showOrbBeams.fail"))
        @NonSync
        var beamHeight = ValidatedDouble(0.8, 2.0, 0.0)
        @NonSync
        var beamOffset = ValidatedDouble(0.1, 0.25, 0.0)
        @NonSync
        var renderMultiplier = ValidatedFloat(1f, 8f, 0.5f)
        @NonSync
        var particleCount = ValidatedInt(1, 5, 0)
    }

    fun willDropHp(entity: Entity, playerEntity: ServerPlayerEntity, looting: Int): Boolean {
        var chance = if (playerEntity.health <= 1f) {
            (if(hpSettings.lowHpChanceBoost.get()) 4f else 1f) *
                    (hpSettings.hpDropChance.get() + (looting * hpSettings.lootingChanceBoost.get()))
        } else if (playerEntity.health / playerEntity.maxHealth <= hpSettings.lowHpChanceFraction.get()) {
            (if(hpSettings.lowHpChanceBoost.get()) 2f else 1f) *
                    (hpSettings.hpDropChance.get() + (looting * hpSettings.lootingChanceBoost.get()))
        } else {
            (hpSettings.hpDropChance.get() + (looting * hpSettings.lootingChanceBoost.get()))
        }
        chance = chance.coerceIn(0f, 1f)
        if (ONS.random().nextFloat() > chance) return false
        val typeId = EntityType.getId(entity.type)
        if (globalBlacklist.contains(typeId)) return false
        if (globalWhitelist.contains(typeId)) return true
        if (hpSettings.hpBlacklist.contains(typeId)) return false
        return entity is Monster || hpSettings.hpWhitelist.contains(typeId)
    }

    fun willDropXp(entity: Entity, playerEntity: ServerPlayerEntity, looting: Int): Boolean {
        var chance = if (xpSettings.uniqueKillsChanceBoost.get()) {
            val stat = Stats.KILLED.getOrCreateStat(entity.type)
            val kills = playerEntity.statHandler.getStat(stat) - 1
            val multi = 4f - (4 / (1 + exp(-0.2f * kills.toFloat())))
            multi * (xpSettings.xpDropChance.get() + (looting * xpSettings.lootingChanceBoost.get()))
        } else {
            xpSettings.xpDropChance.get() + (looting * xpSettings.lootingChanceBoost.get())
        }
        chance = chance.coerceIn(0f, 1f)
        if (ONS.random().nextFloat() > chance) return false
        val typeId = EntityType.getId(entity.type)
        if (globalBlacklist.contains(typeId)) return false
        if (globalWhitelist.contains(typeId)) return true
        if (xpSettings.xpBlacklist.contains(typeId)) return false
        return entity is Monster || xpSettings.xpWhitelist.contains(typeId)
    }

    fun willDropStatus(entity: Entity, playerEntity: ServerPlayerEntity, looting: Int): Boolean {
        val statuses = (entity as? LivingEntity)?.statusEffects?.size ?: 0
        val chance = statusSettings.statusDropChance.get() +
                (statuses * statusSettings.perStatusBoostAmount.get()) +
                (looting * statusSettings.lootingChanceBoost.get())
        if (ONS.random().nextFloat() > chance) return false
        if (statusSettings.statusAdvancements.any { advancement ->
            val adv = playerEntity.serverWorld.server.advancementLoader.get(advancement)
            if (adv == null) {
                ONS.LOGGER.error("Advancement $advancement couldn't be found. Check contents of the Orbs 'n' Stuff 'statusAdvancements' config list")
                true
            } else {
                !playerEntity.advancementTracker.getProgress(adv).isDone
            }
        }) return false
        val typeId = EntityType.getId(entity.type)
        if (globalBlacklist.contains(typeId)) return false
        if (globalWhitelist.contains(typeId)) return true
        if (statusSettings.statusBlacklist.contains(typeId)) return false
        return entity is Monster || statusSettings.statusWhitelist.contains(typeId)
    }

    private val bossTag = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("c", "bosses"))

    fun willDropBoss(entity: Entity, playerEntity: ServerPlayerEntity, looting: Int): Boolean {
        if (hasKilled(entity, playerEntity)) return false
        val typeId = EntityType.getId(entity.type)
        if (globalBlacklist.contains(typeId)) return false
        if (globalWhitelist.contains(typeId)) return true
        if (bossSettings.bossBlacklist.contains(typeId)) return false
        return entity.type.isIn(bossTag) || bossSettings.bossWhitelist.contains(typeId)
    }

    private fun hasKilled(entity: Entity, playerEntity: ServerPlayerEntity): Boolean {
        val stat = Stats.KILLED.getOrCreateStat(entity.type)
        return playerEntity.statHandler.getStat(stat) <= bossSettings.getBossKillCount(entity)
    }
}