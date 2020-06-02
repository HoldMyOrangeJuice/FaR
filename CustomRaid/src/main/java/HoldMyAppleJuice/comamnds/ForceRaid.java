package HoldMyAppleJuice.comamnds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import HoldMyAppleJuice.raid.managers.RaidManager;
import HoldMyAppleJuice.raid.RaidType;

public class ForceRaid implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player player = sender.getServer().getPlayer(sender.getName());
        if (args.length!=0 && args[0].toLowerCase().equals("force"))
        {
            RaidManager.startRaid(Bukkit.getServer().getPlayer(sender.getName()).getLocation(), RaidType.EASY);
            player.sendMessage(ChatColor.DARK_RED + "You force-started HoldMyAppleJuice.raid at " + ChatColor.AQUA + + player.getLocation().getX() + player.getLocation().getZ());
        }
        return true;
    }
}
