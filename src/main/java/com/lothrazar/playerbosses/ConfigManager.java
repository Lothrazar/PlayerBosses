package com.lothrazar.playerbosses;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigManager {

  public static Configuration config;
  public static boolean renderDebugHitboxes;

  private static void load() {
    String category = ModBosses.MODID;
    EntityPlayerBoss.armor = config.getInt("armor", category, 2, 0, 20, "Armor default when not equipped");
    EntityPlayerBoss.health = config.getInt("health", category, 100, 0, 99999, "Health value");
    EntityPlayerBoss.speed = config.getFloat("speed", category, 0.5F, 0, 2.0F, "Movement speed value");
    EntityPlayerBoss.damage = config.getInt("damage", category, 5, 0, 999, "Attack damage without weapons");
    EntityPlayerBoss.mainHand = config.getString("MainHand",
        category, "minecraft:iron_sword", "Main hand holding");
    EntityPlayerBoss.offHand = config.getString("OffHand",
        category, "minecraft:shield", "Off hand holding");
    if (config.hasChanged()) {
      config.save();
    }
  }

  public static void init(File configFile) {
    config = new Configuration(configFile);
    load();
  }

  @SubscribeEvent
  public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
    if (event.getModID().equalsIgnoreCase(ModBosses.MODID)) {
      load();
    }
  }
}
