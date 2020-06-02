package HoldMyAppleJuice.raid;

import HoldMyAppleJuice.raid.raiders.traits.Raider;
import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import HoldMyAppleJuice.raid.raids.Raid;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Set;

public enum RaidType
{

    EASY(1, 20),
    HARD(2, 30);

    HashMap<RaiderType, Integer> raiders;
    Integer radius;

    RaidType(Integer type, Integer spawn_radius)
    {
        this.radius = spawn_radius;
        this.raiders = new HashMap<RaiderType, Integer>();
        switch (type)
        {
            case (1):
                raiders.put(RaiderType.Archer, 2);
                raiders.put(RaiderType.Tank, 3);
                raiders.put(RaiderType.Damager, 2);
                raiders.put(RaiderType.Support, 2);
                break;
            case (2):
                raiders.put(RaiderType.Archer, 5);
                raiders.put(RaiderType.Support, 5);
                raiders.put(RaiderType.Marksman, 5);
                break;
        }

    }
    public HashMap<RaiderType, Integer> getRaiderData()
    {
        return this.raiders;
    }
    public Set<RaiderType> getRaiderTypes()
    {
        return this.raiders.keySet();
    }
    public Integer getRaiderCount(RaiderType raider)
    {
        return this.raiders.get(raider);
    }
    public Integer get_spawn_radius()
    {
        return this.radius;
    }


}
