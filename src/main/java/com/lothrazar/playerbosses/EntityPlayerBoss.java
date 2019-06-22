package com.lothrazar.playerbosses;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityPlayerBoss extends EntityGiantZombie {

  public static double armor;
  public static double health;
  public static double speed;
  public static double damage;
  public static boolean immuneFire;
  public static int expDropped;
  public static String bossName;
  public static String mainHand = "";
  public static String offHand = "";
  public static SoundEvent SOUND_HURT;
  public static SoundEvent SOUND_AMB;
  public static SoundEvent SOUND_DEATH;
  private final BossInfoServer bossInfo = (BossInfoServer) (new BossInfoServer(this.getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);

  public EntityPlayerBoss(World worldIn) {
    super(worldIn);

    // bossInfo = (BossInfoServer) (new BossInfoServer(this.getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);
    net.minecraft.entity.boss.EntityWither x;
    this.isImmuneToFire = immuneFire;
    this.experienceValue = expDropped;// config
    //    ((PathNavigateGround)this.getNavigator()).setCanSwim(true);
  }

  @Override
  protected boolean canDespawn() {
    return ConfigManager.canDespawn;
  }

  ////////////////// Boss info and boss bar
  @Override
  public boolean isNonBoss() {
    return false;
  }

  @Override
  public String getName() {
    return bossName;
  }

  @Override
  public void addTrackingPlayer(EntityPlayerMP player) {
    super.addTrackingPlayer(player);
    this.bossInfo.addPlayer(player);
  }

  @Override
  public void removeTrackingPlayer(EntityPlayerMP player) {
    super.removeTrackingPlayer(player);
    this.bossInfo.removePlayer(player);
  }

  @Override
  public void updateAITasks() {
    super.updateAITasks();
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
  }

  /////////////// data properties // some from config 
  @Override
  protected ResourceLocation getLootTable() {
    return new ResourceLocation(ModBosses.MODID, "entity/player_boss");
  }

  @Override
  public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
    IEntityLivingData res = super.onInitialSpawn(difficulty, livingdata);
    this.setLeftHanded(false);
    if (!mainHand.isEmpty()) {
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Item.getByNameOrId(mainHand)));
    }
    if (!offHand.isEmpty()) {
      this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Item.getByNameOrId(offHand)));
    }
    return res;
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();

    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(speed);
    this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(damage);
    this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(armor);
    //  this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20D);
  }

  ///////////// AI
  @Override
  protected void initEntityAI() {
    super.initEntityAI();
    this.tasks.addTask(0, new EntityAISwimming(this));
    this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
    this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
    this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
    this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    this.tasks.addTask(8, new EntityAILookIdle(this));
    this.applyEntityAI();
  }

  protected void applyEntityAI() {
    this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
    this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] { EntityPigZombie.class }));
    this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
  }

  ///////////////// sounds 
  @Override
  protected SoundEvent getAmbientSound() {
    return SOUND_AMB;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    return SOUND_HURT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SOUND_DEATH;
  }

  protected SoundEvent getStepSound() {
    return SoundEvents.ENTITY_ZOMBIE_STEP;
  }

  @Override
  protected void playStepSound(BlockPos pos, Block blockIn) {
    this.playSound(this.getStepSound(), 0.15F, 1.0F);
  }

  /////////////////// render hook
  public static class Factory implements IRenderFactory<EntityPlayerBoss> {

    @Override
    public Render<? super EntityPlayerBoss> createRenderFor(RenderManager manager) {
      return new RenderPlayerBoss(manager);
    }
  }
}
