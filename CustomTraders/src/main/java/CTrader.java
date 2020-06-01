
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class CTrader implements CommandExecutor
{
    Player player;
    Trader traderTrait;
    NPC selectedNPC;
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments_raw)
    {
        String[] args = handle_args(arguments_raw);
       // Bukkit.getServer().broadcastMessage("raw "+ Arrays.toString(arguments_raw) + " processed " + Arrays.toString(args));

        player = sender.getServer().getPlayer(sender.getName());
        selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
        if (selectedNPC.hasTrait(Trader.class))
            traderTrait = selectedNPC.getTrait(Trader.class);
        else
            traderTrait = null;

        if (args.length>0 && args[0].equals("parse"))
        {
            player.sendMessage(traderTrait.parseString(args[1], Integer.valueOf(args[2])));
        }

        if (args.length>0 && args[0].equals("group"))
        {
            if (args.length>1 && args[1].equals("add"))
            {
                Chat.message(ChatPrefix.TRADER_INFO, player, "added npc to group", args[2]);
                traderTrait.group = args[2];
                traderTrait.clone_group_inventory();
            }
            if (args.length>1 && args[1].equals("remove"))
            {
                Chat.message(ChatPrefix.TRADER_INFO, player, "removed npc from", traderTrait.group);
                traderTrait.group = null;
            }
        }

        if (args.length>0 && args[0].equals("make"))
        {
            selectedNPC.addTrait(Trader.class);
            traderTrait = selectedNPC.getTrait(Trader.class);
        }

        if (args.length>0 && args[0].equals("edit"))
        {
            traderTrait.edited_by = player;
            player.sendMessage("now you can edit npc's inventory");
        }


        if (args.length>0 && args[0].equals("mode"))
        {
            if (args.length>1 && args[1].equals("sell"))
            {
                traderTrait.mode = "sell";
                Chat.message(ChatPrefix.TRADER_INFO, player, "now mode is sell");
            }
            else if (args[1].equals("buy"))
            {
                traderTrait.mode = "buy";
            }
        }

        if (args.length>0 && args[0].equals("invname"))
        {
            traderTrait.inv_name = args[1];
        }

        if (args.length>0 && args[0].equals("name"))
        {
            traderTrait.trader_name = args[1];
        }

        if (args.length>0 && args[0].equals("get"))
        {
            if (args.length>1 && args[1].equals("disp"))
            {
                if (args.length>2 && args[2].equals("mat"))
                {
                    player.sendMessage("material of slot " + args[3] + " is " + traderTrait.get_disp_material(Integer.valueOf(args[3])));
                }
                if (args.length>2 && args[2].equals("lore"))
                {
                    player.sendMessage("lore of slot " + args[3] + " is " + traderTrait.get_disp_lore(Integer.valueOf(args[3])).toString());
                }
                if (args.length>2 && args[2].equals("name"))
                {
                    player.sendMessage("name of slot " + args[3] + " is " + traderTrait.get_disp_name(Integer.valueOf(args[3])));
                }
            }

            if (args.length>1 && args[1].equals("item"))
            {
                if (args.length>2 && args[2].equals("mat"))
                {
                    player.sendMessage("material of slot " + args[3] + " is " + traderTrait.get_item_material(Integer.valueOf(args[3])));
                }
                if (args.length>2 && args[2].equals("lore"))
                {
                    player.sendMessage("lore of slot " + args[3] + " is " + traderTrait.get_item_lore(Integer.valueOf(args[3])).toString());
                }
                if (args.length>2 && args[2].equals("name"))
                {
                    player.sendMessage("name of slot " + args[3] + " is " + traderTrait.get_item_name(Integer.valueOf(args[3])));
                }
            }

            if (args.length>1 && args[1].equals("price"))
            {
                if (args.length>2)
                {
                    Integer price = selectedNPC.getTrait(Trader.class).prices.get(Integer.valueOf(args[2]));
                    player.sendMessage(ChatColor.GOLD + "price is " + price);
                }
            }
            if (args.length>1 && args[1].equals("layout"))
            {
                Chat.message(ChatPrefix.TRADER_INFO, player, "Layout:");
                Chat.message(player, ChatColor.BLUE + "" + ChatColor.BOLD + "display:", "material", "lore", "name", "item:", ChatColor.DARK_GREEN + "" + ChatColor.BOLD +"material", "price", "lore", "name");

                if (args.length>2 )
                {
                    print_lore(Integer.parseInt(args[2]));
                }
                else
                {
                    print_lore(null);
                }

            }
        }
        if (args.length>0 && args[0].equals("set"))
        {
            handle_set(1, args, false);
        }
        if (args.length>0 && args[0].equals("updset"))
        {
            handle_set(1, args, true);

        }
        return true;
    }

    public void print_lore(Integer row)
    {

        Integer start_slot = 0;
        Integer end_slot = 53;
        if (row!=null)
        {
            start_slot = row*9;
            end_slot = (row+1)*9;
            player.sendMessage(ChatColor.GOLD + "_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-" + " line "+ row + " -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_");
        }

        for (Integer slot=start_slot; slot<end_slot; slot++)
        {
            player.spigot().sendMessage
                    (
                            Chat.text(ChatColor.YELLOW+String.valueOf(slot)+"."),
                            Chat.format_clickable("/trader updset disp mat ~ ", ChatColor.YELLOW+"set display material", 15, slot, ""+ChatColor.YELLOW+traderTrait.get_disp_material(slot)),
                            Chat.format_clickable("/trader updset item mat ~ ", ChatColor.AQUA+"set item material", 15, slot, ChatColor.AQUA+traderTrait.get_item_material(slot)),
                            Chat.format_clickable("/trader updset disp lore ~ ", ChatColor.DARK_PURPLE+"set display lore", 10, slot, ChatColor.DARK_PURPLE+traderTrait.get_disp_lore(slot).toString()),
                            Chat.format_clickable("/trader updset item lore ~ ", ChatColor.LIGHT_PURPLE+"set item lore", 10, slot, ChatColor.LIGHT_PURPLE+traderTrait.get_item_lore(slot).toString()),
                            Chat.format_clickable("/trader updset disp name ~ ", ChatColor.DARK_RED+"set display name", 10, slot, ChatColor.DARK_RED+traderTrait.get_disp_name(slot)),
                            Chat.format_clickable("/trader updset item name ~ ", ChatColor.DARK_BLUE+"set item name", 10, slot, ChatColor.DARK_BLUE+traderTrait.get_item_name(slot))
                    );
        }
    }

    public void handle_set(Integer start_index,  String[]args, boolean update_layout)
    {
        String arg1=null;
        String arg2=null;
        Integer arg3=null;
        String arg4=null;
        ArrayList<String> values = new ArrayList<String>();
        if (args.length >= start_index)
            arg1 = args[start_index];
        if (args.length >= start_index+1)
            arg2 = args[start_index+1];
        if (args.length >= start_index+2)
            arg3 = Integer.valueOf(args[start_index+2]);
        if (args.length >= start_index+3)
            arg4 = args[start_index+3];

        if (args.length >= start_index+3 && start_index+3<=args.length)
            values = new ArrayList<String>(Arrays.asList(args).subList(start_index + 3, args.length));

        if (arg1!=null)
        {
            if (arg1.equals("disp"))
            {
                if (arg2 != null)
                {
                    if (arg2.equals("mat"))
                        traderTrait.set_disp_material(arg3, arg4);
                    if (arg2.equals("name"))
                        traderTrait.set_disp_name(arg3, arg4);
                    if (arg2.equals("lore"))
                    {
                        traderTrait.set_disp_lore(arg3, values);
                    }
                }
                else
                {
                    Chat.message(ChatPrefix.TRADER_ERROR, player, "mode can't be null.");
                }

            }
            if (arg1.equals("item"))
            {
                if (arg2 != null)
                {
                    if (arg2.equals("mat"))
                        traderTrait.set_item_material(arg3, arg4);
                    if (arg2.equals("name"))
                        traderTrait.set_item_name(arg3, arg4);
                    if (arg2.equals("lore"))
                    {
                        traderTrait.set_item_lore(arg3, values);
                    }
                }
                else
                {
                    Chat.message(ChatPrefix.TRADER_ERROR, player, "parameter can't be null.");
                }

            }
            if (arg1.equals("price"))
            {
                if (arg2 != null)
                {
                    traderTrait.set_price(arg3, Integer.valueOf(arg2));
                }
                else
                {
                    Chat.message(ChatPrefix.TRADER_ERROR, player, "price can't be null.");
                }

            }
            if (update_layout)
            {
                print_lore((int)Math.floor(arg3 / 9d));
            }
        }
        else
        {
            Chat.message(ChatPrefix.TRADER_ERROR, player, "mode can't be null.");
        }

    }

    public static String[] handle_args(String[] args)
    {
        String line="";
        ArrayList<Integer> double_quote_index = new ArrayList<Integer>(); // "
        ArrayList<String>args_parsed = new ArrayList<String>();

        // args to line;
        for (int i = 0; i<args.length; i++)
        {
            if (i+1!=args.length)
                line = line.concat(args[i]).concat(" ");
            else
                line = line.concat(args[i]);
        }


        // format array of quotes
        for (int i = 0; i<line.length(); i++)
        {
            char letter = line.toCharArray()[i];
            if (letter == '\"')
            {
                double_quote_index.add(i);
            }
        }


        // iterate through string
        String argument = "";
        for (int letter_index=0; letter_index<line.length(); letter_index++)
        {
            char letter = line.charAt(letter_index);

            if (letter == " ".charAt(0))
            {

                boolean is_quoted = false;
                for (int pair_index=1; pair_index<=Math.floor(double_quote_index.size()/2d); pair_index++)
                {
                    Integer quote1_idx = double_quote_index.get(pair_index*2-2);
                    Integer quote2_idx = double_quote_index.get(pair_index*2-1);

                    if (quote1_idx<letter_index && letter_index<quote2_idx)
                    {

                        is_quoted = true;
                        break;
                    }
                }
                if (!is_quoted)
                {

                    args_parsed.add(argument);
                    argument = "";
                }
                else
                {

                    argument = argument.concat(String.valueOf(letter));
                }

            }
            else
            {
                argument = argument.concat(String.valueOf(letter));
                // last char
                if (letter_index+1==line.length())
                {
                    args_parsed.add(argument);
                }
            }

        }
        String[] arguments_parsed_primitive = new String[args_parsed.size()];

        int i = 0;
        for (String arg : args_parsed)
        {
            arguments_parsed_primitive[i]=arg.replaceAll("\"", "");
            i++;
        }
        return arguments_parsed_primitive;
    }
}
