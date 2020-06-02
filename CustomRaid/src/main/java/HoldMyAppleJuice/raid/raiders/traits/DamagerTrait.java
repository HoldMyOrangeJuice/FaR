package HoldMyAppleJuice.raid.raiders.traits;

import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import net.citizensnpcs.api.util.DataKey;

public class DamagerTrait extends Raider
{

    public DamagerTrait() {
        super("damager", RaiderType.Damager);
        //RaiderType.Damager.setup(npc);
    }

    public void load_data(DataKey key) {

    }

    public void save_data(DataKey key) {

    }

    public void run_task() {

    }

    public void run_on_attach() {

    }

    public void run_on_remove() {

    }

    public void run_on_spawn()
    {
        //RaiderType.Damager.set(npc);
        //mainGoal = new MeleeAttackGoal(npc, type);
        //npc.getDefaultGoalController().addGoal(mainGoal, 1);
    }

    public void run_on_despawn() {

    }
}
