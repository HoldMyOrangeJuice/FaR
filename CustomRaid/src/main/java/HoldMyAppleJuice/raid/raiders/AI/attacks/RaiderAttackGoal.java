package HoldMyAppleJuice.raid.raiders.AI.attacks;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.npc.NPC;

public interface RaiderAttackGoal extends Goal
{
     NPC set_random_victim();
     boolean is_attacking();
     void set_victim(NPC npc);
}
