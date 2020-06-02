package HoldMyAppleJuice.raid.villagers.traits;

import HoldMyAppleJuice.CustomRaids;
import HoldMyAppleJuice.raid.managers.PlayerKarmaManager;
import HoldMyAppleJuice.raid.managers.RaidManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;

public class RaidParticipant extends Trait
{
    public RaidParticipant() {
        super("trader_raid_participant");
        plugin = JavaPlugin.getPlugin(CustomRaids.class);
    }

    CustomRaids plugin = null;
    public Location anchor = null;
    public String participant_name = "default name";
    public boolean dead = false;

    public boolean raid_participant = true;

    // see the 'Persistence API' section
    //@Persist("trader_raid_participant") boolean automaticallyPersistedSetting = false;

    // Here you should load up any values you have previously saved (optional).
    // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
    // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
    // This is called BEFORE onSpawn, npc.getBukkitEntity() will return null.
    public void load(DataKey key) {
        //SomeSetting = key.getBoolean("SomeSetting", false);
        dead = key.getBoolean("dead");
        participant_name = key.getString("participant_name", "default name");
        anchor = new Location(Bukkit.getWorld(key.getString("anchor_w")), key.getInt("anchor_x"), key.getInt("anchor_y"), key.getInt("anchor_z"));


        if (dead)
        {
            plugin.getServer().getLogger().info(npc.getName() + " is dead. trying to respawn");
            respawn();
        }

    }

    // Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
    public void save(DataKey key) {
        key.setBoolean("raid_participant", raid_participant);
        key.setBoolean("dead", dead);
        key.setString("participant_name", participant_name);
        key.setInt("anchor_x", anchor.getBlockX());
        key.setInt("anchor_y", anchor.getBlockY());
        key.setInt("anchor_z", anchor.getBlockZ());
        key.setString("anchor_w", anchor.getWorld().getName());
    }

    // An example event handler. All traits will be registered automatically as Bukkit Listeners.
    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!

    }

    // Called every tick
    @Override
    public void run()
    {

    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        plugin.getServer().getLogger().info(npc.getId() + " is raid participant. is dead " + dead);
    }

    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getBukkitEntity() is still valid.
    @Override
    public void onDespawn() {
    }

    //Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
    //This is called AFTER onAttach and AFTER Load when the server is started.
    @Override
    public void onSpawn() {

        participant_name = npc.getName();

        npc.setProtected(false);

        if (npc.isSpawned() && anchor==null)
        {
            anchor = npc.getEntity().getLocation();
            Bukkit.getServer().broadcastMessage("on spawn anchor " + npc.getEntity().getLocation());
        }
    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }

    @EventHandler
    public void npcdamage(NPCDamageByEntityEvent event)
    {
        if (event.getNPC()!=npc) return;
        //boolean damager_is_npc = CitizensAPI.getNPCRegistry().isNPC(event.getDamager());
        //boolean damager_is_projectile = (event.getDamager() instanceof Projectile);

        //boolean proj_source_is_ent = (((Projectile)event.getDamager()).getShooter() instanceof Entity);
        //boolean proj_source_is_npc = CitizensAPI.getNPCRegistry().isNPC( (Entity) ((Projectile)(event.getDamager())).getShooter());

        if (!CitizensAPI.getNPCRegistry().isNPC(event.getDamager())
                && !((event.getDamager() instanceof Projectile)
                && (((Projectile)event.getDamager()).getShooter() instanceof Entity)
                && CitizensAPI.getNPCRegistry().isNPC( (Entity) ((Projectile)(event.getDamager())).getShooter())) )
        {
            Bukkit.getServer().broadcastMessage("not raider attacked npc");
            event.setCancelled(true);
            return;
        }
        if (CitizensAPI.getNPCRegistry().isNPC(event.getDamager()))
        {
            if (((LivingEntity)npc.getEntity()).getHealth()-event.getDamage()<=0)
            {
                npc.setName(ChatColor.BLACK + "dead");
                NPC killer = CitizensAPI.getNPCRegistry().getNPC(event.getDamager());
                //Collection<Player> players_nearby = PlayerKarmaManager.getPlayersNearby(killer.getEntity().getLocation(), 100);
            }
        }
        else if (event.getDamager() instanceof Arrow)
        {

        }

    }

    @EventHandler
    public void damage (EntityDamageEvent event)
    {
        if (event.getEntity() != npc.getEntity())return;
        LivingEntity entity = (LivingEntity)event.getEntity();
        set_name_health(entity.getHealth(), entity.getMaxHealth(), event.getDamage());
    }

    @EventHandler
    public void regen(EntityRegainHealthEvent event)
    {
        if (event.getEntity() != npc.getEntity())return;
        LivingEntity entity = (LivingEntity)event.getEntity();
        set_name_health(entity.getHealth(), entity.getMaxHealth(), -event.getAmount());
    }


    @EventHandler
    public void died(NPCDeathEvent event)
    {
        if (event.getNPC() == npc)
        {
            Bukkit.getServer().broadcastMessage("raid participant died");
            Bukkit.getServer().broadcastMessage("respawning at x " + anchor.getBlockX() + " y "+ anchor.getBlockY() + " z "+ anchor.getBlockZ() + " ");
            dead = true;
            respawn();
        }
    }

    public void set_name_health(Double health, Double max_health, Double damage)
    {
        String name = "";
        for (int i = 0; i<health-damage; i ++)
        {
            name = name.concat("❤");
        }
        if (health-damage<max_health/3d)
            npc.setName("" + ChatColor.DARK_RED + name);
        else if (health-damage<max_health/2d)
            npc.setName("" + ChatColor.YELLOW + name);
        else if (health-damage<=max_health)
            npc.setName("" + ChatColor.DARK_GREEN +name);
    }

    public void respawn()
    {
        if (RaidManager.getRaidersNearby(anchor, 300).isEmpty())
        {
            Bukkit.getServer().broadcastMessage("respawning...");
            npc.spawn(anchor);
            npc.setName(participant_name);
            dead = false;
        }
        else
        {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CustomRaids.plugin, new Runnable() {
                public void run() {
                    respawn();
                }
            }, (int)(2000*Math.random()));
        }
    }
}


