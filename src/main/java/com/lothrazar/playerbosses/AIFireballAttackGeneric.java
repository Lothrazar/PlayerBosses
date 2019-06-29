package com.lothrazar.playerbosses;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AIFireballAttackGeneric extends EntityAIBase {

  private final EntityPlayerBoss parentEntity;
  public int attackTimer;

  public AIFireballAttackGeneric(EntityPlayerBoss ghast)
  {
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
    //    double d0 = 64.0D;
    if (attackTarget.getDistanceSq(this.parentEntity) < 4096.0D && this.parentEntity.canEntityBeSeen(attackTarget)) {

      World world = this.parentEntity.world;
      //      ++this.attackTimer;
      System.out.println(" distance yes" + attackTimer);
      //      if (this.attackTimer == 10) {
      //        world.playEvent((EntityPlayer) null, 1015, new BlockPos(this.parentEntity), 0);
      //      }
      if (world.rand.nextDouble() < ConfigManager.fireballChance) {
        double d1 = 4.0D;
        Vec3d vec3d = this.parentEntity.getLook(1.0F);
        double d2 = attackTarget.posX - (this.parentEntity.posX + vec3d.x * 4.0D);
        double d3 = attackTarget.getEntityBoundingBox().minY + attackTarget.height / 2.0F - (0.5D + this.parentEntity.posY + this.parentEntity.height / 2.0F);
        double d4 = attackTarget.posZ - (this.parentEntity.posZ + vec3d.z * 4.0D);
        world.playEvent((EntityPlayer) null, 1016, new BlockPos(this.parentEntity), 0);
        EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this.parentEntity, d2, d3, d4);
        entitylargefireball.explosionPower = this.parentEntity.getFireballStrength();
        entitylargefireball.posX = this.parentEntity.posX + vec3d.x * 4.0D;
        entitylargefireball.posY = this.parentEntity.posY + this.parentEntity.height / 2.0F + 0.5D;
        entitylargefireball.posZ = this.parentEntity.posZ + vec3d.z * 4.0D;
        world.spawnEntity(entitylargefireball);
        this.attackTimer = -40;
      }
    }
    //    else if (this.attackTimer > 0) {
    //      --this.attackTimer;
    //    }

    this.parentEntity.setAttacking(this.attackTimer > 10);
  }
}