package HoldMyAppleJuice.raid.raiders.AI.movement;

import HoldMyAppleJuice.raid.managers.RaidManager;
import HoldMyAppleJuice.raid.raiders.traits.Raider;
import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import HoldMyAppleJuice.raid.raids.Raid;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.StuckAction;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;

public class MoveStuckAction implements StuckAction
{
    Integer fails = 0;
    NPC victim = null;
    NPC prev_victim = null;
    RaiderType type;

    public MoveStuckAction(RaiderType type){this.type=type;}

    public boolean run(NPC npc, Navigator navigator) {
        //Bukkit.getServer().broadcastMessage("stuck at " + npc.getEntity().getLocation());
        if (npc.hasTrait(Raider.class))
        {
            victim = npc.getTrait(this.type.get_trait()).mainGoal.set_random_victim();
             if (victim!=null)
             {
                 // set new target close enough
                 if (victim == prev_victim)
                 {
                     fails ++;
                 }
                 else
                 {
                     fails = 0;
                 }
             }
             else
             {
                 for (NPC raider_mate : RaidManager.getRaidersNearby(npc.getEntity().getLocation(), 100))
                 {
                     if (raider_mate.getTrait(Raider.class).mainGoal.is_attacking())
                     {
                         npc.getNavigator().setTarget(raider_mate.getEntity(), false);
                     }
                 }
             }
        }
        return true;
    }
}
