package me.fzzyhmstrs.orbs_n_stuff.entity.variant

import me.fzzyhmstrs.lootables.api.LootableItem
import java.util.function.Supplier

data class OrbVariant(val data: LootableItem.LootableData, val color: Supplier<Int>) {
}