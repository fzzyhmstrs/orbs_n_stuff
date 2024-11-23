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

import me.fzzyhmstrs.orbs_n_stuff.ONSClient
import me.fzzyhmstrs.orbs_n_stuff.config.ONSConfig
import me.fzzyhmstrs.orbs_n_stuff.config.ONSDebugConfig
import me.fzzyhmstrs.orbs_n_stuff.entity.variant.OrbVariant
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.MovementType
import net.minecraft.entity.Ownable
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.DustParticleEffect
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World
import org.joml.Vector3f
import java.awt.Color
import kotlin.math.min

class OrbEntity(type: EntityType<out OrbEntity>, world: World, val variant: OrbVariant) : Entity(type, world), Ownable {

    private var owner: Entity? = null
    private var count = 1
    private var orbAge = 0

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        count = if (nbt.contains("loot_count")) nbt.getInt("loot_count") else 1
        orbAge = if (nbt.contains("orb_age")) nbt.getInt("orb_age") else 0
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        if (count != 1) {
            nbt.putInt("loot_count", count)
        }
        if (orbAge > 0) {
            nbt.putInt("orb_age", orbAge)
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
            orbBeam()
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

    private fun orbBeam() {
        if (!ONSConfig.INSTANCE.clientSettings.showOrbBeams.get()) return
        val camera = ONSClient.getCamera()
        if (camera == null || !shouldParticle(camera)) return
        val color = Color(variant.color.get())
        val colors = color.getColorComponents(null)
        for (i in 1..ONSConfig.INSTANCE.clientSettings.particleCount.get()) {
            world.addParticle(
                DustParticleEffect(
                    Vector3f(colors),
                    1f
                ),
                true,
                this.x + random.nextTriangular(0.0, 0.075),
                this.y + ONSConfig.INSTANCE.clientSettings.beamOffset.get() + (random.nextDouble() * ONSConfig.INSTANCE.clientSettings.beamHeight.get()),
                this.z + random.nextTriangular(0.0, 0.075),
                0.0,
                0.0,
                0.0
            )
        }
    }

    private fun shouldParticle(camera: Entity): Boolean {
        val xx = this.x - camera.x
        val yy = this.y - camera.y
        val zz = this.z - camera.z
        val dd = xx * xx + yy * yy + zz * zz
        return shouldRender(dd)
    }

    override fun onPlayerCollision(player: PlayerEntity) {
        if (ONSDebugConfig.INSTANCE.restrictPickups.shouldPlayerPickup(player)) {
            handleCollision(player)
        }
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

    override fun shouldRender(distance: Double): Boolean {
        var d = boundingBox.lengthX
        if (java.lang.Double.isNaN(d)) {
            d = 1.0
        }

        d *= 64.0 * getRenderDistanceMultiplier() * ONSConfig.INSTANCE.clientSettings.renderMultiplier.get()
        return distance < d * d
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