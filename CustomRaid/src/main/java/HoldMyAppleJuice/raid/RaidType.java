package HoldMyAppleJuice.raid;

import HoldMyAppleJuice.raid.raiders.traits.Raider;
import HoldMyAppleJuice.raid.raiders.types.RaiderType;
import HoldMyAppleJuice.raid.raids.Raid;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Set;

public enum RaidType
{

    EASY(1,
    20,
    20,
    40,
    5,
    100,
    130
    );
    //HARD(2, 30);

    HashMap<RaiderType, Integer> raiders;
    Integer radius;

    // Karma modifiers
    Integer villager_death_penalty;
    Integer raider_death_reward;
    Integer villager_damage_penalty;
    Integer win_reward;
    Integer loss_penalty;

    RaidType(Integer type, Integer spawn_radius,
             Integer villager_death_penalty,
             Integer raider_death_reward,
             Integer villager_damage_penalty,
             Integer win_reward,
             Integer loss_penalty
             )
    {
        this.radius = spawn_radius;
        this.raiders = new HashMap<RaiderType, Integer>();

        this.villager_damage_penalty = villager_damage_penalty;
        this.villager_death_penalty = villager_death_penalty;
        this.raider_death_reward = raider_death_reward;
        this.win_reward = win_reward;
        this.loss_penalty = loss_penalty;

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

    public Integer villager_damage_penalty()
    {
        return villager_damage_penalty;
    }
    public Integer get_villager_death_penalty()
    {
        return villager_death_penalty;
    }
    public Integer get_raider_death_reward()
    {
        return raider_death_reward;
    }
    public Integer get_win_reward()
    {
        return win_reward;
    }
    public Integer get_loss_penalty()
    {
        return loss_penalty;
    }


}
