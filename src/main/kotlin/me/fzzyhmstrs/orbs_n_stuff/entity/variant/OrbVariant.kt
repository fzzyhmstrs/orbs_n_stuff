package me.fzzyhmstrs.orbs_n_stuff.entity.variant

import me.fzzyhmstrs.lootables.api.LootableItem
import net.minecraft.server.network.ServerPlayerEntity
import java.util.function.Consumer
import java.util.function.Supplier

data class OrbVariant(val data: LootableItem.LootableData, val color: Supplier<Int>, val onPickedUp: Consumer<ServerPlayerEntity>) {
}
