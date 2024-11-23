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

package me.fzzyhmstrs.orbs_n_stuff.entity.variant

import me.fzzyhmstrs.lootables.api.LootableItem
import me.fzzyhmstrs.lootables.api.LootablesApi
import me.fzzyhmstrs.orbs_n_stuff.ONS
import me.fzzyhmstrs.orbs_n_stuff.registry.ItemRegistry
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.DyeColor
import net.minecraft.util.Util
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import java.awt.Color
import java.util.function.Consumer
import java.util.function.Supplier

class OrbVariant private constructor(val data: LootableItem.LootableData, val color: Supplier<Int>, val onSupplied: Consumer<ServerPlayerEntity>?, val onPickedUpForUse: UsePredicate?) {

    companion object {
        @JvmStatic
        @JvmOverloads
        fun create(data: LootableItem.LootableData, color: Supplier<Int>, onSupplied: Consumer<ServerPlayerEntity>? = null, onPickedUpForUse: UsePredicate? = null): OrbVariant {
            return OrbVariant(data, color, onSupplied, onPickedUpForUse)
        }

        @JvmStatic
        @JvmOverloads
        fun create(data: LootableItem.LootableData, color: Int, onSupplied: Consumer<ServerPlayerEntity>? = null, onPickedUpForUse: UsePredicate? = null): OrbVariant {
            return OrbVariant(data, { color }, onSupplied, onPickedUpForUse)
        }

        @JvmStatic
        @JvmOverloads
        fun create(data: LootableItem.LootableData, color: DyeColor, onSupplied: Consumer<ServerPlayerEntity>? = null, onPickedUpForUse: UsePredicate? = null): OrbVariant {
            return OrbVariant(data, { color.fireworkColor }, onSupplied, onPickedUpForUse)
        }

        private object XpColor: Supplier<Int> {

            override fun get(): Int {
                val time = (Util.getMeasuringTimeMs() % 2000).toInt()
                val index = MathHelper.cos((Math.PI * 2.0).toFloat() * (time / 2000f)) + 1f
                val hue = MathHelper.clampedMap(index, 0f, 2f, 0.16666667f, 0.3611111f)
                return Color.HSBtoRGB(hue, 0.6f, 1f)
            }
        }

        private object BossColor: Supplier<Int> {

            override fun get(): Int {
                val hue = (Util.getMeasuringTimeMs() % 2000L).toFloat() / 2000f
                return Color.HSBtoRGB(hue, 2.75f / 3f, 0.9f)
            }
        }

        private fun soundConsumer(soundEvent: SoundEvent): Consumer<ServerPlayerEntity> {
            return Consumer { p ->
                p.networkHandler.sendPacket(PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(soundEvent), SoundCategory.PLAYERS, p.x, p.y, p.z, 0.2f, (p.random.nextFloat() - p.random.nextFloat()) * 1.4f + 2.0f, ONS.random().nextLong()))
            }
        }

        internal val bossData = LootableItem.LootableData.builder(ONS.identity("boss")).rolls(3).choices(1).build()

        val HP = create(LootableItem.LootableData.builder(ONS.identity("hp")).onPickup().build(), DyeColor.RED, soundConsumer(SoundEvents.ENTITY_WARDEN_HEARTBEAT))
        val XP = create(LootableItem.LootableData.builder(ONS.identity("xp")).onPickup().build(), XpColor, soundConsumer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP))
        val STATUS = create(LootableItem.LootableData.builder(ONS.identity("status")).onPickup().build(), DyeColor.CYAN, soundConsumer(SoundEvents.BLOCK_BEACON_POWER_SELECT))
        val BOSS = create(
            bossData,
            BossColor,
            soundConsumer(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE)
        ) { p, _, _ ->
            val stack = ItemRegistry.ORB_OF_ACCOMPLISHMENT.get().defaultStack
            p.inventory.offerOrDrop(stack)
            true
        }

    }

    fun supplyLoot(playerEntity: ServerPlayerEntity, pos: Vec3d): Boolean {
        val bl = if (data.eventType == LootableItem.LootableData.EventType.PICKUP) {
            if (data.rollType == LootableItem.LootableData.RollType.RANDOM) {
                LootablesApi.supplyLootRandomly(data.table, playerEntity, pos, data.key, data.rolls)
            } else {
                LootablesApi.supplyLootWithChoices(data.table, playerEntity, pos, { _, _ -> }, { p, _ -> }, data.key, data.rolls)
            }
        } else {
            onPickedUpForUse?.onPickup(playerEntity, pos, data) ?: false
        }
        if (bl) {
            onSupplied?.accept(playerEntity)
        }
        return bl
    }

    @FunctionalInterface
    fun interface UsePredicate {
        fun onPickup(playerEntity: ServerPlayerEntity, pos: Vec3d, data: LootableItem.LootableData): Boolean
    }
}