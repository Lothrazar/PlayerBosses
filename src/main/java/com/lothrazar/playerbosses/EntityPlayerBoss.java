package com.lothrazar.playerbosses;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityPlayerBoss extends EntityGiantZombie {

  public EntityPlayerBoss(World worldIn) {
    super(worldIn);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    //  this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20D);
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    //    this.dataManager.register(SWINGING_ARMS, Boolean.valueOf(false));
  }

  @Override
  protected void initEntityAI() {
    super.initEntityAI();
    this.tasks.addTask(1, new EntityAISwimming(this));
    this.tasks.addTask(6, new EntityAILookIdle(this));
    this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
  }

  @Override
  protected ResourceLocation getLootTable() {
    return new ResourceLocation(ModBosses.MODID, "player_boss");
  }

  public static class Factory implements IRenderFactory<EntityPlayerBoss> {

    @Override
    public Render<? super EntityPlayerBoss> createRenderFor(RenderManager manager) {
      return new RenderPlayerBoss(manager);
    }
  }
}
