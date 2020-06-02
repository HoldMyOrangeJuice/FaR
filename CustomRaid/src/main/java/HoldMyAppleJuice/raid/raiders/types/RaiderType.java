package HoldMyAppleJuice.raid.raiders.types;

import HoldMyAppleJuice.raid.raiders.AI.Support.HealGoal;
import HoldMyAppleJuice.raid.raiders.AI.attacks.ShootGoal;
import HoldMyAppleJuice.raid.raiders.AI.attacks.MeleeAttackGoal;
import HoldMyAppleJuice.raid.raiders.AI.attacks.RaiderAttackGoal;
import HoldMyAppleJuice.raid.raiders.traits.*;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public enum RaiderType
{
    Archer(
            EntityType.SKELETON,
            new ItemStack(Material.GOLDEN_HELMET),
            new ItemStack(Material.GOLDEN_CHESTPLATE),
            new ItemStack(Material.GOLDEN_LEGGINGS),
            new ItemStack(Material.GOLDEN_BOOTS),
            new ItemStack(Material.BOW),
            1.1d, 30d,1.2f,1, 50,
            ArcherTrait.class, ShootGoal.class
    ),

    Marksman(
            EntityType.SKELETON,
            new ItemStack(Material.GOLDEN_HELMET),
            new ItemStack(Material.GOLDEN_CHESTPLATE),
            new ItemStack(Material.GOLDEN_LEGGINGS),
            new ItemStack(Material.GOLDEN_BOOTS),
            new ItemStack( Material.BOW),
            1.3d, 20d,1.1f,50, 1,
            MarksmanTrait.class, ShootGoal.class
    ),

    Tank(EntityType.ZOMBIE,
            new ItemStack(Material.GOLDEN_HELMET),
            new ItemStack(Material.GOLDEN_CHESTPLATE),
            new ItemStack(Material.GOLDEN_LEGGINGS),
            new ItemStack(Material.GOLDEN_BOOTS),
            new ItemStack(Material.WOODEN_AXE),
            0.3d, 200d,0.5f,1, 1,
            TankTrait.class, MeleeAttackGoal.class
    ),

    Damager(
            EntityType.VINDICATOR,
            new ItemStack(Material.GOLDEN_HELMET),
            new ItemStack(Material.GOLDEN_CHESTPLATE),
            new ItemStack(Material.GOLDEN_LEGGINGS),
            new ItemStack(Material.GOLDEN_BOOTS),
            new ItemStack(Material.DIAMOND_AXE),
        2d, 15d,0.9f, 1, 50,
            DamagerTrait.class, MeleeAttackGoal.class
    ),

    Support(
            EntityType.WITCH,
            new ItemStack(Material.GOLDEN_HELMET),
            new ItemStack(Material.GOLDEN_CHESTPLATE),
            new ItemStack(Material.GOLDEN_LEGGINGS),
            new ItemStack(Material.GOLDEN_BOOTS),
            new ItemStack(Material.IRON_SWORD),
            0.8d, 20d, 1.4f, 1, 1,
            SupportTrait.class, HealGoal.class
    );

    EntityType entity;

    ItemStack helm;
    ItemStack chest;
    ItemStack leg;
    ItemStack boots;
    ItemStack weapon;
    Double damage_modifier;
    Double max_hp;
    Float speed;
    Integer weapon_drop_chance;
    Integer armor_drop_chance;
    Class<?extends Raider> trait;
    Class<?extends Goal> goal;


    RaiderType(EntityType entity,
               ItemStack helm,
               ItemStack chest,
               ItemStack leg,
               ItemStack boots,
               ItemStack weapon,
               Double damage_modifier, Double max_hp, Float speed,
               Integer weapon_drop, Integer armor_drop,
               Class<?extends Raider> trait, Class<?extends Goal> goal)
    {
        this.entity = entity;

        this.helm = helm;
        this.chest = chest;
        this.leg = leg;
        this.boots = boots;

        this.damage_modifier = damage_modifier;
        this.max_hp = max_hp;
        this.speed = speed;
        this.weapon = weapon;
        this.armor_drop_chance = armor_drop;
        this.weapon_drop_chance = weapon_drop;

        this.trait = trait;
        this.goal = goal;
    }

    public void equip(LivingEntity entity)
    {
        entity.getEquipment().setHelmet(this.helm);
        entity.getEquipment().setHelmetDropChance(armor_drop_chance);

        entity.getEquipment().setChestplate(this.chest);
        entity.getEquipment().setChestplateDropChance(armor_drop_chance);

        entity.getEquipment().setLeggings(this.leg);
        entity.getEquipment().setLeggingsDropChance(armor_drop_chance);

        entity.getEquipment().setBoots(this.boots);
        entity.getEquipment().setBootsDropChance(armor_drop_chance);

        entity.getEquipment().setItemInMainHand(weapon);
        entity.getEquipment().setItemInMainHandDropChance(weapon_drop_chance);
    }

    public Double get_hp()
    {
        return this.max_hp;
    }
    public Double get_damage()
    {
        return this.damage_modifier;
    }
    public EntityType get_entity()
    {
        return this.entity;
    }
    public Float get_speed()
    {
        return this.speed;
    }

    public Class<?extends Raider> get_trait()
    {
        return this.trait;
    }
    public Class<?extends Goal> get_goal() {
        return this.goal;
    }

    public void setup(NPC npc)
    {
        LivingEntity entity = (LivingEntity)npc.getEntity();
        equip( entity );

        AttributeInstance health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        health.setBaseValue(get_hp());
        entity.setHealth(get_hp());

        AttributeInstance damage = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        damage.setBaseValue(get_damage());

        npc.getNavigator().getLocalParameters().baseSpeed(speed);
        //AttributeInstance speed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        //speed.setBaseValue(get_speed());
    }


}
