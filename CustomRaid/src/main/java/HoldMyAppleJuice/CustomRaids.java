package HoldMyAppleJuice;

import HoldMyAppleJuice.IO.ConfigManager;
import HoldMyAppleJuice.comamnds.*;
import HoldMyAppleJuice.raid.raiders.traits.*;
import HoldMyAppleJuice.raid.villagers.traits.Trader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import HoldMyAppleJuice.raid.villagers.traits.RaidParticipant;

import java.io.File;

public class CustomRaids extends JavaPlugin
{
    public static CustomRaids plugin;
    public static ConfigManager config;

    public CustomRaids()
    {
        plugin = this;
    }

    @Override
    public void onEnable()
    {
        File file = new File(getDataFolder(), "PlayerKarma.yml");
        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
        if (!file.exists())
        {
            saveResource("PlayerKarma.yml", false);
        }

        config = new ConfigManager(file, conf);

        getCommand("highlight").setExecutor(new Highlight());
        getCommand("craid").setExecutor(new ForceRaid());
        getCommand("raider").setExecutor(new CRaider());
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(RaidParticipant.class).withName("trader_raid_participant"));
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(Trader.class).withName("trader"));
        //net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(Raider.class).withName("raider"));

        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(ArcherTrait.class).withName("archer"));
        System.out.println("------");
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(SupportTrait.class).withName("support"));
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(DamagerTrait.class).withName("damager"));
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(TankTrait.class).withName("tank"));
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(MarksmanTrait.class).withName("marksman"));

        getCommand("raidp").setExecutor(new SpawnRaidParticipant());
        getCommand("participant").setExecutor(new CParticipant());

        getCommand("trader").setExecutor(new CTrader());
        getCommand("trader").setTabCompleter(new CTraiderAutocomplete());
    }

}
