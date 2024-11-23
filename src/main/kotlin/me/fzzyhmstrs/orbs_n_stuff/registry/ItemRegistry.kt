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

import me.fzzyhmstrs.lootables.api.LootableItem
import me.fzzyhmstrs.lootables.registry.ComponentRegistry
import me.fzzyhmstrs.orbs_n_stuff.ONS
import me.fzzyhmstrs.orbs_n_stuff.entity.variant.OrbVariant
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Rarity
import java.util.function.Supplier

object ItemRegistry {

    private val orbOfAccomplishment = Registry.register(
        Registries.ITEM,
        ONS.identity("orb_of_accomplishment"),
        LootableItem(Item.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .maxCount(1)
            .component(ComponentRegistry.LOOTABLE_DATA.get(), OrbVariant.bossData)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true))
    )

    val ORB_OF_ACCOMPLISHMENT: Supplier<Item> = Supplier { orbOfAccomplishment }

    fun init() {}

}