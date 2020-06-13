package HoldMyAppleJuice.comamnds;

import HoldMyAppleJuice.CustomRaids;
import HoldMyAppleJuice.raid.villagers.traits.Trader;
import HoldMyAppleJuice.utils.Chat;
import HoldMyAppleJuice.utils.ChatPrefix;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        //Bukkit.getServer().broadcastMessage("raw "+ Arrays.toString(arguments_raw) + " processed " + Arrays.toString(args));


        if (sender instanceof Player)
        {
            player = (Player) sender;
        }
        else
        {
            return false;
        }

        if (!player.isOp())
        {
            player.sendMessage(ChatColor.RED + "У вас недостаточно прав, что бы сделать это!");
            return false;
        }

        selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
        if (selectedNPC!=null && selectedNPC.hasTrait(Trader.class))
            traderTrait = selectedNPC.getTrait(Trader.class);
        else
        {
            traderTrait = null;
            player.sendMessage(ChatColor.RED + "Сначала выберите NPC с trait trader" + ChatColor.AQUA + " /npc sel");
            return false;
        }

        if (args[0].equals("bal"))
        {
            player.sendMessage("balance: " + CustomRaids.econ.getBalance(player));
        }

        if (args[0].equals("help"))
        {
            if (args.length == 1 || args[1].equals("1"))
            {
                player.sendMessage("/trader help 2 следующая страница");
                player.sendMessage("/trader [set] [disp] [mat] [slot] [material here]");
                player.sendMessage("/trader [set] [disp] [name] [slot] [name here]");
                player.sendMessage("/trader [set] [disp] [lore] [slot] [lore] (lore) (lore)...");

                player.sendMessage("/trader [set] [price] [slot] [price here]");
                player.sendMessage("/trader [set] [karma-dependent] [true/false]");

                player.sendMessage("/trader [set] [item] [name] [slot] [material here]");
                player.sendMessage("/trader [set] [item] [lore] [slot] [name here]");
                player.sendMessage("/trader [set] [item] [mat] [slot] [lore] (lore) (lore)...");

                player.sendMessage("/trader [get] [disp] [mat] [slot]");
                player.sendMessage("/trader [get] [disp] [name] [slot]");
                player.sendMessage("/trader [get] [disp] [lore] [slot]");

                player.sendMessage("/trader [get] [item] [mat] [slot]");
                player.sendMessage("/trader [get] [item] [name] [slot]");
                player.sendMessage("/trader [get] [item] [lore] [slot]");

                player.sendMessage("/trader [get] [price] [slot]");
                player.sendMessage("/trader [get] [layout]");
                player.sendMessage("/trader [get] [name]");
                player.sendMessage("/trader [get] [invname]");
                player.sendMessage("/trader [parse] [text] [slot]");
            }

            if (args[1].equals("2"))
            {
                player.sendMessage("/trader help 3 следующая страница");
                player.sendMessage("if синтаксис:" +
                        "\n$var1 operator var2?var_true:var_false$\n" +
                        "var1 - переменная, число, которое будет сравниваться с var2.\n" +
                        "var2 - переменная, число, которое будет сравниваться с var1.\n" +
                        "var_true - переменная, строка, будет возвращена вместо $выражения$, если оно истинно.\n" +
                        "var_false - переменная, строка, будет возвращена вместо $выражения$, если оно ложно.\n" +
                        "operator - оператор, строка, возможные операторы: >, <, >=, <=, ==." +
                        "Пример:\n" +
                        "\"$%rawprice>%price?%green%price%reset %red%strikethrough%rawprice%reset:%red%price%reset %green%strikethrough%rawprice%reset$\"");
            }
            if (args[1].equals("3"))
            {
                player.sendMessage("Переменные, поддерживающие парсинг:\n" +
                        "%rawprice - Цена слота без учета репутации\n" +
                        "%price - Цена слота с учетом репутации\n" +
                        "%fimat - formatted item material - строка с названием предмета исходя из материалла в слоте, заменяет _ на \" \". Пока что на английском.\n" +
                        "%fdmat - formatted display material - строка с названием отображаемого предмета исходя из материалла в слоте, заменяет _ на \" \". Пока что на английском.\n" +
                        "%imat - название_материалла_в_слоте\n" +
                        "%dmat - название_отображаемого_материалла_в_слоте\n" +
                        "%dname - название отображаемого предмета в слоте\n" +
                        "%iname - название предмета в слоте\n" +
                        "%name - имя торговца, которое используется для сообщений в чате\n" +
                        "%slot - слот\n" +
                        "%inv - название инвентаря\n" +
                        "%group - группа\n" +
                        "%npcname - имя NPC\n"
                );
            }
        }

        if (args.length>0 && args[0].equals("parse"))
        {
            player.sendMessage(traderTrait.parseString(args[1], Integer.valueOf(args[2]), player));
        }

        if (args.length>0 && args[0].equals("group"))
        {
            if (args.length>1 && args[1].equals("add"))
            {
                if (args[2]!=null)
                {
                    Chat.message(ChatPrefix.TRADER_INFO, player, "Торговец добавлен к группе", args[2]);
                    traderTrait.group = args[2];
                    traderTrait.clone_group_inventory();
                }
                else
                {
                    Chat.message(ChatPrefix.TRADER_ERROR, player, "Укажите название группы");
                }

            }
            if (args.length>1 && args[1].equals("remove"))
            {
                Chat.message(ChatPrefix.TRADER_INFO, player, "Торговец удален из группы", traderTrait.group);
                traderTrait.group = String.valueOf(Math.random());
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
            player.sendMessage("Теперь вы можете редактировать инвентарь торговца");
        }


        if (args.length>0 && args[0].equals("mode"))
        {
            if (args.length>1 && args[1].equals("sell"))
            {
                traderTrait.mode = "sell";
                traderTrait.update_group_inventory();
                Chat.message(ChatPrefix.TRADER_INFO, player, "Установлен режим продажи");
            }
            else if (args[1].equals("buy"))
            {
                traderTrait.mode = "buy";
                traderTrait.update_group_inventory();
                Chat.message(ChatPrefix.TRADER_INFO, player, "Установлен режим скупки");
            }
        }


        if (args.length>0 && args[0].equals("get"))
        {
            if (args.length>1 && args[1].equals("disp"))
            {
                if (args.length>2 && args[2].equals("mat"))
                {
                    player.sendMessage("Материалл (отображаемый) в слоте " + args[3] + " - " + traderTrait.get_disp_material(Integer.valueOf(args[3])));
                }
                if (args.length>2 && args[2].equals("lore"))
                {
                    player.sendMessage("Описание (отображаемое) слота " + args[3] + " - " + traderTrait.get_disp_lore(Integer.valueOf(args[3])).toString());
                }
                if (args.length>2 && args[2].equals("name"))
                {
                    player.sendMessage("название (отображаемое)" + args[3] + " - " + traderTrait.get_disp_name(Integer.valueOf(args[3])));
                }
            }

            if (args.length>1 && args[1].equals("item"))
            {
                if (args.length>2 && args[2].equals("mat"))
                {
                    player.sendMessage("Материалл (предмета) в слоте " + args[3] + " - " + traderTrait.get_item_material(Integer.valueOf(args[3])));
                }
                if (args.length>2 && args[2].equals("lore"))
                {
                    player.sendMessage("Описание (предмета) в слоте " + args[3] + " - " + traderTrait.get_item_lore(Integer.valueOf(args[3])).toString());
                }
                if (args.length>2 && args[2].equals("name"))
                {
                    player.sendMessage("Название (предмета) в слоте " + args[3] + " - " + traderTrait.get_item_name(Integer.valueOf(args[3])));
                }
            }

            if (args.length>1 && args[1].equals("price"))
            {
                if (args.length>2)
                {
                    Integer price = selectedNPC.getTrait(HoldMyAppleJuice.raid.villagers.traits.Trader.class).prices.get(Integer.valueOf(args[2]));
                    player.sendMessage(ChatColor.GOLD + "base price is " + price);
                }
            }
            if (args.length>1 && args[1].equals("name"))
            {
                player.sendMessage("Имя торговца - " + traderTrait.trader_name);
            }

            if (args.length>1 && args[1].equals("invname"))
            {
                player.sendMessage("Название инвентаря - " + traderTrait.inv_name);
            }

            if (args.length>1 && args[1].equals("layout"))
            {
                Chat.message(ChatPrefix.TRADER_INFO, player, "Layout:");
                //Chat.message(player, ChatColor.BLUE + "" + ChatColor.BOLD + "display:", "material", "lore", "name", "item:", ChatColor.DARK_GREEN + "" + ChatColor.BOLD +"material", "price", "lore", "name");

                if (args.length>2 )
                {
                    print_layout(Integer.parseInt(args[2]));
                }
                else
                {
                    print_layout(null);
                }

            }
            if (args.length>1 && args[1].equals("group"))
            {
                player.sendMessage("Торговец находится в группе " + traderTrait.group);
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

    public void print_layout(Integer row)
    {

        Integer start_slot = 0;
        Integer end_slot = traderTrait.gui_size;
        if (row!=null)
        {
            start_slot = row*9;
            end_slot = (row+1)*9;
            player.sendMessage(ChatColor.GOLD + "_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-" + " line "+ row + " -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_");
        }
        System.out.println( start_slot + " " + end_slot);
        for (Integer slot=start_slot; slot<end_slot; slot++)
        {

            player.spigot().sendMessage
                    (
                            Chat.text(ChatColor.YELLOW+String.valueOf(slot)+"."),
                            Chat.format_clickable("/trader updset disp mat ~ ", ChatColor.YELLOW+"set display material", 15, slot, ""+ChatColor.YELLOW+traderTrait.get_disp_material(slot)),
                            Chat.format_clickable("/trader updset item mat ~ ", ChatColor.AQUA+"set item material", 15, slot, ChatColor.AQUA+traderTrait.get_item_material(slot)),
                            Chat.format_clickable("/trader updset price ~ ", ChatColor.AQUA+"set price", 6, slot, ""+ChatColor.AQUA+traderTrait.get_raw_price(slot)),
                            Chat.format_clickable("/trader updset disp lore ~ ", ChatColor.DARK_PURPLE+"set display lore", 10, slot, ""+ChatColor.DARK_PURPLE+traderTrait.get_disp_lore(slot)),
                            Chat.format_clickable("/trader updset item lore ~ ", ChatColor.LIGHT_PURPLE+"set item lore", 10, slot, ""+ChatColor.LIGHT_PURPLE+traderTrait.get_item_lore(slot)),
                            Chat.format_clickable("/trader updset disp name ~ ", ChatColor.DARK_RED+"set display name", 10, slot, ChatColor.DARK_RED+traderTrait.get_disp_name(slot)),
                            Chat.format_clickable("/trader updset item name ~ ", ChatColor.DARK_BLUE+"set item name", 10, slot, ChatColor.DARK_BLUE+traderTrait.get_item_name(slot))
                    );
        }
    }

    public void handle_set(Integer start_index,  String[]args, boolean update_layout)
    {
        String arg1=null;
        String arg2=null;
        String arg3=null;
        String arg4=null;
        ArrayList<String> values = new ArrayList<String>();
        if (args.length-start_index >= start_index)
            arg1 = args[start_index];
        if (args.length-start_index >= start_index+1)
            arg2 = args[start_index+1];
        if (args.length-start_index >= start_index+2)
            arg3 = args[start_index+2];
        if (args.length-start_index >= start_index+3)
            arg4 = args[start_index+3];

        if (args.length-start_index >= start_index+3 && start_index+3<=args.length)
            values = new ArrayList<String>(Arrays.asList(args).subList(start_index + 3, args.length));

        if (arg1!=null)
        {
            if (arg1.equals("disp"))
            {
                if (arg2 != null)
                {

                    if (arg3==null)
                    {
                        Chat.message(ChatPrefix.TRADER_ERROR, player, "Укажите значение");
                        return;
                    }

                    if (arg2.equals("mat"))
                    {
                        traderTrait.set_disp_material(Integer.valueOf(arg3), arg4);
                        Chat.message(ChatPrefix.TRADER_INFO, player, "Отображаемый материалл установлен", arg4, "для", arg3);
                    }
                    if (arg2.equals("name"))
                    {
                        if (arg3.equals("all"))
                        {
                            for (int slot = 0; slot < traderTrait.gui_size; slot ++)
                            {
                                traderTrait.set_disp_name(slot, arg4);
                            }
                        }
                        else
                        {
                            traderTrait.set_disp_name(Integer.valueOf(arg3), arg4);
                        }
                        Chat.message(ChatPrefix.TRADER_INFO, player, "Отображаемое название установлено", arg4, "для", arg3);
                    }

                    if (arg2.equals("lore"))
                    {
                        if (arg3.equals("all"))
                        {
                            for (int slot = 0; slot < traderTrait.gui_size; slot ++)
                            {
                                traderTrait.set_disp_lore(slot, values);
                            }
                        }
                        else
                        {
                            traderTrait.set_disp_lore(Integer.valueOf(arg3), values);
                        }
                        Chat.message(ChatPrefix.TRADER_INFO, player, "Отображаемое описание установлено", values.toString(), "для", arg3);

                    }
                }
                else
                {
                    Chat.message(ChatPrefix.TRADER_ERROR, player, "/set disp mat/lore/name");
                }

            }
            if (arg1.equals("karma-dependent"))
            {
                if (arg2 != null)
                {
                    traderTrait.karma_dependent = (arg2.toLowerCase().equals("true"));
                    player.sendMessage("Зависимость от репутации " + traderTrait.karma_dependent);
                }
                else
                {
                    player.sendMessage("Неверный аргумент");
                }
            }
            if (arg1.equals("item"))
            {
                if (arg2 != null)
                {
                    if (arg3==null)
                    {
                        Chat.message(ChatPrefix.TRADER_ERROR, player, "Укажите значение");
                        return;
                    }
                    if (arg2.equals("mat")) {
                        traderTrait.set_item_material(Integer.valueOf(arg3), arg4);
                        Chat.message(ChatPrefix.TRADER_INFO, player, "Материалл предмета установлен", arg4, "для", arg3);
                    }
                    if (arg2.equals("name")){
                        traderTrait.set_item_name(Integer.valueOf(arg3), arg4);
                        Chat.message(ChatPrefix.TRADER_INFO, player, "Название предмета установлено", arg4, "для", arg3);
                    }
                    if (arg2.equals("lore"))
                    {


                        if (arg3.equals("all"))
                        {
                            for (int slot = 0; slot < traderTrait.gui_size; slot ++)
                            {
                                traderTrait.set_item_lore(slot, values);
                            }
                        }
                        traderTrait.set_item_lore(Integer.valueOf(arg3), values);
                        Chat.message(ChatPrefix.TRADER_INFO,player, "Описание предмета установлено", values.toString(), "для", arg3);
                    }
                }
                else
                {
                    Chat.message(ChatPrefix.TRADER_ERROR, player, "parameter can't be null.");
                }
// 4b91f894-012e-3d46-b8ba-4c1f31e60a9d
            }
            if (arg1.equals("price"))
            {
                if (arg2 != null)
                {
                    try {
                        traderTrait.set_price(Integer.valueOf(arg2), Integer.valueOf(arg3) );
                    }catch (Exception e)
                    {
                        Bukkit.getServer().broadcastMessage("cant cast "  + arg2 + " " + arg3);
                    }
                }
                else
                {
                    Chat.message(ChatPrefix.TRADER_ERROR, player, "price can't be null.");
                }

            }
            if (update_layout)
            {

                print_layout((int)Math.floor(Integer.parseInt(arg2) / 9d));
            }

            if (args.length>1 && arg1.equals("name"))
            {
                if (arg2==null)return;
                traderTrait.trader_name = arg2;
                player.sendMessage("Имя торговца изменено на " + traderTrait.trader_name);
            }

            if (args.length>1 && arg1.equals("invname"))
            {
                if (arg2==null)return;
                traderTrait.inv_name = arg2;
                player.sendMessage("Название инвентаря изменено на " + traderTrait.inv_name);
            }
            if (args.length>1 && arg1.equals("size"))
            {
                if (arg2==null)return;
                int size = Integer.parseInt(arg2) - Integer.parseInt(arg2) % 9;
                if (size > 0 && size < 55)
                {
                    traderTrait.gui_size = size;
                    player.sendMessage("Размер инвентаря изменен на " + traderTrait.gui_size);
                    return;
                }
                player.sendMessage("Неверное значение " + size);
            }

        }
        else
        {
            Chat.message(ChatPrefix.TRADER_ERROR, player, "/set item mat/lore/name");
        }

    }

    public void handle_get(Player sender, Integer start_index, String[] args)
    {
        String arg0;
        String arg1;
        String arg2;

        if (args.length-start_index > 0) {
             arg0 = args[0];
        }
        else
        {
            arg0 = null;
        }
        if (args.length-start_index > 1) {
             arg1 = args[1];
        }
        else
        {
            arg1 = null;
        }
        if (args.length-start_index > 2) {
             arg2 = args[3];
        }
        else
        {
            arg2 = null;
        }

        if (arg0.equals("price"))
        {
            sender.sendMessage("price is");
        }
        if (arg0.equals("disp"))
        {

        }
        if (arg0.equals("item"))
        {

        }
        if (arg0.equals("layout"))
        {

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
