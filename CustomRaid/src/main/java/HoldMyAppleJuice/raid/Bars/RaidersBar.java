package HoldMyAppleJuice.raid.Bars;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

public class RaidersBar implements BossBar
{
    String title;
    public String getTitle() {
        return "test";
    }

    public void setTitle(String s) {
        this.title = s;
    }

    public BarColor getColor() {
        return null;
    }

    public void setColor(BarColor barColor) {

    }

    public BarStyle getStyle() {
        return null;
    }

    public void setStyle(BarStyle barStyle) {

    }

    public void removeFlag(BarFlag barFlag) {

    }

    public void addFlag(BarFlag barFlag) {

    }

    public boolean hasFlag(BarFlag barFlag) {
        return false;
    }

    public void setProgress(double v) {

    }

    public double getProgress() {
        return 0;
    }

    public void addPlayer(Player player) {

    }

    public void removePlayer(Player player) {

    }

    public void removeAll() {

    }

    public List<Player> getPlayers() {
        return null;
    }

    public void setVisible(boolean b) {

    }

    public boolean isVisible() {
        return false;
    }

    public void show() {

    }

    public void hide() {

    }
}
