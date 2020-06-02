package HoldMyAppleJuice.utils;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class Vectors
{
    public static Location look(Entity e1, Entity e2)
    {
        Vector dirBetweenLocations = e1.getLocation().toVector().subtract(e2.getLocation().toVector());
        Location loc = e2.getLocation();
        loc.setDirection(dirBetweenLocations);

        return loc;
    }
}
