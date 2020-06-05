package HoldMyAppleJuice.raid.raiders.traits;

import HoldMyAppleJuice.CustomRaids;
import HoldMyAppleJuice.raid.managers.RaidManager;
import HoldMyAppleJuice.raid.raiders.AI.movement.MoveStuckAction;
import HoldMyAppleJuice.raid.raiders.AI.attacks.RaiderAttackGoal;
import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalController;
import net.citizensnpcs.api.ai.StuckAction;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Raider extends Trait {


    public Raider(String trait_name, RaiderType type)
    {
        super(trait_name);
        plugin = JavaPlugin.getPlugin(CustomRaids.class);
        this.type = type;
        System.out.println("tarit" +  trait_name);
        System.out.println("type" + type);

        //LivingEntity entity = (LivingEntity) npc.getEntity();

    }

    public NPC victim;
    public RaiderAttackGoal mainGoal;
    public StuckAction stuckAction;
    public RaiderType type;

    public abstract void load_data(DataKey key);
    public abstract void save_data(DataKey key);
    public abstract void run_task();
    public abstract void run_on_attach();
    public abstract void run_on_remove();
    public abstract void run_on_spawn();
    public abstract void run_on_despawn();

    CustomRaids plugin = null;
    //@Persist("settindname") boolean automaticallyPersistedSetting = false;


    public void load(DataKey key)
    {
        load_data(key);
    }

    public void save(DataKey key)
    {
        save_data(key);
    }


    // Called every tick
    @Override
    public void run()
    {
        run_task();
    }


    @Override
    public void onAttach() {
        plugin.getServer().getLogger().info(npc.getName() + " has been assigned Raider!");


        run_on_attach();
    }

    @Override
    public void onDespawn()
    {
        run_on_despawn();
    }


    @Override
    public void onSpawn()
    {
        Object goal = null;
        Class<?extends Goal> goal_class = type.get_goal();
        Constructor<?> constructor = null;
        try {
            constructor = goal_class.getConstructor(NPC.class, RaiderType.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            assert constructor != null;
            goal = constructor.newInstance(npc, type);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (goal instanceof Goal)
        {
            System.out.println("goal is goal " + goal + "\n " + npc);
            GoalController goalController = npc.getDefaultGoalController();
            goalController.addGoal((Goal) goal, 1);
        }
        else
        {
            System.out.println("goal is object weird ");
        }
        npc.setProtected(false);
        stuckAction = new MoveStuckAction(type);
        npc.getNavigator().getLocalParameters().stuckAction(stuckAction);
        npc.getNavigator().getLocalParameters().stationaryTicks(200);
        npc.getNavigator().getLocalParameters().range(100);

        run_on_spawn();
    }

    public static void onEntitySpawn(NPC npc, RaiderType type)
    {
        ////////
        System.out.println("setting up " + type + " " +npc + " " + npc.getEntity() + " " + npc.isSpawned());
        type.setup(npc);
        ((LivingEntity)(npc.getEntity())).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000, 1, false, false, false));
        ///////
    }


    @Override
    public void onRemove()
    {
        npc.getNavigator().cancelNavigation();
        npc.getDefaultGoalController().cancelCurrentExecution();
        npc.getDefaultGoalController().clear();

        run_on_remove();
    }

    @EventHandler
    public void npcdamage(NPCDamageByEntityEvent event)
    {
        if (event.getNPC()==victim && event.getDamager() == npc.getEntity())
        {

        }
    }

    @EventHandler
    public void raiderDied(NPCDeathEvent event)
    {
        if (event.getNPC() != npc) return;
        event.getNPC().destroy();
    }

    @EventHandler
    public void raiderDamaged(EntityDamageEvent event)
    {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
        {
            if (CitizensAPI.getNPCRegistry().getNPC(event.getEntity()) == npc)
            {
                double hp = Math.floor(((LivingEntity)npc.getEntity()).getHealth()- event.getDamage());
                npc.setName(RaidManager.getRaiderType(npc).get_name() + " HP: " + (hp<0?0:hp) );
            }

        }
    }
    @EventHandler
    public void raiderHealed(EntityRegainHealthEvent event)
    {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
        {
            if (CitizensAPI.getNPCRegistry().getNPC(event.getEntity()) == npc)
            {
                npc.setName("HP: " + Math.floor(((LivingEntity)npc.getEntity()).getHealth()) + event.getAmount());
            }

        }
    }

}
