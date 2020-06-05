package HoldMyAppleJuice.raid.raiders.AI.Support;

import HoldMyAppleJuice.raid.managers.RaidManager;
import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;

import java.util.ArrayList;
import java.util.Random;

public class HealGoal implements Goal {

    NPC support;
    RaiderType type;
    Long last_thrown=System.currentTimeMillis();
    Long throw_delay = 5000L;
    Long move_delay = 1000L;
    Long last_throw_move = System.currentTimeMillis();

    public HealGoal(NPC support, RaiderType type)
    {
        this.support = support;
        this.type = type;
    }

    public void reset()
    {

    }

    public void run(GoalSelector goalSelector)
    {
        // move towards low raiders
        if (System.currentTimeMillis() -  last_throw_move > move_delay)
        {
            navigate();
        }
        // throw potion if close
        throw_potion();
//        if (throw_potion())
//        {
//
//        }
//        else
//        {
//            navigate();
//        }
    }

    public boolean shouldExecute(GoalSelector goalSelector)
    {
        return true;
    }

    public NPC getActiveRaiderNearby(Integer radius)
    {
        ArrayList<NPC> raiders_close = RaidManager.getRaidersNearby(support.getEntity().getLocation(), radius);
        ArrayList<NPC > to_del = new ArrayList<NPC>();
        for (NPC raider_close : raiders_close)
        {
            if (raider_close.hasTrait(RaiderType.Support.get_trait()))
            {
                to_del.add(raider_close);
            }
        }
        for (NPC n : to_del)
        {
            raiders_close.remove(n);
        }

        if (raiders_close.size() > 0)
        {
            NPC best = raiders_close.get(0);
            Double best_coef = 0d;

            for (NPC raider : raiders_close) {
                Double distance = best.getEntity().getLocation().distance(support.getEntity().getLocation());
                LivingEntity entity = ((LivingEntity)(raider.getEntity()));
                AttributeInstance max_health_attr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                Double max_health = max_health_attr.getBaseValue();
                Double actual_health = entity.getHealth();
                Double health_lost = max_health - actual_health;
                Double health_lost_part = health_lost / max_health;


                Double coef = health_lost_part * distance;

                if (coef >= best_coef)
                {
                    best = raider;
                    best_coef = coef;
                }


//                if (!prefer_low &&  < dist)  {
//                    Bukkit.getServer().broadcastMessage("closest guy for now " + raider.getId());
//                    best = raider;
//                    dist = best.getEntity().getLocation().distance(support.getEntity().getLocation());
//                }
//                Bukkit.broadcastMessage(prefer_low +  " and " + hp + " > " + ((LivingEntity)raider.getEntity()).getHealth());
//                if (prefer_low && hp > ((LivingEntity)raider.getEntity()).getHealth()  ) {
//                    best = raider;
//                    Bukkit.getServer().broadcastMessage("lowest guy for now " + raider.getId());
//                    hp =((LivingEntity)raider.getEntity()).getHealth();
//                }
}

            return best;
        }
        else
        {
            return null;
        }
    }

    public NPC getRandomRaiderNearby(Integer radius)
    {
        ArrayList<NPC> raiders_close = RaidManager.getRaidersNearby(support.getEntity().getLocation(), radius);
        if (raiders_close.size() > 0)
        {
            return raiders_close.get(new Random().nextInt(raiders_close.size()-1));
        }
        return null;
    }

    public boolean throw_potion()
    {
        if (System.currentTimeMillis() - last_thrown < throw_delay) return false;

        NPC mate = getActiveRaiderNearby(5);

        if (mate != null && mate.isSpawned())
        {
            support.getNavigator().cancelNavigation();
            last_thrown = System.currentTimeMillis();

            ((LivingEntity)support.getEntity()).launchProjectile(ThrownPotion.class);

            LivingEntity entity = ((LivingEntity)(mate.getEntity()));
            AttributeInstance max_health_attr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            Double max_health = max_health_attr.getBaseValue();
            Double actual_health = entity.getHealth();
            Double health_lost = max_health - actual_health;
            entity.setHealth(actual_health + health_lost * 0.5);

            mate.setName( RaidManager.getRaiderType(mate).get_name() + " HP: " + (Math.floor(entity.getHealth())<0?0:Math.floor(entity.getHealth())));
            entity.playEffect(EntityEffect.ENTITY_POOF);


            return true;
        }
        return false;

    }

    public boolean navigate()
    {
        if (support.getNavigator().isNavigating())
        {
            return false;
        }

        NPC mate = getActiveRaiderNearby(100);
        if (mate!=null)
        {
            if (support.getEntity().getLocation().distance(mate.getEntity().getLocation())< 4)
            {
                return false;
            }
            support.getNavigator().setTarget(mate.getEntity(), false);
            return false;
        }
        return  false;
    }
}
