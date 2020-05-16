package HoldMyAppleJuice;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.yaml.snakeyaml.Yaml;
import com.sk89q.worldedit.world.World;
import sun.util.resources.cldr.hy.CalendarData_hy_AM;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import static com.sk89q.jnbt.NBTUtils.toVector;

public class Main extends JavaPlugin implements Listener
{
    public static StateFlag MY_CUSTOM_FLAG;
    public static JavaPlugin plugin;
    public static String DEBUG = "true";
    public static String WORLD_NAME = "world";



    @Override
    public void onEnable()
    {
        System.out.println(ChatColor.GOLD + "NORAID ACTIVATED");
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onLoad()
    {
//        try{init_cfg();}
//        catch (IOException e)
//        {
//            for (OfflinePlayer administrator : Bukkit.getServer().getOperators())
//            {
//                if (administrator.isOnline())
//                {
//                    administrator.getPlayer().sendMessage(ChatColor.GOLD + "[NORAID] " + ChatColor.RED + "Failed to load config.properties, using default values");
//                }
//            }
//        }

        plugin = this;
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            // create a flag with the name "my-custom-flag", defaulting to true
            StateFlag flag = new StateFlag("raid", false);
            registry.register(flag);
            MY_CUSTOM_FLAG = flag; // only set our field if there was no error
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get("raid");
            if (existing instanceof StateFlag) {
                MY_CUSTOM_FLAG = (StateFlag) existing;
            } else {
                System.out.println("flag conflict");
            }
        }

    }

    public void init_cfg() throws IOException
    {
        InputStream inputStream=null;

        try {
            Properties prop = new Properties();
            String propFileName = "D:\\myFignya\\programs\\FaR server\\Server Enviroment\\plugins\\config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            DEBUG = prop.getProperty("debug");
            WORLD_NAME = prop.getProperty("world");
            System.out.println("initialized config: " + DEBUG + " " + WORLD_NAME);

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }

    }

    @EventHandler
    public void raid_spawned(RaidTriggerEvent e)
    {
        Location loc = e.getPlayer().getLocation();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt( Bukkit.getWorlds().get(0) ));
        Map<String, ProtectedRegion> regionMap = regions.getRegions();
        for (ProtectedRegion region : regionMap.values())
        {
            for(Flag flag : region.getFlags().keySet())
            {
                if (flag.getName().equals("raid") && region.getFlag(flag).toString().equals("DENY"))
                {
                    if(region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
                    {
                        e.setCancelled(true);
                        if (DEBUG.toLowerCase().equals("true"))
                        {
                            for (OfflinePlayer administrator : Bukkit.getServer().getOperators())
                            {
                                if (administrator.isOnline())
                                {
                                    Bukkit.getServer().broadcastMessage("region " + ChatColor.AQUA + region.getId() + ChatColor.RESET + " cancelled raid " + "triggered by " + ChatColor.AQUA + e.getPlayer().getDisplayName());
                                }
                            }

                        }
                    }

                }

            }
        }
    }

}
