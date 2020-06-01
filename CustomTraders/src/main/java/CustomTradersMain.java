import org.bukkit.plugin.java.JavaPlugin;

public class CustomTradersMain extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(Trader.class).withName("trader"));
        this.getCommand("trader").setExecutor(new CTrader() );
        this.getCommand("trader").setTabCompleter(new CTraiderAutocomplete());
    }
}
