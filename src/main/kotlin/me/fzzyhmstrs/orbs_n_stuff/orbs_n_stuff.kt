@file:Suppress("PropertyName")

package me.fzzyhmstrs.orbs_n_stuff

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
    }

    fun random(): Random {
        return Random.createLocal()
    }
}