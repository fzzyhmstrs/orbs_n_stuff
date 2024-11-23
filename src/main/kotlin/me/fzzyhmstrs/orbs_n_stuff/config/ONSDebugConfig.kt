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

package me.fzzyhmstrs.orbs_n_stuff.config

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField.Companion.withListener
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.orbs_n_stuff.ONS
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerEntity

class ONSDebugConfig: Config(ONS.identity("debug")) {

    var restrictPickups = Restriction.NEVER

    enum class Restriction(val always: Boolean, val creative: Boolean) {
        NEVER(false, false),
        CREATIVE(false, true),
        ALWAYS(true, true);

        fun shouldPlayerPickup(playerEntity: PlayerEntity): Boolean {
            return !(always || playerEntity.isCreative && creative)
        }
    }

    @Transient
    private var locked = false

    var guaranteeHpDrops: ValidatedBoolean = ValidatedBoolean(false).withListener { hp ->
        if (locked) return@withListener
        locked = true
        if (hp.get()) {
            guaranteeXpDrops.accept(false)
            guaranteeStatusDrops.accept(false)
            guaranteeBossDrops.accept(false)
        }
        locked = false
    }
    var guaranteeXpDrops: ValidatedBoolean = ValidatedBoolean(false).withListener { xp ->
        if (locked) return@withListener
        locked = true
        if (xp.get()) {
            guaranteeHpDrops.accept(false)
            guaranteeStatusDrops.accept(false)
            guaranteeBossDrops.accept(false)
        }
        locked = false
    }
    var guaranteeStatusDrops: ValidatedBoolean = ValidatedBoolean(false).withListener { status ->
        if (locked) return@withListener
        locked = true
        if (status.get()) {
            guaranteeHpDrops.accept(false)
            guaranteeXpDrops.accept(false)
            guaranteeBossDrops.accept(false)
        }
        locked = false
    }
    var guaranteeBossDrops: ValidatedBoolean = ValidatedBoolean(false).withListener { boss ->
        if (locked) return@withListener
        locked = true
        if (boss.get()) {
            guaranteeHpDrops.accept(false)
            guaranteeXpDrops.accept(false)
            guaranteeStatusDrops.accept(false)
        }
        locked = false
    }

    companion object {

        val INSTANCE = if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            ConfigApi.registerAndLoadConfig({ ONSDebugConfig() })
        } else {
            ONSDebugConfig()
        }

        fun init() {}

    }


}