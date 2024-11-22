package me.fzzyhmstrs.orbs_n_stuff.entity.variant

import me.fzzyhmstrs.lootables.api.LootableItem
import net.minecraft.server.network.ServerPlayerEntity
import java.util.function.Supplier

data class OrbVariant(val data: LootableItem.LootableData, val color: Supplier<Int>, val onPickedUpForUse: UsePredicate) {

    fun supplyLoot(playerEntity: ServerPlayerEntity, pos: Vec3d): Boolean {
        return if (data.eventType == LootableItem.LootableData.EventType.PICKUP) {
            if (data.rollType == LootableItem.LootableData.RollType.RANDOM) {
                LootablesApi.supplyLootRandomly(data.table, playerEntity, pos, data.key, data.rolls))
            } else {
                LootablesApi.supplyLootWithChoices(data.table, playerEntity, pos, { _, _ -> }, { p, _ -> }, data.key, data.rolls)
            }
        } else {
            onPickedUpForUse.onPickup(playerEntity, pos, data)
        }
    }

    @FunctionalInterface
    fun interface UsePredicate {
        fun onPickup(playerEntity: ServerPlayerEntity, pos: Vec3d, data: LootableItem.LootableData): Boolean
    }
}
