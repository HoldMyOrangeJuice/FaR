package HoldMyAppleJuice.comamnds;

import HoldMyAppleJuice.raid.raiders.traits.Raider;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CRaider implements CommandExecutor
{
    NPC selectedNPC;
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(commandSender);
        if (args[0].equals("make"))
        {
            selectedNPC.addTrait(Raider.class);
        }
        return true;
    }
}
