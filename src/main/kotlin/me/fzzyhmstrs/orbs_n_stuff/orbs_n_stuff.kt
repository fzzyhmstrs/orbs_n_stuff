@file:Suppress("PropertyName")

package me.fzzyhmstrs.orbs_n_stuff

import me.fzzyhmstrs.orbs_n_stuff.config.ONSConfig
import me.fzzyhmstrs.orbs_n_stuff.event.ONSEvents
import me.fzzyhmstrs.orbs_n_stuff.registry.EntityRegistry
import me.fzzyhmstrs.orbs_n_stuff.registry.EntityRendererRegistry
import me.fzzyhmstrs.orbs_n_stuff.registry.ItemRegistry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object ONS: ModInitializer {
    const val MOD_ID = "orbs_n_stuff"
    val LOGGER: Logger = LoggerFactory.getLogger("orbs_n_stuff")
    val random = Random.createThreadSafe()


    override fun onInitialize() {
        ONSConfig.init()
        ONSEvents.init()
        ItemRegistry.init()
        EntityRegistry.init()
    }

    fun random(): Random {
        return random
    }

    fun identity(path: String): Identifier {
        return Identifier.of(MOD_ID, path)
    }
}

@Environment(value = EnvType.CLIENT)
object ONSClient: ClientModInitializer {

    override fun onInitializeClient() {
        EntityRendererRegistry.init()
    }

    fun random(): Random {
        return Random.createLocal()
    }
}