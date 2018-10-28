package com.lothrazar.playerbosses;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityPlayerBoss extends EntityGiantZombie {

  public EntityPlayerBoss(World worldIn) {
    super(worldIn);
  }

  @Override
  protected ResourceLocation getLootTable() {
    return LootTableList.ENTITIES_GIANT;
  }

  public static class Factory implements IRenderFactory<EntityPlayerBoss> {

    @Override
    public Render<? super EntityPlayerBoss> createRenderFor(RenderManager manager) {
      return new RenderPlayerBoss(manager);
    }
  }
}
