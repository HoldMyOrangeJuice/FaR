package HoldMyAppleJuice.comamnds;

import HoldMyAppleJuice.raid.villagers.traits.RaidParticipant;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

public class SpawnRaidParticipant implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        NPC villager = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "test");
        villager.spawn(sender.getServer().getPlayer(sender.getName()).getLocation());
        villager.addTrait(RaidParticipant.class);
        return true;
    }
}