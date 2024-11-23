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

import me.fzzyhmstrs.orbs_n_stuff.renderer.OrbEntityRenderer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object EntityRendererRegistry {

    fun init() {
        EntityRendererRegistry.register(EntityRegistry.HP_ORB.get(), ::OrbEntityRenderer)
        EntityRendererRegistry.register(EntityRegistry.XP_ORB.get(), ::OrbEntityRenderer)
        EntityRendererRegistry.register(EntityRegistry.STATUS_ORB.get(), ::OrbEntityRenderer)
        EntityRendererRegistry.register(EntityRegistry.BOSS_ORB.get(), ::OrbEntityRenderer)
    }


}