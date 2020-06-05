package HoldMyAppleJuice.comamnds;

import HoldMyAppleJuice.CustomRaids;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import HoldMyAppleJuice.raid.managers.RaidManager;
import HoldMyAppleJuice.raid.RaidType;

public class CRaid implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player)) return false;
        Player player = (Player)sender;
        if (!player.isOp())
        {
            player.sendMessage(ChatColor.RED + "У вас недостаточно прав, что бы сделать это!");
            return false;
        }
        player.sendMessage("anchors: " + CustomRaids.config.load("Raid.anchors"));
        if (args.length!=0 && args[0].toLowerCase().equals("force"))
        {
            if (RaidManager.startRaid(Bukkit.getServer().getPlayer(sender.getName()).getLocation(), RaidType.EASY))
            {
                player.sendMessage(ChatColor.GOLD + "Вы начали рейд на координатах " + ChatColor.AQUA + player.getLocation().getBlockX() + player.getLocation().getBlockX());
                return true;
            }
            else
            {
                player.sendMessage(ChatColor.RED + "Не удалось начать рейд. По близости нет жителей!");
                return false;
            }
        }

        if (args.length!=0 && args[0].toLowerCase().equals("loc"))
        {
            if (args.length!=1 && args[1].toLowerCase().equals("add"))
            {
                String name = args[2];
                Location loc = player.getLocation();
                CustomRaids.config.save("Raid.anchors." + name + ".world", loc.getWorld().getName());
                CustomRaids.config.save("Raid.anchors." + name + ".x", loc.getBlockX());
                CustomRaids.config.save("Raid.anchors." + name + ".y", loc.getBlockY());
                CustomRaids.config.save("Raid.anchors." + name + ".z", loc.getBlockZ());
            }
            if (args.length!=1 && args[1].toLowerCase().equals("rem"))
            {
                String name = args[2];
                CustomRaids.config.save("Raid.anchors." + name, null );
                player.sendMessage("Позиция " + name + " удалена");
            }
        }
        player.sendMessage(ChatColor.YELLOW + "/craid force - начать рейд");
        return false;

    }
}
