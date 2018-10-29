package com.lothrazar.playerbosses;

import net.minecraft.client.renderer.entity.RenderGiantZombie;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerBoss extends RenderGiantZombie {

  private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation(ModBosses.MODID,
      "textures/entity/skin.png");

  public RenderPlayerBoss(RenderManager rm) {
    super(rm, 6.0F);//same as vanilla giant zombie size, MAYBE config

  }

  @Override
  public void doRender(EntityGiantZombie entity, double x, double y, double z, float entityYaw, float partialTicks) {
    super.doRender(entity, x, y, z, entityYaw, partialTicks);
    if (ConfigManager.renderDebugHitboxes)
      RenderUtil.renderEntityBoundingBox(entity, x, y, z);
  }

  @Override
  protected ResourceLocation getEntityTexture(EntityGiantZombie entity) {
    return ZOMBIE_TEXTURES;
  }
}
