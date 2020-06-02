package HoldMyAppleJuice.raid.raiders.traits;

import HoldMyAppleJuice.CustomRaids;
import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.plugin.java.JavaPlugin;

public class SupportTrait extends Raider
{
    CustomRaids plugin = CustomRaids.plugin;

    public SupportTrait()
    {
        super("support", RaiderType.Support);
        //RaiderType.Support.setup(npc);
        plugin = JavaPlugin.getPlugin(CustomRaids.class);
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

        //npc.getDefaultGoalController().addGoal(new HealGoal(), 1);
        //npc.getDefaultGoalController().addGoal(new PoisonGoal(), 2);
    }

    public void run_on_despawn() {

    }





}