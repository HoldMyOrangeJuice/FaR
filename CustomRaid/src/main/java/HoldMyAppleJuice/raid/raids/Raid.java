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
    public boolean active;
    public BossBar raidStatusBar;

    public Raid(RaidType type, Location at)
    {
        this.raidType = type;
        location = at;
        start();
        active = true;
        Bukkit.getServer().broadcastMessage("Started raid " + this.hashCode());

        //add_raiders(RaidManager.getRaidersNearby(location, 100));
        // raiders added on spawn
        add_victims(RaidManager.getParticipantsNearby(location, 300));

        raidStatusBar = Bukkit.createBossBar("Raiders left: " + alive_raiders.size() + " Defenders left: " + alive_defenders.size(), BarColor.BLUE, BarStyle.SEGMENTED_20);

        for (Player player : PlayerKarmaManager.getPlayersNearby(location, 300))
        {
            raidStatusBar.addPlayer(player);

        }

        for (NPC def : alive_defenders)
        {
            Bukkit.getServer().broadcastMessage("init defender added " + def.getId());
        }


        //player_defenders_nearby.addAll(PlayerKarmaManager.getPlayersNearby(location, 100));
        //player_defenders.addAll(Bukkit.getServer().getOnlinePlayers());

        CustomRaids.plugin.getServer().getPluginManager().registerEvents(this, CustomRaids.plugin);
    }

    public abstract void start();
    public abstract void loose();
    public abstract void win();

    public abstract void handle_raider_death(NPC raider);
    public abstract void handle_victim_death(NPC victim);
    public abstract void handle_player_damage_raider(Player player, NPC raider, Double damage);
    public abstract void handle_player_kill_raider(Player player, NPC raider, Double damage);

    @EventHandler
    public void onNPCdamage(NPCDamageByEntityEvent event)
    {
        NPC npc = event.getNPC();
        if (belongs_to_raid(npc) && event.getDamager() instanceof Player)
        {
            if ( ((LivingEntity)(npc.getEntity())).getHealth()-event.getDamage()>0 ) {
                // raider damaged by player
                handle_player_damage_raider((Player) event.getDamager(), event.getNPC(), event.getDamage());
            }
            else
            {
                // raider killed by player
                handle_player_kill_raider((Player)event.getDamager(), npc, event.getDamage());
            }
        }
    }


    @EventHandler
    public void onNPCDeath(NPCDeathEvent event)
    {
        NPC npc = event.getNPC();
        if (belongs_to_raid(npc))
        {
            // raider died

            //add_raiders(RaidManager.getRaidersNearby(location, 100));
            remove_raider(npc);
            handle_raider_death(npc);
            Bukkit.getServer().broadcastMessage("" + ChatColor.BOLD + ""+ ChatColor.DARK_RED + alive_raiders.size() + " raiders left");
            update_bar();
            if (alive_raiders.isEmpty() && active)
            {
                win();
                end();
            }
        }
        if (npc.hasTrait(RaidParticipant.class))
        {
            // villager died
            //add_victims(RaidManager.getParticipantsNearby(location, 100));
            handle_victim_death(npc);
            remove_victim(npc);

            Bukkit.getServer().broadcastMessage("" + ChatColor.BOLD + ""+ ChatColor.DARK_GREEN + alive_defenders.size() + " defenders left");
            update_bar();
            if (alive_defenders.isEmpty() && active)
            {
                loose();
                end();
            }
        }
    }

    public void end()
    {
        for (NPC raider : alive_raiders)
        {
            raider.destroy();
        }
        active = false;

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
        raidStatusBar.setTitle("Raiders left: " + alive_raiders.size() + " Defenders left: " + alive_defenders.size());
    }

    public void add_raiders(Iterable<NPC> raiders)
    {
        for (NPC raider : raiders)
        {
            add_raider(raider);
        }
    }

    public void add_raider(NPC raider)
    {

        //if (!raider.hasTrait(Raider.class))return;
        if (!raider.isSpawned())return;
        if (alive_raiders.contains(raider))return;

        this.alive_raiders.add(raider);
    }

    public void remove_raider(NPC raider)
    {
        Bukkit.getServer().broadcastMessage("remove raider from registry...");

        if (alive_raiders.contains(raider))
        {
            Bukkit.getServer().broadcastMessage("removed.");
            alive_raiders.remove(raider);
            //dead_raiders.add(raider);
        }
    }

    public void add_victim(NPC victim)
    {

        if (!victim.hasTrait(RaidParticipant.class))return;
        if (!victim.isSpawned())return;
        if (alive_defenders.contains(victim))return;

        this.alive_defenders.add(victim);
        Bukkit.getServer().broadcastMessage("added victim " + victim.getId());
    }

    public void add_victims(Iterable<NPC> victims)
    {
        for (NPC victim : victims)
        {
            add_victim(victim);
        }
    }

    public void remove_victim(NPC victim) {
        if (victim.isSpawned()) {
            victim.despawn();
        }

        Bukkit.getServer().broadcastMessage("try to remove victim");
        while (alive_defenders.contains(victim)) {
            alive_defenders.remove(victim);
            Bukkit.getServer().broadcastMessage("removed " + victim.getId());
        }

    }

    public boolean belongs_to_raid(NPC npc)
    {
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
        ArrayList<Location> possible_spawn_locations = new ArrayList<Location>();

        for (int x_offset = -this.raidType.get_spawn_radius(); x_offset <= this.raidType.get_spawn_radius(); x_offset ++) {
            for (int z_offset = -this.raidType.get_spawn_radius(); z_offset <= this.raidType.get_spawn_radius(); z_offset++) {
                for (int y = 100; y >= 50; y--)
                {
                    Location loc_stand = new Location(location.getWorld(), location.getBlockX() + x_offset, y - 1, location.getBlockZ() + z_offset);
                    Location loc_foot = new Location(location.getWorld(), location.getBlockX() + x_offset, y, location.getBlockZ() + z_offset);
                    Location loc_head = new Location(location.getWorld(), location.getBlockX() + x_offset, y + 1, location.getBlockZ() + z_offset);
                    if (loc_stand.getWorld().getBlockAt(loc_stand).getType() != Material.AIR &&
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

                NPC raider = CitizensAPI.getNPCRegistry().createNPC(raider_type.get_entity(), "HP: " + raider_type.get_hp());
                raiders.add(raider);
                Bukkit.getServer().broadcastMessage("registering trait "+ raider_type.get_trait());
                raider.addTrait(raider_type.get_trait());
                raider.spawn(spawn_location);
                this.add_raider(raider);
                Raider.onEntitySpawn(raider, raider_type);

            }
        }
        return raiders;
    }

}
