package HoldMyAppleJuice.raid.villagers.traits;

import HoldMyAppleJuice.CustomRaids;
import HoldMyAppleJuice.raid.managers.PlayerKarmaManager;
import HoldMyAppleJuice.utils.Chat;
import HoldMyAppleJuice.utils.ChatPrefix;
import com.sun.deploy.util.OrderedHashSet;
import jdk.nashorn.internal.runtime.ECMAException;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Trader extends Trait
{
    public Trader()
    {
        super("trader");
        plugin = JavaPlugin.getPlugin(CustomRaids.class);
    }
    CustomRaids plugin = null;


    //             slot     value
    public HashMap<Integer, Integer> prices = new HashMap<Integer, Integer>();

    public HashMap<Integer, String> disp_materials = new HashMap<Integer, String>();
    public HashMap<Integer, String> item_materials = new HashMap<Integer, String>();

    public HashMap<Integer, String> disp_names = new HashMap<Integer, String>();
    public HashMap<Integer, String> item_names = new HashMap<Integer, String>();

    public HashMap<Integer, List<String>> disp_lore = new HashMap<Integer, List<String>>();
    public HashMap<Integer, List<String>> item_lore = new HashMap<Integer, List<String>>();

    public Integer gui_size = 54;
    public Player edited_by = null;
    public Inventory trader_GUI;
    public String mode = "sell";
    public String inv_name = "this is inventory";
    public String trader_name = "Bob Ross";
    public String group = String.valueOf(Math.random());
    public boolean karma_dependent=false;



    // Here you should load up any values you have previously saved (optional).
    // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
    // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
    // This is called BEFORE onSpawn, npc.getBukkitEntity() will return null.
    public void load(DataKey key)
    {
        // #-------------------------------------------# //
        inv_name = key.getString("inv_name");
        trader_name = key.getString("trader_name");
        group = key.getString("group");
        mode = key.getString("mode");
        karma_dependent = key.getBoolean("karma_dependent");
        // #-------------------------------------------# //

        for (int i = 0; i< gui_size; i++)
        {
            if (key.getInt(price_field(i)) != 0)
            {
                prices.put(i, key.getInt(price_field(i)));
            }
            if (!key.getString(Dmat_field(i)).equals(""))
            {
                disp_materials.put(i, key.getString(Dmat_field(i)));
            }
            if (!key.getString(Dname_field(i)).equals(""))
            {
                disp_names.put(i, key.getString(Dname_field(i)));
            }
            if (!key.getString(Iname_field(i)).equals(""))
            {
                item_names.put(i, key.getString(Iname_field(i)));
            }
            if (!key.getString(Imat_field(i)).equals(""))
            {
                item_materials.put(i, key.getString(Imat_field(i)));
            }

            Integer lore_line = 1;
            ArrayList<String>loaded_disp_lore=new ArrayList<String>();
            ArrayList<String>loaded_item_lore=new ArrayList<String>();


            while (!key.getString(Dlore_field(i, lore_line)).equals(""))
            {
                loaded_disp_lore.add(key.getString(Dlore_field(i, lore_line)));
                lore_line++;
            }
            disp_lore.put(i, loaded_disp_lore);
            lore_line = 1;
            while (!key.getString(Dlore_field(i, lore_line)).equals(""))
            {
                loaded_item_lore.add(key.getString(Ilore_field(i, lore_line)));
                lore_line++;
            }
            item_lore.put(i, loaded_item_lore);
        }
    }

    // Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
    public void save(DataKey key)
    {
        key.setString("inv_name", inv_name);
        key.setString("trader_name", trader_name);
        key.setString("group", group);
        key.setString("mode", mode);
        key.setBoolean("karma_dependent", karma_dependent);

        for (int i = 0; i< gui_size; i++)
        {
            // SAVE PRICES
            if (prices.containsKey(i))
            {

                key.setInt(price_field(i), prices.get(i));
            }
            // SAVE DISPLAY MATERIALS
            if (disp_materials.containsKey(i))
            {
                key.setString(Dmat_field(i), disp_materials.get(i));

            }
            // SAVE ITEM MATERIALS
            if (item_materials.containsKey(i))
            {
                key.setString(Imat_field(i), item_materials.get(i));

            }
            // SAVE DISPLAY NAMES
            if (disp_names.containsKey(i))
            {
                key.setString(Dname_field(i), disp_names.get(i));

            }
            // SAVE ITEM NAMES
            if (item_names.containsKey(i))
            {
                key.setString(Iname_field(i), item_names.get(i));

            }
            // SAVE DISPLAY LORE
            if (disp_lore.containsKey(i))
            {
                for (int line=0; line < disp_lore.get(i).size(); line++)
                {
                    key.setString(Dlore_field(i, line+1), disp_lore.get(i).get(line));
                }
            }
            // SAVE ITEM LORE
            if (item_lore.containsKey(i))
            {
                for (int line=1; line<item_lore.get(i).size(); i++)
                {
                    key.setString(Ilore_field(i, line), item_lore.get(i).get(line));
                }
            }
        }
    }
    public boolean validate_args(Object ... args)
    {
        for (Object arg : args)
        {
            if (arg == null)
            {
                return false;
            }
        }
        return true;
    }
    public String set_price(Integer slot, Integer price)
    {
        if (validate_args(slot, price))
        {
            this.prices.put(slot, price);
            update_group_inventory();
            return ChatPrefix.TRADER_INFO + "price of slot " + slot + " set to " + price;
        }
            return ChatPrefix.TRADER_ERROR + " invalid arguments";

    }
    public Integer get_raw_price(Integer slot)
    {
        if (validate_args(slot, this.prices.get(slot)) )
        {
            return this.prices.get(slot);
        }
        return 0;
    }

    public Integer get_price(Integer slot, Player player)
    {
        // price player pays
        if (karma_dependent)
        {
            Integer discount = getDiscount(player);
            Integer price = 0;
            if (validate_args(slot, this.prices.get(slot)))
            {
                if (mode.equals("buy"))
                {
                    price = this.prices.get(slot) + discount;
                }
                else if (mode.equals("sell"))
                {
                    price = this.prices.get(slot) - discount;
                }

                if (price!= null && price <= 0)
                {
                    return 1;
                }
                else
                {
                    return price;
                }
            }
        }
        if (validate_args(slot, this.prices.get(slot)))
        {
            return this.prices.get(slot);
        }
        return 0;
    }


    private Integer getDiscount(Player player)
    {
        Integer Karma = PlayerKarmaManager.getPlayerKarma(player);
        return  ( Karma / 10 );
    }

    // # ------------------ # //
    public String set_disp_name(Integer slot, String name)
    {
        if (validate_args(slot, name))
        {
            this.disp_names.put(slot, name);
            update_group_inventory();
            return ChatPrefix.TRADER_INFO + "set name of slot " + slot + " to " + name;
        }
        return ChatPrefix.TRADER_ERROR + " invalid arguments";

    }

    @Nullable
    public String get_disp_name(Integer slot)
    {
        return this.disp_names.get(slot);
    }

    public String set_item_name(Integer slot, String name)
    {
        if (validate_args(slot, name)) {
            this.item_names.put(slot, name);
            update_group_inventory();
            return ChatPrefix.TRADER_INFO + "set name of slot " + slot + " to " + name;
        }
        return ChatPrefix.TRADER_ERROR + " invalid arguments";

    }
    @Nullable
    public String get_item_name(Integer slot)
    {
        if (item_names.containsKey(slot))
        {
            return this.item_names.get(slot);
        }
        return null;
    }

    public String set_disp_material(Integer slot, String material)
    {
        if (validate_args(slot, material)) {
            this.disp_materials.put(slot, material);
            update_group_inventory();
            return ChatPrefix.TRADER_INFO + "set material of slot " + slot + " to " + material;
        }
        return ChatPrefix.TRADER_ERROR + " invalid arguments";

    }
    public String get_disp_material(Integer slot)
    {
        if (this.disp_materials.containsKey(slot))
        {
            return this.disp_materials.get(slot);
        }
        return Material.AIR.toString().toLowerCase();


    }

    public void set_disp_lore(Integer slot, List<String>lore)
    {
        this.disp_lore.put(slot, lore);
        update_group_inventory();
    }
    public List<String> get_disp_lore(Integer slot)
    {
        if (this.disp_lore.containsKey(slot))
        {
            return this.disp_lore.get(slot);
        }
        return new ArrayList<String>();

    }
    public void set_item_lore(Integer slot, List<String>lore)
    {
        this.item_lore.put(slot, lore);
        update_group_inventory();
    }
    public List<String> get_item_lore(Integer slot)
    {
        if (this.item_lore.containsKey(slot))
            return this.item_lore.get(slot);
        return new ArrayList<String>();
    }

    public void set_item_material(Integer slot, String material)
    {
        this.item_materials.put(slot, material);
        update_group_inventory();
    }
    public String get_item_material(Integer slot)
    {
        if (this.item_materials.containsKey(slot))
            return this.item_materials.get(slot);
        return get_disp_material(slot);
    }


    public void clone_group_inventory()
    {

        for (NPC npc : CitizensAPI.getNPCRegistry())
        {
            if (npc!= this.npc && npc.hasTrait(Trader.class) && npc.getTrait(Trader.class).group!=null && npc.getTrait(Trader.class).group.equals(this.group))
            {
                Bukkit.getServer().broadcastMessage("clone group inv called");
                Bukkit.getServer().broadcastMessage("found another group member " + npc);
                this.item_names = (HashMap<Integer, String>) npc.getTrait(Trader.class).item_names.clone();
                this.disp_names = (HashMap<Integer, String>) npc.getTrait(Trader.class).disp_names.clone();
                this.item_lore = (HashMap<Integer, List<String>>) npc.getTrait(Trader.class).item_lore.clone();
                this.disp_lore = (HashMap<Integer, List<String>>) npc.getTrait(Trader.class).disp_lore.clone();
                this.prices = (HashMap<Integer, Integer>) npc.getTrait(Trader.class).prices.clone();
                this.item_materials = (HashMap<Integer, String>) npc.getTrait(Trader.class).item_materials.clone();
                this.disp_materials = (HashMap<Integer, String>) npc.getTrait(Trader.class).disp_materials.clone();
                // test
                this.mode = npc.getTrait(Trader.class).mode;
                this.karma_dependent = npc.getTrait(Trader.class).karma_dependent;
                break;
            }
        }
    }
    public void update_group_inventory()
    {
        Integer count = 0;
        if (this.group == null)
        {
            return;
        }
        for (NPC npc : CitizensAPI.getNPCRegistry())
        {
            if (npc != this.npc && npc.hasTrait(Trader.class) && npc.getTrait(Trader.class).group!=null && npc.getTrait(Trader.class).group.equals(this.group))
            {
                Bukkit.getServer().broadcastMessage("update_group_inventory called");
                Bukkit.getServer().broadcastMessage("npc " + npc.getName() +  "has trait trader" + npc.getTrait(Trader.class).group.equals(this.group) + " " + npc.getTrait(Trader.class).group + " = " + this.group);
                npc.getTrait(Trader.class).item_names = (HashMap<Integer, String>) this.item_names.clone();
                npc.getTrait(Trader.class).disp_names = (HashMap<Integer, String>) this.disp_names.clone();
                npc.getTrait(Trader.class).item_lore = (HashMap<Integer, List<String>>) this.item_lore.clone();
                npc.getTrait(Trader.class).disp_lore = (HashMap<Integer, List<String>>) this.disp_lore.clone();
                npc.getTrait(Trader.class).prices = (HashMap<Integer, Integer>) this.prices.clone();
                npc.getTrait(Trader.class).item_materials = (HashMap<Integer, String>) this.item_materials.clone();
                npc.getTrait(Trader.class).disp_materials = (HashMap<Integer, String>) this.disp_materials.clone();

                npc.getTrait(Trader.class).mode = this.mode;
                npc.getTrait(Trader.class).karma_dependent = this.karma_dependent;
                count ++;
            }
        }
    }

    public String parseString(String text, Integer slot)
    {
        String colored_text = text;

        int c = 0;
        while(colored_text.contains("%") && c < 10)
        {
            for (ChatColor color : ChatColor.values())
            {
                String color_code = "%"+color.name().toLowerCase();
                colored_text = colored_text.replaceAll(color_code, color.toString());
            }

            colored_text = colored_text.
                    replaceAll("%price",     String.valueOf(get_raw_price(slot))).
                    replaceAll("%fdmat",     get_formatted_d_mat(slot)).
                    replaceAll("%fimat",     get_formatted_i_mat(slot)).
                    replaceAll("%imat",      get_item_material(slot)).
                    replaceAll("%dmat",      get_disp_material(slot)).
                    replaceAll("%dname",     ""+get_disp_name(slot)).
                    replaceAll("%iname",     "" + get_item_name(slot)).
                    replaceAll("%name",      trader_name).
                    replaceAll("%slot",      String.valueOf(slot)).
                    replaceAll("%inv",       inv_name).
                    replaceAll("%group",     group).
                    replaceAll("%npcname",   npc.getName());
            c ++;
        }
        colored_text = parse_logic(colored_text);
        return colored_text;

    }

    public String parseString(String text, Integer slot, Player player)
    {
        String colored_text = text;

        int c = 0;
        while(colored_text.contains("%") && c < 10)
        {
            for (ChatColor color : ChatColor.values())
            {
                String color_code = "%"+color.name().toLowerCase();
                colored_text = colored_text.replaceAll(color_code, color.toString());
            }

            colored_text = colored_text.
                    replaceAll("%rawprice", String.valueOf(get_raw_price(slot))).
                    replaceAll("%fimat", get_formatted_i_mat(slot)).
                    replaceAll("%fdmat", get_formatted_d_mat(slot)).
                    replaceAll("%price", String.valueOf(get_price(slot, player))).
                    replaceAll("%imat", get_item_material(slot)).
                    replaceAll("%dmat", get_disp_material(slot)).
                    replaceAll("%dname", "" + get_disp_name(slot)).
                    replaceAll("%iname", ""+get_item_name(slot)).
                    replaceAll("%name", trader_name).
                    replaceAll("%slot", String.valueOf(slot)).
                    replaceAll("%inv", inv_name).
                    replaceAll("%group", group).
                    replaceAll("%npcname", npc.getName()).
                    replaceAll("%karma", String.valueOf(PlayerKarmaManager.getPlayerKarma(player)));
            c++;
        }
        colored_text = parse_logic(colored_text);
        return colored_text;
    }

    private String get_formatted_d_mat(Integer slot)
    {
        String mat = get_disp_material(slot);
        mat = mat.replace("_", " ");
        return mat;
    }

    private String get_formatted_i_mat(Integer slot)
    {
        String mat = get_item_material(slot);
        mat = mat.replace("_", " ");
        return mat;
    }

    public ArrayList<String> parseLore(ArrayList<String>lore, Integer slot)
    {
        ArrayList<String> parsed = new ArrayList<String>();
        if (lore!=null)
        {
            for (String line : lore)
            {
                parsed.add(parseString(line, slot));
            }
        }
        return parsed;
    }

    public ArrayList<String> parseLore(ArrayList<String>lore, Integer slot, Player player)
    {
        ArrayList<String> parsed = new ArrayList<String>();
        if (lore!=null)
        {
            for (String line : lore)
            {
                parsed.add(parseString(line, slot, player));
            }
        }
        return parsed;
    }


    @EventHandler
    public void click(NPCRightClickEvent event)
    {
        Player player = event.getClicker();

        if (event.getNPC() == this.getNPC())
        {
            // DISPLAY INVENTORY WITH "DISPLAY" ITEMS
            trader_GUI = Bukkit.createInventory(null, gui_size, parseString(inv_name, 0));

            for (Integer slot = 0; slot< gui_size; slot++)
            {
                if (disp_materials.containsKey(slot))
                {
                    ItemStack item = new ItemStack(Material.valueOf(get_disp_material(slot).toUpperCase()), 1);
                    if (item.getType()!=Material.AIR)
                    {
                        ItemMeta meta = item.getItemMeta();
                        // slot is NOT empty
                        if (disp_lore.containsKey(slot))
                        {
                            meta.setLore(parseLore((ArrayList<String>) get_disp_lore(slot), slot, player));
                        }
                        if (disp_names.containsKey(slot))
                        {
                            meta.setDisplayName(parseString(disp_names.get(slot), slot));
                        }
                        item.setItemMeta(meta);
                    }
                    // # ----------------- # //
                    trader_GUI.setItem(slot, item);
                }
                else
                {
                    ItemStack item_air = new ItemStack(Material.AIR, 1);
                    trader_GUI.setItem(slot, item_air);
                }
            }
            event.getClicker().openInventory(trader_GUI);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player))return;

        Player player = (Player)event.getWhoClicked();

        // player edits inventory
        if (this.edited_by != null && player == this.edited_by && event.getClickedInventory() == trader_GUI)
        {
            ItemStack item = event.getCursor();
            System.out.println(item.toString());
            Material mat = item.getType();
            System.out.println(mat.toString());
            List<String> lore = item.getLore();
            System.out.println(lore);
            Integer slot = event.getSlot();

            set_disp_material(slot, mat.name());

            if (validate_args(slot, lore))
                set_disp_lore(slot, lore);
        }
        else
        {
            String trader_prefix = ChatColor.WHITE + "[L] [" + parseString(trader_name, 0, player) + ChatColor.WHITE + "] " + ChatColor.DARK_GRAY + ">> " + ChatColor.RESET;



            if (event.getClickedInventory() == trader_GUI)
            {
                Integer slot = event.getSlot();
                ItemStack clicked_item = event.getCurrentItem();



                if (clicked_item==null || clicked_item.getType() == Material.AIR)
                {
                    event.setCancelled(true);
                    return;
                }

                String name = clicked_item.getItemMeta().getDisplayName();
                if (name==null || name.equals(""))
                {
                    name = get_formatted_i_mat(slot);
                }

                if (this.mode.equals("sell"))
                {
                    ItemStack item_to_give = create_item_for_sale(clicked_item, slot, player);

                    if (event.getClick().isShiftClick())
                    {
                        item_to_give.setAmount(64);

                        if (CustomRaids.econ.has(player, get_price(slot, player) * 64))
                        {
                            CustomRaids.econ.depositPlayer(player, -get_price(slot, player) * 64);
                            PlayerKarmaManager.updatePlayerKarma(player, get_raw_price(slot)*64);
                            player.sendMessage(trader_prefix + "Вы приобрели 64 " + name + " за $" + get_price(slot, player)*64);
                            player.getInventory().addItem(item_to_give);
                        }
                        else
                        {
                            player.sendMessage(trader_prefix + "У вас недостаточно денег!");
                        }

                    }
                    else
                    {
                        item_to_give.setAmount(1);
                        if (CustomRaids.econ.has(player, get_price(slot, player)))
                        {
                            CustomRaids.econ.depositPlayer(player, -get_price(slot, player));
                            PlayerKarmaManager.updatePlayerKarma(player, get_raw_price(slot));
                            player.sendMessage(trader_prefix + "Вы приобрели " + name + " за $" + get_price(slot, player));
                            //player.sendMessage(trader_prefix + "потрачено $" + get_price(slot, player));
                            PlayerKarmaManager.updatePlayerKarma(player, get_raw_price(slot));
                            player.getInventory().addItem(item_to_give);
                        }
                        else
                        {
                            player.sendMessage(trader_prefix + "У вас недостаточно денег!");
                        }

                    }


                }
                else if (this.mode.equals("buy"))
                {
                    int amount = 0;
                    for (ItemStack item : player.getInventory().getContents())
                    {
                        if (item!=null && item.getType() == clicked_item.getType())
                        {
                            amount += item.getAmount();
                        }
                    }

                    if (amount == 0)
                    {
                        player.sendMessage(trader_prefix + "У вас нет " + name);
                        event.setCancelled(true);
                        return;
                    }

                    if (event.getClick().isShiftClick() || amount <= 64)
                    {
                        take_item(player.getInventory(), clicked_item.getType(), amount);

                        player.sendMessage(trader_prefix + "Продано " + amount + " " +  name  + ChatColor.RESET + " за " + get_price(slot, player)*amount + "$");
                        //player.sendMessage(trader_prefix + "получено $" +  get_price(slot, player)*amount);
                        CustomRaids.econ.depositPlayer(player, get_price(slot, player)*amount);
                    }
                    else
                    {
                        take_item(player.getInventory(), clicked_item.getType(), 64);
                        player.sendMessage(trader_prefix + "Продано " + 64 + " " + name + ChatColor.RESET + " за " + get_price(slot, player)*64 + "$");
                        //player.sendMessage(trader_prefix + "получено $" +  get_price(slot, player)*64);
                        CustomRaids.econ.depositPlayer(player, get_price(slot, player)*64);
                    }
                }
                event.setCancelled(true);
            }
        }
    }
    public boolean mat_is_legit(String s)
    {
        for (Material m : Material.values())
        {
            if (m.toString().equals(s))
            {
                return true;
            }
        }
        return false;
    }
    public ItemStack create_item_for_sale(ItemStack clicked, Integer slot, Player player)
    {
        ItemStack item_to_give;

        if (get_item_material(slot)!=null && !get_item_material(slot).equals("air") && !get_item_material(slot).equals("") && mat_is_legit(get_item_material(slot).toUpperCase()))
        {
            item_to_give = new ItemStack(Material.valueOf(get_item_material(slot).toUpperCase()), 1);
        }
        else
        {
            item_to_give = new ItemStack(clicked.getType(), 1);
        }

        ItemMeta meta = item_to_give.getItemMeta();
        if (get_item_name(slot)!=null && !get_item_name(slot).equals(""))
        {
            meta.setDisplayName(parseString(get_item_name(slot), slot));
        }

        if (get_item_lore(slot)!=null && get_item_lore(slot).size() > 0)
        {
            List<String>lore = new ArrayList<String>();
            for (String line : get_item_lore(slot))
            {
                if (!line.equals(""))
                {
                    lore.add(line);
                }
            }
            if (lore.size() > 0)
            {
                meta.setLore(parseLore((ArrayList<String>) get_item_lore(slot), slot, player));
            }

        }
        item_to_give.setItemMeta(meta);
        return item_to_give;
    }

    public void take_item(Inventory inventory, Material type, Integer amount)
    {

        Integer left = amount;
        for (ItemStack stack : inventory.getContents())
        {
            if (stack == null)continue;

            if (left > 0)
            {
                if (stack.getType() == type && stack.getAmount() <= left)
                {
                    left -= stack.getAmount();
                    inventory.removeItem(stack);

                }
                else if (stack.getType() == type && stack.getAmount() > left)
                {
                    stack.setAmount(stack.getAmount()-left);
                    left = 0;

                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        Player player = (Player)event.getPlayer();
        if (edited_by == player)
        {
            this.edited_by = null;
            player.sendMessage("Вы больше не можете редактировать этот инвентарь");
        }
    }

    // Called every tick
    @Override
    public void run() {
            }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
            plugin.getServer().getLogger().info(npc.getName() + " has been assigned with my TRADER trait!");
            }

    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getBukkitEntity() is still valid.
    @Override
    public void onDespawn() {
            }

    //Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
    //This is called AFTER onAttach and AFTER Load when the server is started.
    @Override
    public void onSpawn() {

            }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }

    public String price_field(Integer slot)
    {
        return "price_"+slot;
    }
    public String Imat_field(Integer slot)
    {
        return "material_item_"+slot;
    }
    public String Dmat_field(Integer slot)
    {
        return "material_disp_"+slot;
    }
    public String Iname_field(Integer slot)
    {
        return "name_item_"+slot;
    }
    public String Dname_field(Integer slot)
    {
        return "name_disp_"+slot;
    }
    public String Ilore_field(Integer slot, Integer line)
    {
        return "lore"+line+"_item_"+slot;
    }
    public String Dlore_field(Integer slot, Integer line)
    {
        return "lore" + String.valueOf(line) + "_disp_" + String.valueOf(slot);
    }

    public String parse_logic(String text)
    {
        final ArrayList<String> operators = new ArrayList<String>(Arrays.asList("<=", ">=", "<", ">", "=="));
        ArrayList<Integer> double_quote_index = new ArrayList<Integer>();
        for (int i = 0; i<text.length(); i++)
        {
            char letter = text.toCharArray()[i];
            if (letter == '$')
            {
                double_quote_index.add(i);
            }
        }
        ArrayList<String> scripts = new ArrayList<String>();
        String script = "";
        for (int letter_index=0; letter_index<text.length(); letter_index++)
        {

            char letter = text.charAt(letter_index);
            for (int pair_index = 1; pair_index <= Math.floor(double_quote_index.size() / 2d); pair_index++) {
                Integer quote1_idx = double_quote_index.get(pair_index * 2 - 2);
                Integer quote2_idx = double_quote_index.get(pair_index * 2 - 1);

                if (quote1_idx < letter_index && letter_index < quote2_idx) {
                    script = script.concat(String.valueOf(letter));
                }
                else if (!script.equals(""))
                {
                    scripts.add(script);
                    script = "";
                }
            }

        }

        for (int i = 0; i < scripts.size(); i++)
        {
            String line = scripts.get(i);
            //"test > test2?text:text2"
            if (line.split("\\?").length == 2)
            {
                String condition = line.split("\\?")[0];
                String values = line.split("\\?")[1];
                if (values.split(":").length ==2)
                {
                    String val_true = values.split(":")[0];
                    String val_false = values.split(":")[1];

                    for (String operator : operators)
                    {
                        if (condition.contains(operator))
                        {
                            if (condition.split(operator).length == 2)
                            {
                                String cond_val_1 = condition.split(operator)[0];
                                String cond_val_2 = condition.split(operator)[1];

                                try
                                {
                                    Integer val1 = Integer.valueOf(cond_val_1);
                                    Integer val2 = Integer.valueOf(cond_val_2);

                                    if ( (val1.equals(val2) && operator.equals("==")) ||
                                            (val1 > val2 && operator.equals(">") ) ||
                                            (val1 >= val2 && operator.equals(">=") ) ||
                                            (val1 <= val2 && operator.equals("<=") ) ||
                                            (val1 < val2 && operator.equals("<") ) )
                                    {
                                        line = line.replaceAll("\\$", "");
                                        text = text.replace(line, val_true);

                                    }
                                    else
                                    {
                                        line = line.replaceAll("\\$", "");
                                        text = text.replace(line, val_false);
                                    }
                                }catch (Exception e)
                                {

                                }
                            }
                        }
                    }
                }
            }
        }
        return text.replaceAll("\\$", "");
    }

}
