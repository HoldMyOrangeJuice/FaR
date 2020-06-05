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

    public Object load(String path)
    {
        return conf.get(path);
    }

}
