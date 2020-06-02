package HoldMyAppleJuice.comamnds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import HoldMyAppleJuice.raid.managers.RaidManager;

public class Highlight implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        RaidManager.highlightVillagersNearby(Bukkit.getServer().getPlayer(sender.getName()).getLocation(), 100);
        return true;
    }
}
