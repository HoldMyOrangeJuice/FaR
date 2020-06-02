package HoldMyAppleJuice.IO;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager
{
    File file;
    FileConfiguration conf;
    public ConfigManager(File file, FileConfiguration conf)
    {
        this.file = file;
        this.conf = conf;
    }

    public void save(String path, Object data)
    {
        try {
            conf.set(path, data);
            conf.save(file);
        }
        catch (Exception e)
        {

        }

    }

    public Integer load(String path)
    {
        if (conf.get(path) instanceof Integer)
            return (Integer) conf.get(path);
        return 0;
    }

}
