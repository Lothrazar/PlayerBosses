package com.lothrazar.playerbosses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderGiantZombie;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerBoss extends RenderGiantZombie {

  public static ResourceLocation SKIN = null;
  //      new ResourceLocation(ModBosses.MODID,
  //      "textures/entity/skin.png");

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
    if (SKIN == null) {
      RenderPlayerBoss.SKIN = this.fetchTexture("Sevadus");
    }
    return SKIN;
  }

  private ResourceLocation fetchTexture(String name) {
    // sevadus is? 
    // https://minecraft-techworld.com/uuid-lookup-tool
    //https://skins.minecraft.net/MinecraftSkins/4ce6ca9f-9157-4e88-a874-0bb42c15a593.png
    // https://skins.minecraft.net/MinecraftSkins/Sevadus.png
    ResourceLocation resourcelocation = AbstractClientPlayer.getLocationSkin(name);// 
    System.out.println("BEFORE" + resourcelocation);
    AbstractClientPlayer.getDownloadImageSkin(resourcelocation, name);
    //   response. 
    System.out.println("AFTER  " + resourcelocation);
    TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
    ITextureObject texture = texturemanager.getTexture(resourcelocation);
    //    System.out.println("OK its in the map now what " + texture);
    return resourcelocation;
  }
}
