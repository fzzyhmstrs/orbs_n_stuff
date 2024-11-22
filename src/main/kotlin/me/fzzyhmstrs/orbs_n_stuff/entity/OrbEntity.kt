package me.fzzyhmstrs.orbs_n_stuff.entity

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.Ownable
import net.minecraft.entity.data.DataTracker
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.World

class OrbEntity(type: EntityType<out OrbEntity>, world: World) : Entity(type, world), Ownable {

    override fun initDataTracker(builder: DataTracker.Builder) {
        TODO("Not yet implemented")
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {
        TODO("Not yet implemented")
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound?) {
        TODO("Not yet implemented")
    }

    override fun getOwner(): Entity? {
        TODO("Not yet implemented")
    }
}