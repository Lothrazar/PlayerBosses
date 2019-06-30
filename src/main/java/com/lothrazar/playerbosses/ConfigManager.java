package com.lothrazar.playerbosses;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigManager {

  public static boolean renderDebugHitboxes;
  public static Configuration config;
  public static boolean canDespawn;
  public static double fireballChance;
  public static double thirdPhaseHealth;
  public static double secondPhaseHealth;
  public static boolean arrowPotions;
  public static boolean thirdPhaseRunaway;
  public static double arrowChance;

  private static void load() {
    // CHAT SENDING  
    // on  certain damage levels. make sure only once. 
    String category = ModBosses.MODID;
    renderDebugHitboxes = config.getBoolean("debugHitboxes", category, false, "Show hitboxes for testing");
    canDespawn = config.getBoolean("canDespawnNaturally", category, false, "Natural Despawning");
    EntityPlayerBoss.bossName = config.getString("name",
        category, "Severed", "Name in boss bar");
    EntityPlayerBoss.expDropped = config.getInt("expDropped", category, 500, 0, 99999, "Exp when killed");
    EntityPlayerBoss.immuneFire = config.getBoolean("immune to fire", category, true, "is immune to fire");
    EntityPlayerBoss.armor = config.getInt("armor", category, 2, 0, 20, "Armor default when not equipped");
    EntityPlayerBoss.armor = config.getInt("armor", category, 2, 0, 20, "Armor default when not equipped");
    EntityPlayerBoss.health = config.getInt("health", category, 250, 0, 99999, "Health value");
    EntityPlayerBoss.speed = config.getFloat("speed", category, 0.5F, 0, 2.0F, "Movement speed value");
    EntityPlayerBoss.damage = config.getInt("damage", category, 5, 0, 999, "Attack damage without weapons");
    EntityPlayerBoss.mainHand = config.getString("MainHand",
        category, "minecraft:iron_sword", "Main hand holding");
    EntityPlayerBoss.offHand = config.getString("OffHand",
        category, "minecraft:shield", "Off hand holding");
    category = ModBosses.MODID + ".firstPhase";
    config.addCustomCategoryComment(category, "Initial phase is always bow.  ");
    arrowPotions = config.getBoolean("arrowPotions", category, true, "In first phase, do the arrows have potion effects");
    arrowChance = config.getFloat("arrowChance", category, 0.03F, 0, 0.99F, "Chance of bow shots in a tick, approximate");
    category = ModBosses.MODID + ".secondphase";
    config.addCustomCategoryComment(category, "Melee Phase ");
    secondPhaseHealth = config.getFloat("healthTrigger", category, 0.6F, 0, 0.99F, "At which percentage of health should it switch to second phase of melee attacks");
    category = ModBosses.MODID + ".thirdphase";
    config.addCustomCategoryComment(category, "Final Magic Phase ");
    thirdPhaseHealth = config.getFloat("healthTrigger", category, 0.15F, 0, 0.99F, "At which percentage of health should it switch to third phase of ghast fireballs");
    thirdPhaseRunaway = config.getBoolean("doRunaway", category, true, "Does it run away while shooting fire in phase three");
    fireballChance = config.getFloat("fireballChance", category, 0.2F, 0, 0.99F, "In third phase, how often to fireballs shoot");
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
