package com.lothrazar.playerbosses;

import org.apache.logging.log4j.Logger;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ModBosses.MODID)
public class ModBosses {

  public static final String MODID = "playerbosses";
  private static Logger logger;
  @Instance(MODID)
  public static ModBosses instance;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    logger = event.getModLog();
    MinecraftForge.EVENT_BUS.register(this);
    int id = 0;
    String name = "player_boss";
    EntityRegistry.registerModEntity(new ResourceLocation(MODID, name),
        EntityPlayerBoss.class, name, id++, instance, 64, 1, true);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    // some example code
    logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void registerRendering(ModelRegistryEvent event) {
    // 
    RenderingRegistry.registerEntityRenderingHandler(EntityPlayerBoss.class, new EntityPlayerBoss.Factory());
  }
}
