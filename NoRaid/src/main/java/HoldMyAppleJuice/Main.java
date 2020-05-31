package HoldMyAppleJuice;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


public class Main extends JavaPlugin implements Listener
{
    public static StateFlag MY_CUSTOM_FLAG;
    public static JavaPlugin plugin;
    public static String DEBUG = "true";
    public static String WORLD_NAME = "world";


    @Override
    public void onEnable()
    {
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
            StateFlag flag = new StateFlag("raid", false);
            registry.register(flag);
            MY_CUSTOM_FLAG = flag;
        } catch (FlagConflictException e) {
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
            String propFileName = "config.properties";

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

        RegionManager regions = container.get(BukkitAdapter.adapt( e.getWorld() ));

        if (regions != null) {

            Map<String, ProtectedRegion> regionMap = regions.getRegions();

            if (regionMap.isEmpty()) return;

            for (ProtectedRegion region : regionMap.values()) {
                if (region == null) continue;
                for (Flag flag : region.getFlags().keySet()) {
                    if (flag == null) continue;
                    if (region.getFlag(flag) != null) {
                        try {
                            if (flag.getName().equals("raid") && region.getFlag(flag).toString().equals("DENY")) {
                                if (region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                                    e.setCancelled(true);
                                    return;
                                }
                            }
                        } catch (Exception ex) {
                            System.out.println("NORAID caused " + ex);
                        }
                    }
                }
            }
        }
    }
}
