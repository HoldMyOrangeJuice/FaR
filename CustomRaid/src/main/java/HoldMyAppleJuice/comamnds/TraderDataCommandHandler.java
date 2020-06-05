package HoldMyAppleJuice.comamnds;

import HoldMyAppleJuice.raid.villagers.traits.Trader;
import HoldMyAppleJuice.utils.Chat;
import com.google.gson.internal.$Gson$Types;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TraderDataCommandHandler
{
    public Trader traderTrait;
    public Player player;
    public String[] args;

    public TraderDataCommandHandler(Trader traderTrait, Player sender, String[] args)
    {
        this.traderTrait = traderTrait;
        this.player = sender;
        this.args = args;
    }

//    public void handle(Integer start_index)
//    {
//        if arlgs.lenght - start_index < command.getNeededArgs return;
//        command.handle(args);
//    }

    public void get_layout(Object row)
    {
        if (row instanceof Integer)
        {
            Integer start_slot;
            Integer end_slot;

            start_slot = (Integer)row*9;
            end_slot = ((Integer)row+1)*9;
            player.sendMessage(ChatColor.GOLD + "_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-" + " line "+ row + " -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_");

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
        else
        {
            for (Integer slot=0; slot<54; slot++)
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
    }

    public void get_disp_mat(Object slot)
    {
        if (slot instanceof Integer)
        {
            player.sendMessage(" " + traderTrait.get_disp_material((Integer) slot));
        }
        else
        {
            player.sendMessage(" ");
        }
    }
    public void get_item_mat(Object slot)
    {
        if (slot instanceof Integer)
        {
            player.sendMessage(" " + traderTrait.get_item_material((Integer) slot));
        }
        else
        {
            player.sendMessage(" ");
        }
    }
    public void get_disp_lore(Object slot)
    {
        if (slot instanceof Integer)
        {
            player.sendMessage(" " + traderTrait.get_disp_lore((Integer) slot));
        }
        else
        {
            player.sendMessage(" ");
        }
    }
    public void get_item_lore(Object slot)
    {
        if (slot instanceof Integer)
        {
            player.sendMessage(" " + traderTrait.get_item_lore((Integer) slot));
        }
        else
        {
            player.sendMessage(" ");
        }
    }
    public void get_disp_name(Object slot)
    {
        if (slot instanceof Integer)
        {
            player.sendMessage(" " + traderTrait.get_disp_name((Integer) slot));
        }
        else
        {
            player.sendMessage(" ");
        }
    }
    public void get_item_name(Object slot)
    {
        if (slot instanceof Integer)
        {
            player.sendMessage(" " + traderTrait.get_item_name((Integer) slot));
        }
        else
        {
            player.sendMessage(" ");
        }
    }
}
