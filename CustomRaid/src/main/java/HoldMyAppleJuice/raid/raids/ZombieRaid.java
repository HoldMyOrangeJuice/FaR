package HoldMyAppleJuice.raid.raids;

import HoldMyAppleJuice.CustomRaids;
import HoldMyAppleJuice.raid.RaidType;
import HoldMyAppleJuice.raid.managers.PlayerKarmaManager;
import HoldMyAppleJuice.raid.villagers.traits.RaidParticipant;
import HoldMyAppleJuice.utils.Chat;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
        spawn_raiders();
    }

    public void loose()
    {
        for (Player player : PlayerKarmaManager.getPlayersNearby(location, 100))
        {
            Integer karma_lost = PlayerKarmaManager.updatePlayerKarma(player, -raidType.get_loss_penalty());
            Chat.message(player, ChatColor.DARK_RED + "Вы потерпели поражение. Потеряно", karma_lost.toString(), ChatColor.DARK_RED + "очков репутации." );
        }
        raidStatusBar.setTitle("Поражение");
    }

    public void win()
    {
        for (Player player : players_participated)
        {
            Integer karma_gained = PlayerKarmaManager.updatePlayerKarma(player, raidType.get_win_reward());
            Chat.message(player, ChatColor.GOLD + "Вы победили! Получено", karma_gained.toString(), ChatColor.GOLD + "очков репутации." );
        }
        raidStatusBar.setTitle("Победа");
    }

    @Override
    public void onEnd()
    {
        Chat.message(players_participated, ChatColor.GOLD + "Ивент окнчен!" );
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CustomRaids.plugin, new Runnable() {
            public void run() {
                remove_bar();
            }
        }, 1000);
    }

    @Override
    public void handle_raider_death(NPC raider)
    {
        Collection<Player> players_nearby = PlayerKarmaManager.getPlayersNearby(raider.getEntity().getLocation(), 100);
        PlayerKarmaManager.updatePlayerKarma( players_nearby, raidType.get_raider_death_reward() );
    }

    public void handle_victim_death(NPC victim)
    {
        Bukkit.broadcastMessage(victim.getTrait(RaidParticipant.class).participant_name + " died!");
        PlayerKarmaManager.updateKarmaAtLocation(victim.getStoredLocation(), 20, -raidType.get_villager_death_penalty());
    }

    @Override
    public void handle_player_damage_raider(Player player, NPC raider, Double damage)
    {
        // too op. could be abused
        //Integer karma = (int) Math.floor(damage);
        //PlayerKarmaManager.updatePlayerKarma(player, karma);
    }

    public void handle_player_kill_raider(Player player, NPC raider, Double damage)
    {
        Integer karma = raidType.get_raider_death_reward();
        PlayerKarmaManager.updatePlayerKarma(player, karma);
    }
}
