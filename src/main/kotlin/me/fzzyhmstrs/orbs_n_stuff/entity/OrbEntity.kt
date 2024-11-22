package me.fzzyhmstrs.orbs_n_stuff.entity

import me.fzzyhmstrs.lootables.api.LootablesApi
import me.fzzyhmstrs.lootables.api.LootableItem.LootableData.EventType
import me.fzzyhmstrs.orbs_n_stuff.config.ONSConfig
import me.fzzyhmstrs.orbs_n_stuff.entity.variant.OrbVariant
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.Ownable
import net.minecraft.entity.data.DataTracker
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.World

class OrbEntity(type: EntityType<out OrbEntity>, world: World, private val variant: OrbVariant) : Entity(type, world), Ownable {

    private var owner: Entity? = null
    
    override fun initDataTracker(builder: DataTracker.Builder) {
        TODO("Not yet implemented")
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {
        TODO("Not yet implemented")
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound?) {
        TODO("Not yet implemented")
    }

    override fun tick() {
        super.tick
        if (this.age > ONSConfig.INSTANCE.orbDespawnTime.get()) {
            this.discard()
        }
    }

    private fun handleCollision(collidedWith: Entity) {
        if (collidedWith !is ServerPlayerEntity) return
        if (collidedWith != owner && this.age <= ONSConfig.INSTANCE.orbOwnerTime.get()) return
        if (variant.supplyLoot(collidedWith, this.pos))
            this.discard()
    }

    override fun setOwner(entity: Entity) {
        this.owner = owner
    }

    override fun getOwner(): Entity? {
        return owner
    }
}
