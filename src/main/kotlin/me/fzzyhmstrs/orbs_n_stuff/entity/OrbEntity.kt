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

package me.fzzyhmstrs.orbs_n_stuff.entity

import me.fzzyhmstrs.orbs_n_stuff.config.ONSConfig
import me.fzzyhmstrs.orbs_n_stuff.entity.variant.OrbVariant
import net.minecraft.entity.*
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World
import kotlin.math.min

class OrbEntity(type: EntityType<out OrbEntity>, world: World, val variant: OrbVariant) : Entity(type, world), Ownable {

    private var owner: Entity? = null
    private var count = 1
    private var orbAge = 0

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        count = if(nbt.contains("loot_count")) nbt.getInt("loot_count") else 1
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        if (count != 1) {
            nbt.putInt("loot_count", count)
        }
    }

    override fun initDataTracker(builder: DataTracker.Builder?) {
    }

    override fun tick() {
        super.tick()
        orbAge++
        if (ONSConfig.INSTANCE.orbDespawnTime.get() in 0 until orbAge && !this.world.isClient) {
            this.discard()
            return
        }

        val oldVelocity = this.velocity

        if (this.isTouchingWater && this.getFluidHeight(FluidTags.WATER) > 0.1f) {
            this.applyWaterBuoyancy()
        } else if (this.isInLava && this.getFluidHeight(FluidTags.LAVA) > 0.1f) {
            this.applyLavaBuoyancy()
        } else {
            this.applyGravity()
        }

        if (world.isClient) {
            this.noClip = false
        } else {
            this.noClip = !world.isSpaceEmpty(this, boundingBox.contract(1.0E-7))
            if (this.noClip) {
                this.pushOutOfBlocks(this.x, (boundingBox.minY + boundingBox.maxY) / 2.0, this.z)
            }
        }

        if (!this.isOnGround || velocity.horizontalLengthSquared() > 1.0E-5f || (this.age + this.id) % 4 == 0) {
            this.move(MovementType.SELF, this.velocity)
            var f = 0.98f
            if (this.isOnGround) {
                f = world.getBlockState(this.velocityAffectingPos).block.slipperiness * 0.98f
            }

            this.velocity = velocity.multiply(f.toDouble(), 0.98, f.toDouble())
            if (this.isOnGround) {
                val vec3d2 = this.velocity
                if (vec3d2.y < 0.0) {
                    this.velocity = vec3d2.multiply(1.0, -0.5, 1.0)
                }
            }
        }

        if (!this.world.isClient && this.age % 23 == 0) {
            val orbs = world.getEntitiesByClass(
                OrbEntity::class.java,
                boundingBox.expand(0.5, 0.0, 0.5)
            ) { otherOrb: OrbEntity -> otherOrb !== this && otherOrb.variant == this.variant }

            for (orb in orbs) {
                merge(orb)
            }
        }

        this.velocityDirty = this.velocityDirty or this.updateWaterState()
        if (!world.isClient) {
            val d = velocity.subtract(oldVelocity).lengthSquared()
            if (d > 0.01) {
                this.velocityDirty = true
            }
        }

        if (this.world.isClient) {

        }
    }

    private fun applyWaterBuoyancy() {
        val vec3d = this.velocity
        this.setVelocity(vec3d.x * 0.99f, vec3d.y + (if (vec3d.y < 0.06f) 5.0E-3f else 0.0f).toDouble(), vec3d.z * 0.99f)
    }

    private fun applyLavaBuoyancy() {
        val vec3d = this.velocity
        this.setVelocity(vec3d.x * 0.95f, vec3d.y + (if (vec3d.y < 0.06f) 5.0E-4f else 0.0f).toDouble(), vec3d.z * 0.95f)
    }

    private fun merge(other: OrbEntity) {
        this.count += other.count
        this.orbAge = min(this.orbAge, other.orbAge)
        other.discard()
    }

    override fun onPlayerCollision(player: PlayerEntity) {
        handleCollision(player)
    }

    private fun handleCollision(collidedWith: PlayerEntity) {
        if (collidedWith !is ServerPlayerEntity) return
        if (collidedWith != owner && this.orbAge <= ONSConfig.INSTANCE.orbOwnerTime.get()) return
        var bl = true
        for (i in 1..count) {
            bl = bl && variant.supplyLoot(collidedWith, this.pos)
        }
        if (bl) {
            this.discard()
        }
    }

    override fun isFireImmune(): Boolean {
        return true
    }

    override fun isAttackable(): Boolean {
        return false
    }

    override fun getGravity(): Double {
        return 0.04
    }

    fun setOwner(entity: Entity) {
        this.owner = entity
    }

    override fun getOwner(): Entity? {
        return owner
    }
}