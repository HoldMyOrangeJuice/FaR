package HoldMyAppleJuice.comamnds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CTraiderAutocomplete implements TabCompleter {

    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args)
    {
        ArrayList<String> list = new ArrayList<String>();

        if (!(sender instanceof Player)) return null;
        Player player = (Player)sender;
        if (!player.isOp())
        {
            return null;
        }
        if (args.length == 1)
        {
            return suggest(args[0], "set", "get", "mode", "group", "make", "edit");
        }
        if (args.length == 2)
        {
            if (args[0].equals("set"))
            {
                return suggest(args[1], "disp", "item", "price", "name", "invname", "group", "karma-dependent");
            }
            if (args[0].equals("get"))
            {
                return suggest(args[1], "disp", "item", "price", "layout", "name", "invname", "group");
            }
            if (args[0].equals("mode"))
            {
                return suggest(args[1], "buy", "sell");
            }
            if (args[0].equals("group"))
            {
                return suggest(args[1], "add", "remove");
            }

        }
        if (args.length == 3)
        {
            if (args[1].equals("disp"))
            {
                return suggest(args[2], "mat", "lore", "name");
            }
            if (args[1].equals("item"))
            {
                return suggest(args[2], "mat", "lore", "name");
            }
        }

        if (args.length == 4)
        {
            if (args[1].equals("item") || args[1].equals("disp"))
            {
                for (int i = 0; i<54; i++)
                {
                    list.add(String.valueOf(i));
                }
            }
        }

        if (args.length == 5)
        {
            if (args[1].equals("item") || args[1].equals("disp"))
            {
                for (Material mat : Material.values())
                {
                    if (mat.toString().toLowerCase().contains(args[4]))
                    {
                        list.add(mat.toString().toLowerCase());
                    }
                }
            }
        }
        return list;
    }

    public static ArrayList<String> suggest(String arg, String ... options)
    {
        ArrayList<String> list = new ArrayList<String>();
        for (String option : options)
        {
            if (option.contains(arg))
            {
                list.add(option);
            }
        }
        return list;
    }
}

