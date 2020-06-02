package HoldMyAppleJuice.raid.managers;

import HoldMyAppleJuice.raid.RaidType;
import HoldMyAppleJuice.raid.raiders.traits.Raider;
import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import HoldMyAppleJuice.raid.villagers.traits.RaidParticipant;
import HoldMyAppleJuice.raid.villagers.traits.Trader;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import HoldMyAppleJuice.raid.raids.Raid;
import HoldMyAppleJuice.raid.raids.ZombieRaid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class RaidManager
{

    public static void startRaid(Location location, RaidType type)
    {
        Raid raid = new ZombieRaid(type, location);
        //getParticipantsNearby(location, 100);
    }

    public static void highlightVillagersNearby(Location loc, Integer radius)
    {
        for (Entity villager : getVillagersNearby(loc, radius))
        {
            try {
                ((LivingEntity) villager).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 10, false, false));
            }
            catch (Exception e)
            {

            }
        }
    }

    public static Collection<Entity> getVillagersNearby(Location center, Integer radius)
    {
        return center.getNearbyEntities(radius, radius, radius);//getNearbyEntitiesByType(Villager.class, radius);
    }


    public static ArrayList<NPC> getParticipantsNearby(Location location, Integer radius)
    {
        ArrayList<NPC> npc_defenders = new ArrayList<NPC>();
        for (NPC npc : CitizensAPI.getNPCRegistry())
        {
            if (npc.isSpawned() && npc.getEntity().getLocation().distance(location) < radius &&
                    npc.hasTrait(RaidParticipant.class) )
            {
                npc_defenders.add(npc);
                location.getWorld().spawn(npc.getEntity().getLocation(), Firework.class);
            }
        }
        return npc_defenders;
    }

    public static ArrayList<NPC> getTraderParticipantsNearby(Location loc, Integer radius)
    {
        ArrayList<NPC> traders = new ArrayList<NPC>();
        ArrayList<NPC> npc_defenders = getParticipantsNearby(loc, radius);
        for (NPC defender : npc_defenders)
        {
            if (defender.hasTrait(Trader.class) )
            {
                traders.add(defender);
            }
        }
        return traders;
    }

    public static ArrayList<NPC> getRaidersNearby(Location loc, Integer radius)
    {
        ArrayList<NPC> raiders = new ArrayList<NPC>();
        for (NPC npc : CitizensAPI.getNPCRegistry())
        {
            for (RaiderType type : RaiderType.values())
            {
                Class<?extends Raider> trait = type.get_trait();
                if (npc.isSpawned() && npc.getEntity().getLocation().distance(loc) < radius &&
                        npc.hasTrait(trait) )
                {
                    raiders.add(npc);
                    loc.getWorld().spawn(npc.getEntity().getLocation(), Firework.class);
                }
            }
        }
        return raiders;
    }
}
