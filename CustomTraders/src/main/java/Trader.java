
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trader extends Trait
{
    public Trader()
    {
        super("trader");
        plugin = JavaPlugin.getPlugin(CustomTradersMain.class);
    }
    CustomTradersMain plugin = null;


    //             slot     value
    public HashMap<Integer, Integer> prices = new HashMap<Integer, Integer>();

    public HashMap<Integer, String> disp_materials = new HashMap<Integer, String>();
    public HashMap<Integer, String> item_materials = new HashMap<Integer, String>();

    public HashMap<Integer, String> disp_names = new HashMap<Integer, String>();
    public HashMap<Integer, String> item_names = new HashMap<Integer, String>();

    public HashMap<Integer, List<String>> disp_lore = new HashMap<Integer, List<String>>();
    public HashMap<Integer, List<String>> item_lore = new HashMap<Integer, List<String>>();

    public Integer max_gui_size = 54;
    public Player edited_by = null;
    public Inventory trader_GUI;
    public String mode = "sell";
    public String inv_name = "this is inventory";
    public String trader_name = "Bob Ross";
    public String group = null;

    // see the 'Persistence API' section
    @Persist("trader") boolean automaticallyPersistedSetting = false;

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
        // #-------------------------------------------# //

        for (int i=0; i<max_gui_size; i++)
        {
            prices.put(i, key.getInt(price_field(i), 0));
            disp_materials.put(i, key.getString(Dmat_field(i), "air"));
            disp_names.put(i, key.getString(Dname_field(i)));
            item_names.put(i, key.getString(Iname_field(i)));
            item_materials.put(i, key.getString(Imat_field(i)));

            Integer lore_line = 1;
            ArrayList<String>loaded_disp_lore=new ArrayList<String>();
            ArrayList<String>loaded_item_lore=new ArrayList<String>();
            System.out.println(key.getString(Dlore_field(1, 1)));
            System.out.println(Dlore_field(i, lore_line));

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

        for (int i=0; i<max_gui_size; i++)
        {
            // SAVE PRICES
            if (prices.containsKey(i))
            {
                System.out.println("saving price " + price_field(i) + " " + " with " + prices.get(i) );
                key.setInt(price_field(i), prices.get(i));
            }
            else
            {
                System.out.println("no value for " + price_field(i));
            }

            // SAVE DISPLAY MATERIALS
            if (disp_materials.containsKey(i))
            {
                key.setString(Dmat_field(i), disp_materials.get(i));
                System.out.println("saving mat " + Dmat_field(i) + " " + " with " + disp_materials.get(i) );
            }
            else
            {
                System.out.println("no value for " + Dmat_field(i));
            }
            // SAVE ITEM MATERIALS
            if (item_materials.containsKey(i))
            {
                key.setString(Imat_field(i), item_materials.get(i));
                System.out.println("saving mat " + Imat_field(i) + " " + " with " + item_materials.get(i) );
            }
            else
            {
                System.out.println("no value for " + Dmat_field(i));
            }

            // SAVE DISPLAY NAMES
            if (disp_names.containsKey(i))
            {
                key.setString(Dname_field(i), disp_names.get(i));
                System.out.println("saving dname " + "display_name_"+i + " " + " with " + disp_names.get(i) );
            }
            else
            {
                System.out.println("no value for " + "display_name_" + i);
            }

            // SAVE ITEM NAMES
            if (item_names.containsKey(i))
            {
                key.setString(Iname_field(i), item_names.get(i));
                System.out.println("saving iname " + Iname_field(i) + " with " + item_names.get(i) );
            }
            else
            {
                System.out.println("no value for " + Iname_field(i));
            }

            // SAVE DISPLAY LORE
            if (disp_lore.containsKey(i))
            {
                for (int line=0; line<disp_lore.get(i).size(); i++)
                {
                    key.setString(Dlore_field(i, line+1), disp_lore.get(i).get(line));
                }
            }
            else
            {
                System.out.println("no value for " + Dlore_field(i, -1));
            }

            // SAVE ITEM LORE
            if (item_lore.containsKey(i))
            {
                for (int line=1; line<item_lore.get(i).size(); i++)
                {
                    key.setString(Ilore_field(i, line), item_lore.get(i).get(line));
                }
            }
            else
            {
                System.out.println("no value for " + Ilore_field(i, -1));
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
    public Integer get_price(Integer slot)
    {
        if (validate_args(slot))
        {
            return this.prices.get(slot);
        }
        return null;
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
        return this.disp_materials.get(slot);
    }

    public void set_disp_lore(Integer slot, List<String>lore)
    {
        this.disp_lore.put(slot, lore);
        update_group_inventory();
    }
    public List<String> get_disp_lore(Integer slot)
    {
        return this.disp_lore.get(slot);
    }
    public void set_item_lore(Integer slot, List<String>lore)
    {
        this.item_lore.put(slot, lore);
        update_group_inventory();
    }
    public List<String> get_item_lore(Integer slot)
    {
        return this.item_lore.get(slot);
    }

    public void set_item_material(Integer slot, String material)
    {
        this.item_materials.put(slot, material);
        update_group_inventory();
    }
    public String get_item_material(Integer slot)
    {
        return this.item_materials.get(slot);
    }


    public void clone_group_inventory()
    {
        for (NPC npc : CitizensAPI.getNPCRegistry())
        {
            if (npc!= this.npc && npc.hasTrait(Trader.class) && npc.getTrait(Trader.class).group!=null && npc.getTrait(Trader.class).group.equals(this.group))
            {
                this.item_names = npc.getTrait(Trader.class).item_names;
                this.disp_names = npc.getTrait(Trader.class).disp_names;
                this.item_lore = npc.getTrait(Trader.class).item_lore;
                this.disp_lore = npc.getTrait(Trader.class).disp_lore;
                this.prices = npc.getTrait(Trader.class).prices;
                this.item_materials = npc.getTrait(Trader.class).item_materials;
                this.disp_materials = npc.getTrait(Trader.class).disp_materials;
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
            if (npc.hasTrait(Trader.class) && npc.getTrait(Trader.class).group!=null && npc.getTrait(Trader.class).group.equals(this.group))
            {
                npc.getTrait(Trader.class).item_names = this.item_names;
                npc.getTrait(Trader.class).disp_names = this.disp_names;
                npc.getTrait(Trader.class).item_lore = this.item_lore;
                npc.getTrait(Trader.class).disp_lore = this.disp_lore;
                npc.getTrait(Trader.class).prices = this.prices;
                npc.getTrait(Trader.class).item_materials = this.item_materials;
                npc.getTrait(Trader.class).disp_materials = this.disp_materials;
                count ++;
            }
        }
    }

    public String parseString(String text, Integer slot)
    {
        String colored_text = text;
        for (ChatColor color : ChatColor.values())
        {
            String color_code = "%"+color.name().toLowerCase();
            if (text.contains(color_code))
            {
                colored_text = colored_text.replaceAll(color_code, color.toString());
            }
        }
        return colored_text.
                replaceAll("%price",     String.valueOf(get_price(slot))).
                replaceAll("%imat",      get_item_material(slot)).
                replaceAll("%dmat",      get_disp_material(slot)).
                replaceAll("%dname",     get_disp_name(slot)).
                replaceAll("%iname",     get_item_name(slot)).
                replaceAll("%name",      trader_name).
                replaceAll("%slot",      String.valueOf(slot)).
                replaceAll("%inv",       inv_name).
                replaceAll("%group",     group).
                replaceAll("%npcname",   npc.getName());
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


    @EventHandler
    public void click(NPCRightClickEvent event)
    {
        Player player = event.getClicker();

        if (event.getNPC() == this.getNPC())
        {
            // DISPLAY INVENTORY WITH "DISPLAY" ITEMS
            trader_GUI = Bukkit.createInventory(null, 54, parseString(inv_name, 0));

            for (Integer slot = 0; slot<max_gui_size; slot++)
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
                            meta.setLore(parseLore((ArrayList<String>) get_disp_lore(slot), slot));
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
            if (event.getClickedInventory() == trader_GUI)
            {
                Integer slot = event.getSlot();
                ItemStack clicked_item = event.getCurrentItem();

                if (clicked_item==null || clicked_item.getType() == Material.AIR)
                {
                    event.setCancelled(true);
                    return;
                }


                if (this.mode.equals("sell"))
                {
                    ItemStack item_to_give;
                    if (get_item_material(slot)!=null && !get_item_material(slot).equals("air"))
                    {
                        item_to_give = new ItemStack(Material.valueOf(get_item_material(slot).toUpperCase()), 1);
                    }
                    else
                    {
                        item_to_give = new ItemStack(clicked_item.getType(), 1);
                    }

                    ItemMeta meta = item_to_give.getItemMeta();
                    if (get_item_name(slot)!=null && !get_item_name(slot).equals(""))
                    {
                        meta.setDisplayName(parseString(get_item_name(slot), slot));
                    }

                    if (get_item_lore(slot)!=null)
                    {
                        meta.setLore(parseLore((ArrayList<String>) get_item_lore(slot), slot));
                    }

                    item_to_give.setItemMeta(meta);

                    if (event.getClick().isShiftClick())
                    {
                        item_to_give.setAmount(64);
                        player.sendMessage("you bought 64 of " + parseString(item_to_give.getItemMeta().getDisplayName(), slot));
                    }
                    else
                    {
                        item_to_give.setAmount(1);
                        player.sendMessage("you bought " + parseString(item_to_give.getItemMeta().getDisplayName(), slot));
                    }
                    player.getInventory().addItem(item_to_give);
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
                        player.sendMessage("you don't have any " + clicked_item.getItemMeta().getDisplayName());
                    }

                    if (event.getClick().isShiftClick() || amount <= 64)
                    {
                        take_item(player.getInventory(), clicked_item.getType(), amount);
                        player.sendMessage("sold " + amount + " of " + parseString(clicked_item.getItemMeta().getDisplayName(), slot) + " for " + get_price(slot)*amount + "$");
                    }
                    else
                    {
                        take_item(player.getInventory(), clicked_item.getType(), 64);
                        player.sendMessage("sold " + 64 + " of " + parseString(clicked_item.getItemMeta().getDisplayName(), slot) + " for " + get_price(slot)*64 + "$");
                    }
                }
                event.setCancelled(true);
            }
        }
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
            this.edited_by = null;
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

}
