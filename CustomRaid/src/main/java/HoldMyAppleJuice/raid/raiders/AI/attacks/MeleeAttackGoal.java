package HoldMyAppleJuice.raid.raiders.AI.attacks;

import HoldMyAppleJuice.raid.managers.RaidManager;
import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Random;


public class MeleeAttackGoal implements RaiderAttackGoal {
    private Object state;
    private GoalSelector selector; // the current selector
    private NPC victim=null;
    private NPC raider;
    private RaiderType type;
    public boolean is_attacking = false;

    public MeleeAttackGoal(NPC raider, RaiderType type)
    {
        this.raider = raider;
        this.type = type;
    }

    public void reset()
    {
        state = null;
        // this method can be called at any time - tear down any state
    }

    public void run(GoalSelector selector)
    {
        if (shouldExecute(selector))
        {
            //Bukkit.getServer().broadcastMessage("navigating to " + victim.getEntity());
            raider.getNavigator().setTarget(victim.getEntity(), true);

        }
    }

    public boolean shouldExecute(GoalSelector selector) {

        if (victim!=null && victim.getEntity() != null && victim.isSpawned() && raider.getNavigator().isNavigating())
        {
            is_attacking = true;
                //Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN+ "everything is fine" + victim.getEntity());
                return true;
        }

        ArrayList<NPC> potential_victims = RaidManager.getParticipantsNearby(raider.getEntity().getLocation(), 100);

        if (potential_victims.size()>0)
        {
            NPC close_victim = null;
            Double distance = Double.MAX_VALUE;
            for (NPC victim : potential_victims)
            {
                if (raider.getEntity().getLocation().distance(victim.getEntity().getLocation())<distance || close_victim == null)
                {
                    close_victim = victim;
                    distance = raider.getEntity().getLocation().distance(victim.getEntity().getLocation());
                }
            }
            victim = close_victim;
            set_victim(close_victim);
            is_attacking = true;
            return true;
        }
        else
        {
            is_attacking = false;
            return false;
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

    public void set_victim(NPC victim)
    {
        raider.getTrait(type.get_trait()).victim = victim;
    }

}
