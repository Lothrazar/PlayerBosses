package com.lothrazar.playerbosses;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
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
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityPlayerBoss extends EntityGiantZombie implements IRangedAttackMob {

  private static final DataParameter<Boolean> ATTACKINGFIRE = EntityDataManager.<Boolean> createKey(EntityGiantZombie.class, DataSerializers.BOOLEAN);

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
  private EnumAttackType attackType;

  private static enum EnumAttackType {
    MELEE, RANGED, FIRE;
  }

  public EntityPlayerBoss(World worldIn) {
    super(worldIn);
    this.isImmuneToFire = immuneFire;
    this.experienceValue = expDropped;// config 
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    this.dataManager.register(ATTACKINGFIRE, Boolean.valueOf(false));
  }

  public void setAttacking(boolean attacking) {
    this.dataManager.set(ATTACKINGFIRE, Boolean.valueOf(attacking));
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
  public boolean isHandActive() {
    boolean yes = super.isHandActive();

    return this.attackType == EnumAttackType.RANGED;
  }
  @Override
  public void removeTrackingPlayer(EntityPlayerMP player) {
    super.removeTrackingPlayer(player);
    this.bossInfo.removePlayer(player);
  }

  @Override
  public void updateAITasks() {
    super.updateAITasks();
    this.bossInfo.setPercent(getHealthPercent());
  }

  private float getHealthPercent() {
    return this.getHealth() / this.getMaxHealth();
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
  }

  ///////////// AI
  private EntityAINearestAttackableTarget melee;
  private AIFireballAttackGeneric fireball;
  private EntityAIAttackRangedBow bow;

  public AIFireballAttackGeneric getAiFire() {
    if (fireball == null) {
      fireball = new AIFireballAttackGeneric(this);
    }
    return fireball;
  }
  public EntityAINearestAttackableTarget getAiMelee() {
    if (melee == null) {
      //      melee = new EntityAINearestAttackableTarget(this, EntityPlayer.class, true);
      melee = new EntityAINearestAttackableTarget(this, EntityPigZombie.class, true);
    }
    return melee;
  }

  public EntityAIAttackRangedBow getAiBow() {
    if (bow == null) { 
      System.out.println("create EntityAIAttackRangedBow");

      bow = new EntityAIAttackRangedBow<EntityPlayerBoss>(this, 1.0D, 20, 15.0F);
    }
    return bow;
  }
  @Override
  protected void initEntityAI() {
    super.initEntityAI();
    this.tasks.addTask(0, new EntityAISwimming(this));
    this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
    this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
    this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
    this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    this.tasks.addTask(8, new EntityAILookIdle(this));
    this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));

    this.attackType = EnumAttackType.RANGED;
    this.setCombatTask();
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
  public void writeEntityToNBT(NBTTagCompound root) {
    super.writeEntityToNBT(root);
    root.setInteger("attackType", this.attackType.ordinal());
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound root) {
    super.readEntityFromNBT(root);
    int at = root.getInteger("attackType");
    this.attackType = EnumAttackType.values()[at];
  }
  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();
    if (getHealthPercent() < ConfigManager.healthSwitchFire) {
      this.attackType = EnumAttackType.FIRE;
    }
    else if (getHealthPercent() < ConfigManager.healthMelee) {
      this.attackType = EnumAttackType.MELEE;
    }
    else {//from 100% down
      this.attackType = EnumAttackType.RANGED;
    }
    //else stay
    setCombatTask();
  }

  @Override
  @Nullable
  public EntityLivingBase getAttackTarget() {
    //    System.out.println("attack target? " + super.getAttackTarget());
    return super.getAttackTarget();

  }

  public void setCombatTask() {

    tasks.removeTask(getAiMelee());
    tasks.removeTask(getAiFire());
    tasks.removeTask(getAiBow());

    this.targetTasks.addTask(2, getAiMelee());
    switch (this.attackType) {
      case FIRE:
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
        tasks.addTask(4, this.getAiFire());
      break;
      case MELEE:
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPigZombie.class, true));
        tasks.addTask(4, this.getAiMelee());
      break;
      case RANGED:
        //        this.setAttackTarget(null); 
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityPigZombie.class, 12.0F, 1.0D, 1.2D));
        tasks.addTask(4, this.getAiBow());
      break;
      default:
      break;
    }
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

  public int getFireballStrength() {
    return 1;
  }

  @Override
  public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
    System.out.println("ranged attack?");
    EntityArrow entityarrow = this.getArrow(distanceFactor);
    double d0 = target.posX - this.posX;
    double d1 = target.getEntityBoundingBox().minY + target.height / 3.0F - entityarrow.posY;
    double d2 = target.posZ - this.posZ;
    double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
    entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, 14 - this.world.getDifficulty().getDifficultyId() * 4);
    this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    this.world.spawnEntity(entityarrow);
  }

  private EntityArrow getArrow(float p) {
    EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);
    entitytippedarrow.setEnchantmentEffectsFromEntity(this, p);
    System.out.println("get arrow?  attack? + " + entitytippedarrow);
    return entitytippedarrow;
  }

  @Override
  public void setSwingingArms(boolean swingingArms) {
    // TODO Auto-generated method stub
  }

  @Override
  public int getItemInUseMaxCount() {
    return 20;//20 or more if shoot
  }
}
