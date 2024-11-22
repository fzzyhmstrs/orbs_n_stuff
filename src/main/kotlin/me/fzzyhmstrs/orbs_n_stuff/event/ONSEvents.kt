package me.fzzyhmstrs.orbs_n_stuff.event


object ONSEvents {
    
    fun init(){
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register { world, killer, killed ->
            if (killer !is ServerPlayerEntity) return@Register
            if (ONSConfig.INSTANCE.willDropHp(killed)) {
                val orb: OrbEntity = TODO()
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val xDelta = (ONS.random().nextDouble() - 0.5) * 0.15
                val zDelta = (ONS.random().nextDouble() - 0.5) * 0.15
                orb.setVelocity(killed.velocity.x + xDelta, killed.velocity.y + 0.1, killed.velocity.z + zDelta)
                world.spawnEntity(orb)
            }
            if (ONSConfig.INSTANCE.willDropXp(killed)) {
                val orb: OrbEntity = TODO()
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val xDelta = (ONS.random().nextDouble() - 0.5) * 0.15
                val zDelta = (ONS.random().nextDouble() - 0.5) * 0.15
                orb.setVelocity(killed.velocity.x + xDelta, killed.velocity.y + 0.1, killed.velocity.z + zDelta)
                world.spawnEntity(orb)
            }
            if (ONSConfig.INSTANCE.willDropStatus(killed, killer)) {
                val orb: OrbEntity = TODO()
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val xDelta = (ONS.random().nextDouble() - 0.5) * 0.15
                val zDelta = (ONS.random().nextDouble() - 0.5) * 0.15
                orb.setVelocity(killed.velocity.x + xDelta, killed.velocity.y + 0.1, killed.velocity.z + zDelta)
                world.spawnEntity(orb)
            }
            if (ONSConfig.INSTANCE.willDropBoss(killed, killer)) {
                val orb: OrbEntity = TODO()
                orb.setOwner(killer)
                orb.setPosition(killed.pos.x, killed.pos.y + (killed.height/2f).toDouble(), killed.pos.z)
                val xDelta = (ONS.random().nextDouble() - 0.5) * 0.15
                val zDelta = (ONS.random().nextDouble() - 0.5) * 0.15
                orb.setVelocity(killed.velocity.x + xDelta, killed.velocity.y + 0.1, killed.velocity.z + zDelta)
                world.spawnEntity(orb)
            }
        }
    }
}
