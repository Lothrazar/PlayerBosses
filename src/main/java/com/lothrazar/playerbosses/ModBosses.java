package com.lothrazar.playerbosses;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ModBosses.MODID, certificateFingerprint = "@FINGERPRINT@")
public class ModBosses {

  public static final String MODID = "playerbosses";
  private static Logger logger;
  @Instance(MODID)
  public static ModBosses instance;
  private List<SoundEvent> sounds = new ArrayList<>();

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    logger = event.getModLog();
    ConfigManager.init(event.getSuggestedConfigurationFile());
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(new ConfigManager());
    int id = 0;
    String name = "player_boss";
    EntityRegistry.registerModEntity(new ResourceLocation(MODID, name),
        EntityPlayerBoss.class, name, id++, instance, 64, 1, true);
    EntityPlayerBoss.SOUND_HURT = registerSound("boss.hurt");
    EntityPlayerBoss.SOUND_AMB = registerSound("boss.ambient");
    EntityPlayerBoss.SOUND_DEATH = registerSound("boss.death");
  }

  public SoundEvent registerSound(String name) {
    final ResourceLocation res = new ResourceLocation(MODID, name);//new ResourceLocation(Const.MODID, "sounds/" + UtilSound.Own.crackle+".ogg");
    SoundEvent sound = new SoundEvent(res);
    sound.setRegistryName(res);
    sounds.add(sound);
    return sound;
  }

  @SubscribeEvent
  public void onRegisterSoundEvent(RegistryEvent.Register<SoundEvent> event) {
    event.getRegistry().registerAll(this.sounds.toArray(new SoundEvent[0]));
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void registerRendering(ModelRegistryEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityPlayerBoss.class, new EntityPlayerBoss.Factory());
  }

  @EventHandler
  public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
    // https://tutorials.darkhax.net/tutorials/jar_signing/
    String source = (event.getSource() == null) ? "" : event.getSource().getName() + " ";
    String msg = MODID + ": Invalid fingerprint detected! The file " + source + "may have been tampered with. This version will NOT be supported by the author!";
    if (logger == null) {
      System.out.println(msg);
    }
    else {
      logger.error(msg);
    }
  }
}
