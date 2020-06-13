package HoldMyAppleJuice.utils;

import org.bukkit.ChatColor;

public enum ChatPrefix
{
    TRADER_ERROR(ChatColor.RED + "" + ChatColor.BOLD + "[CustomTraders]:Error" + ChatColor.RESET + "" + ChatColor.RED),
    TRADER_INFO(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[CustomTraders]:Info" + ChatColor.RESET);

    String text;
    ChatPrefix(String text)
    {
        this.text = text;
    }

}
