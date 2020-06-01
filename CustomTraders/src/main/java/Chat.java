
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Chat
{
    public static void message(Player player, String ... args)
    {
        String message = "";
        for (String part: args)
        {
            if (part!=null)
            message = message.concat(part).concat(" ");
        }

        player.sendMessage(message);
    }
    public static void message(ChatPrefix prefix, Player player, String ... args)
    {
        String message = "";
        for (String part: args)
        {
            if (part!=null)
            message = message.concat(part).concat(" ");
        }

        player.sendMessage(prefix.text + message);
    }

    public static void message(Iterable<Player>players, String ... args)
    {
        String message = text(args).getText();
        for (Player player : players)
        {
            player.sendMessage(message);
        }
    }

    public static TextComponent format_clickable(String autocomplete, String label, Integer width, Integer slot, String ... args)
    {
        String msg = "";
        for (String arg: args)
        {
            if (arg!=null)
            msg = msg.concat(arg).concat(" ");
        }
        msg = msg.replaceAll("~", String.valueOf(slot));
        if (width - msg.length()<0)
        {
            msg = msg.substring(0, width-1);
            msg = msg.concat("...");
        }
        else
        {
            for (int left = width - msg.length(); left>0; left--)
            {
                msg = msg.concat(" ");
            }
        }

        msg = msg + " | ";

        TextComponent message = new TextComponent( msg );
        message.setClickEvent( new ClickEvent( ClickEvent.Action.SUGGEST_COMMAND, autocomplete.replaceAll("~", String.valueOf(slot)) ) );
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( label ).create()));
        return message;

    }

    public static TextComponent text(String ... args)
    {
        String msg = "";
        for (String arg: args)
        {
            if (arg!=null)
            msg = msg.concat(arg).concat(" ");

        }
        TextComponent message = new TextComponent( msg );
        return message;
    }

}
