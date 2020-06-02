package HoldMyAppleJuice.raid.raids;

import HoldMyAppleJuice.CustomRaids;
import HoldMyAppleJuice.raid.managers.PlayerKarmaManager;
import HoldMyAppleJuice.raid.managers.RaidManager;
import HoldMyAppleJuice.raid.villagers.traits.RaidParticipant;
import HoldMyAppleJuice.utils.Chat;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import HoldMyAppleJuice.raid.RaidType;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ZombieRaid extends Raid
{
    public ZombieRaid(RaidType type, Location at)
    {
        super(type, at);
    }
    public void start()
    {
        System.out.println("raiders spawned: returned value:" + this.spawn_raiders() + " \nactual value: " + this.alive_raiders);
    }

    public void loose()
    {
        Bukkit.getServer().broadcastMessage("LOST");
        raidStatusBar.setTitle("Loose");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CustomRaids.plugin, new Runnable() {
            public void run() {
                remove_bar();
            }
        }, 1000);
    }

    public void win()
    {
        Bukkit.getServer().broadcastMessage("WON");
        raidStatusBar.setTitle("Win");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CustomRaids.plugin, new Runnable() {
            public void run() {
                remove_bar();
            }
        }, 1000);

    }

    @Override
    public void handle_raider_death(NPC raider) {
        Collection<Player> players_nearby = PlayerKarmaManager.getPlayersNearby(raider.getEntity().getLocation(), 100);
        PlayerKarmaManager.updatePlayerKarma(players_nearby, 10);
    }

    public void handle_victim_death(NPC victim) {
        Bukkit.broadcastMessage(victim.getTrait(RaidParticipant.class).participant_name + " died!");
        //Collection<Player> players = PlayerKarmaManager.getPlayersNearby(victim.getEntity().getLocation(), 100);
        //PlayerKarmaManager.update_karma(players, -100);
        Iterable<Player> affected_players = PlayerKarmaManager.updateKarmaAtLocation(victim.getStoredLocation(), 20, -100);
    }

    @Override
    public void handle_player_damage_raider(Player player, NPC raider, Double damage)
    {
        Integer karma = (int)(50*damage);
        PlayerKarmaManager.updatePlayerKarma(player, karma);
    }

    public void handle_player_kill_raider(Player player, NPC raider, Double damage)
    {
        Integer karma = (int)(1000*damage);
        PlayerKarmaManager.updatePlayerKarma(player, karma);
    }
}
