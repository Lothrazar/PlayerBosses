package com.lothrazar.playerbosses;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AIFireballAttackGeneric extends EntityAIBase {

  private final EntityPlayerBoss parentEntity;
  public int attackTimer;

  public AIFireballAttackGeneric(EntityPlayerBoss ghast) {
    this.parentEntity = ghast;
  }

  /**
   * Returns whether the EntityAIBase should begin execution.
   */
  @Override
  public boolean shouldExecute() {
    return this.parentEntity.getAttackTarget() != null;
  }

  /**
   * Execute a one shot task or start executing a continuous task
   */
  @Override
  public void startExecuting() {
    this.attackTimer = 0;
  }

  /**
   * Reset the task's internal state. Called when this task is interrupted by another one
   */
  @Override
  public void resetTask() {
    this.parentEntity.setAttacking(false);
  }

  /**
   * Keep ticking a continuous task that has already been started
   */
  @Override
  public void updateTask() {
    EntityLivingBase attackTarget = this.parentEntity.getAttackTarget();

    if (attackTarget.getDistanceSq(this.parentEntity) < 4096.0D && this.parentEntity.canEntityBeSeen(attackTarget)) {
      World world = this.parentEntity.world;

      if (world.rand.nextDouble() < ConfigManager.fireballChance) {
        final double offset = 4.0D;
        final Vec3d vec3d = this.parentEntity.getLook(1.0F);
        final double dx = attackTarget.posX - (this.parentEntity.posX + vec3d.x * offset);
        final double dy = attackTarget.getEntityBoundingBox().minY + attackTarget.height / 2.0F - (0.5D + this.parentEntity.posY + this.parentEntity.height / 2.0F);
        final double dz = attackTarget.posZ - (this.parentEntity.posZ + vec3d.z * offset);
        world.playEvent((EntityPlayer) null, 1016, new BlockPos(this.parentEntity), 0);
        EntitySmallFireball fireball = new EntitySmallFireball(world, this.parentEntity, dx, dy, dz);
        fireball.posX = this.parentEntity.posX + vec3d.x * offset;
        fireball.posY = this.parentEntity.posY + this.parentEntity.height / 2.0F + 0.5D;
        fireball.posZ = this.parentEntity.posZ + vec3d.z * offset;
        world.spawnEntity(fireball);
        this.attackTimer = -40;
      }
    }

    this.parentEntity.setAttacking(this.attackTimer > 10);
  }
}