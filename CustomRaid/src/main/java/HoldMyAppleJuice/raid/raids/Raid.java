package HoldMyAppleJuice.raid.raids;

import HoldMyAppleJuice.CustomRaids;

import HoldMyAppleJuice.raid.managers.PlayerKarmaManager;
import HoldMyAppleJuice.raid.managers.RaidManager;
import HoldMyAppleJuice.raid.raiders.traits.ArcherTrait;
import HoldMyAppleJuice.raid.raiders.traits.Raider;
import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import HoldMyAppleJuice.raid.villagers.traits.RaidParticipant;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import HoldMyAppleJuice.raid.RaidType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class Raid implements Listener
{
    public Location location;
    public ArrayList<NPC> alive_raiders = new ArrayList<NPC>();
    public RaidType raidType;
    public ArrayList<NPC> alive_defenders = new ArrayList<NPC>();
    public ArrayList<Player> players_participated = new ArrayList<Player>();
    public boolean active;
    public BossBar raidStatusBar;

    public Raid(RaidType type, Location at)
    {
        this.active = true;
        this.raidType = type;
        this.location = at;

        start();
        Bukkit.getServer().broadcastMessage(ChatColor.DARK_GRAY + "[Рейд] >> " + ChatColor.DARK_PURPLE + "Рейд начат на координатах" + ChatColor.DARK_RED + "x:" +  + location.getBlockX() + " y:"  + location.getBlockY()+ ChatColor.DARK_PURPLE + "! Защити торговцев и подними репутацию!");

        // register defenders on raid spawn
        add_victims(RaidManager.getParticipantsNearby(location, 300));

        raidStatusBar = Bukkit.createBossBar("Raiders left: " + alive_raiders.size() + " Defenders left: " + alive_defenders.size(), BarColor.BLUE, BarStyle.SEGMENTED_20);

        //
        for (Player player : PlayerKarmaManager.getPlayersNearby(location, 300))
        {
            raidStatusBar.addPlayer(player);
        }

        CustomRaids.plugin.getServer().getPluginManager().registerEvents(this, CustomRaids.plugin);
    }

    public abstract void start();
    public abstract void loose();
    public abstract void win();
    public abstract void onEnd();

    public abstract void handle_raider_death(NPC raider);
    public abstract void handle_victim_death(NPC victim);
    public abstract void handle_player_damage_raider(Player player, NPC raider, Double damage);
    public abstract void handle_player_kill_raider(Player player, NPC raider, Double damage);


    @EventHandler
    public void onNPCdamage(NPCDamageByEntityEvent event)
    {
        if (!active)return;
        NPC npc = event.getNPC();
        if (belongs_to_raid(npc) && event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            if (!players_participated.contains(player))
            {
                players_participated.add(player);
                raidStatusBar.addPlayer(player);
            }
            if ( ((LivingEntity)(npc.getEntity())).getHealth()-event.getDamage()>0 ) {
                // raider damaged by player
                handle_player_damage_raider(player, event.getNPC(), event.getDamage());
            }
            else
            {
                // raider killed by player
                handle_player_kill_raider(player, npc, event.getDamage());
            }
        }
    }


    @EventHandler
    public void onNPCDeath(NPCDeathEvent event)
    {
        if (!active)return;
        NPC npc = event.getNPC();
        if (belongs_to_raid(npc))
        {
            // raider died
            handle_raider_death(npc);
            remove_raider(npc);
            update_bar();

            if (alive_raiders.isEmpty())
            {
                win();
                end();
            }
        }
        if (npc.hasTrait(RaidParticipant.class))
        {
            // villager died
            handle_victim_death(npc);
            remove_victim(npc);
            update_bar();

            if (alive_defenders.isEmpty())
            {
                loose();
                end();
            }
        }
    }

    public void end()
    {
        if (!active)return;

        onEnd();


        for (NPC raider : alive_raiders)
        {
            raider.destroy();
        }
        active = false;
        RaidManager.raidsInProgress.remove(this.hashCode());

    }

    public void remove_bar()
    {

        for (Player player :  raidStatusBar.getPlayers())
        {
            raidStatusBar.removePlayer(player);
        }

    }
    public void update_bar()
    {
        if (!active)return;
        raidStatusBar.setTitle("Raiders left: " + alive_raiders.size() + " Defenders left: " + alive_defenders.size());
    }

    public void add_raiders(Iterable<NPC> raiders)
    {
        if (!active)return;
        for (NPC raider : raiders)
        {
            add_raider(raider);
        }
    }

    public void add_raider(NPC raider)
    {
        if (!active)return;
        //if (!raider.hasTrait(Raider.class))return;
        if (!raider.isSpawned())return;
        if (alive_raiders.contains(raider))return;

        this.alive_raiders.add(raider);
    }

    public void remove_raider(NPC raider)
    {
        if (!active)return;

        if (alive_raiders.contains(raider))
        {
            alive_raiders.remove(raider);
            //dead_raiders.add(raider);
        }
    }

    public void add_victim(NPC victim)
    {
        if (!active)return;
        if (!victim.hasTrait(RaidParticipant.class))return;
        if (!victim.isSpawned())return;
        if (alive_defenders.contains(victim))return;

        this.alive_defenders.add(victim);
    }

    public void add_victims(Iterable<NPC> victims)
    {
        if (!active)return;
        for (NPC victim : victims)
        {
            add_victim(victim);
        }
    }

    public void remove_victim(NPC victim) {
        if (!active)return;
        if (victim.isSpawned()) {
            victim.despawn();
        }

        while (alive_defenders.contains(victim)) {
            alive_defenders.remove(victim);
        }

    }

    public boolean belongs_to_raid(NPC npc)
    {
        if (!active)return false;
        for (NPC raider : alive_raiders)
        {
            if (npc == raider) {
                return true;
            }
        }
        return false;
    }

    public HashSet<NPC> spawn_raiders()
    {
        if (!active)return null;
        ArrayList<Location> possible_spawn_locations = new ArrayList<Location>();

        for (int x_offset = -this.raidType.get_spawn_radius(); x_offset <= this.raidType.get_spawn_radius(); x_offset ++) {
            for (int z_offset = -this.raidType.get_spawn_radius(); z_offset <= this.raidType.get_spawn_radius(); z_offset++) {
                for (int y = 100; y >= 50; y--)
                {
                    Location loc_stand = new Location(location.getWorld(), location.getBlockX() + x_offset, y - 1, location.getBlockZ() + z_offset);
                    Location loc_foot = new Location(location.getWorld(), location.getBlockX() + x_offset, y, location.getBlockZ() + z_offset);
                    Location loc_head = new Location(location.getWorld(), location.getBlockX() + x_offset, y + 1, location.getBlockZ() + z_offset);
                    if (loc_stand.getWorld().getBlockAt(loc_stand).getType().isSolid() &&
                        loc_foot.getWorld().getBlockAt(loc_foot).getType() == Material.AIR &&
                        loc_head.getWorld().getBlockAt(loc_head).getType() == Material.AIR)
                    {
                        possible_spawn_locations.add(loc_foot);
                        break;
                    }
                }
            }
        }

        HashSet<NPC> raiders = new HashSet<NPC>();
        for (RaiderType raider_type : raidType.getRaiderTypes())
        {
            for (int raider_index=0; raider_index < raidType.getRaiderCount(raider_type); raider_index++)
            {
                Location spawn_location = possible_spawn_locations.get((new Random()).nextInt(possible_spawn_locations.size()));

                NPC raider = CitizensAPI.getNPCRegistry().createNPC(raider_type.get_entity(), raider_type.get_name() + " HP: " + raider_type.get_hp());
                raiders.add(raider);
                raider.addTrait(raider_type.get_trait());
                raider.spawn(spawn_location);
                this.add_raider(raider);
                Raider.onEntitySpawn(raider, raider_type);
            }
        }
        return raiders;
    }

}
