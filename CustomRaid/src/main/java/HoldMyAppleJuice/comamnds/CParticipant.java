package HoldMyAppleJuice.comamnds;

import HoldMyAppleJuice.raid.villagers.traits.RaidParticipant;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CParticipant implements CommandExecutor
{
    NPC selectedNPC;
    Player player;
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

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
        if (args[0].equals("make"))
        {
            selectedNPC.addTrait(RaidParticipant.class);
        }
        return true;
    }
}
