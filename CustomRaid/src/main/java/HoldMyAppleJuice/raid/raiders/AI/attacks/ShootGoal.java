package HoldMyAppleJuice.raid.raiders.AI.attacks;

import HoldMyAppleJuice.raid.managers.RaidManager;
import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class ShootGoal implements RaiderAttackGoal {

    NPC raider;
    LivingEntity entity;
    NPC victim;
    boolean is_attacking=false;
    RaiderType type;
    Long last_shot = System.currentTimeMillis();
    Long delay = 3000L;

    public ShootGoal(NPC raider, RaiderType type)
    {
        this.raider = raider;
        this.type = type;
        entity = (LivingEntity) raider.getEntity();
    }

    public void reset() {

    }

    public void run(GoalSelector goalSelector) {

        if (raider.getNavigator().isNavigating() && victim.isSpawned() && raider.getEntity().getLocation().distance(victim.getEntity().getLocation())>10)
        {
            // everything fine, keep going
            is_attacking = false;
        }
        else
        {
            if (raider.getNavigator().isNavigating() && victim.isSpawned() && raider.getEntity().getLocation().distance(victim.getEntity().getLocation())<=10)
            {

                if (entity.hasLineOfSight(victim.getEntity()))
                {
                    raider.getNavigator().cancelNavigation();
                    shoot();
                }
                is_attacking = true;
                return;
            }
            else if (victim==null ||
                    (!raider.getNavigator().isNavigating() &&
                            victim!=null &&
                            victim.isSpawned() &&
                            raider.getEntity().getLocation().distance(victim.getEntity().getLocation())>10))
            {
                if (navigate())
                {

                    // new target
                }else
                {
                    // no targets

                }
                return;
            }
            else if (victim.isSpawned())
                {

                    shoot();
                }
                else
                {
                    navigate();
                }
        }
    }

    public boolean shouldExecute(GoalSelector goalSelector)
    {
        return true;
    }

    private boolean navigate()
    {
        ArrayList<NPC> potential_victims = RaidManager.getParticipantsNearby(raider.getEntity().getLocation(), 100);

        if (potential_victims.size() > 0)
        {
            NPC closest = null;
            Double dist = Double.MAX_VALUE;
            for (NPC victim : potential_victims)
            {
                if (raider.getEntity().getLocation().distance(victim.getEntity().getLocation()) < dist)
                {
                    closest = victim;
                    dist = raider.getEntity().getLocation().distance(victim.getEntity().getLocation());
                    raider.getNavigator().setTarget(victim.getEntity(), false);
                }
            }
            this.victim = closest;
            return true;
        }
        return false;
    }

    private void shoot()
    {
        if (System.currentTimeMillis() - last_shot > delay)
        {


//            DataWatcher data = new DataWatcher(getNPC());
//            data.add(16, 0);
//            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(804, data, true);
//            for(Player player : Coc.getPlugin().getServer().getOnlinePlayers()){
//                CraftPlayer cp = (CraftPlayer) player;
//                cp.getHandle().playerConnection.sendPacket(packet);
//            }

            Vector dirBetweenLocations = victim.getEntity().getLocation().toVector().subtract(entity.getLocation().toVector());
            Location loc = entity.getLocation();
            loc.setDirection(dirBetweenLocations);
            entity.teleport(loc);
            if (entity.hasLineOfSight(victim.getEntity()))
            {
                entity.launchProjectile(Arrow.class);
                last_shot = System.currentTimeMillis();
            }
            else
            {
                //raider.getDefaultGoalController().addGoal(new WonderGoal());
                raider.getNavigator().setTarget(victim.getEntity(), false);
            }

        }
    }

    public NPC set_random_victim()
    {
        ArrayList<NPC> potential_victims = RaidManager.getParticipantsNearby(raider.getEntity().getLocation(), 100);
        if (potential_victims.size()>0)
        {
            victim = potential_victims.get(new Random().nextInt(potential_victims.size()-1));
            set_victim(victim);
            return victim;
        }
        return null;
    }

    public boolean is_attacking() {
        return is_attacking;
    }

    public void set_victim(NPC npc) {
        this.victim = npc;
        raider.getNavigator().setTarget(npc.getEntity(), false);
    }
}
