package HoldMyAppleJuice.comamnds;

import HoldMyAppleJuice.raid.raiders.traits.Raider;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CRaider implements CommandExecutor
{
    NPC selectedNPC;
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if (!(sender instanceof Player)) return false;
        Player player = (Player)sender;
        if (!player.isOp())
        {
            player.sendMessage(ChatColor.RED + "У вас недостаточно прав, что бы сделать это!");
            return false;
        }
        selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
        if (args[0].equals("make"))
        {
            selectedNPC.addTrait(Raider.class);
        }
        return true;
    }
}
