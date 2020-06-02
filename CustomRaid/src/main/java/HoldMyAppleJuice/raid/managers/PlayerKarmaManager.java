package HoldMyAppleJuice.raid.managers;

import HoldMyAppleJuice.CustomRaids;
import HoldMyAppleJuice.utils.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class PlayerKarmaManager
{

    public static void updatePlayerKarma(Collection<Player>players, Integer karma){
        for (Player player : players)
        {
            updatePlayerKarma(player, karma);
        }
    }

    public static Iterable<Player> updateKarmaAtLocation(Location loc, Integer radius, Integer karma){
        Collection<Player> affected_players = getPlayersNearby(loc, radius);

        for (Player player : affected_players)
        {
            updatePlayerKarma(player, karma);
        }
        return affected_players;
    }


    public static Collection<Player> getPlayersNearby(Location center, Integer radius)
    {
        return center.getNearbyEntitiesByType(Player.class, radius);
    }

    public static Integer getPlayerKarma(Player player)
    {
        return CustomRaids.config.load("Karma." + player.getUniqueId());
    }
    public static void updatePlayerKarma(Player player, Integer value){
        Integer karma = getPlayerKarma(player) + value;
        CustomRaids.config.save("Karma." + player.getUniqueId(), karma);

        if (value > 0)
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "+" + value + " karma");
        if (value < 0)
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED + "-" + value + " karma");
    }
}
