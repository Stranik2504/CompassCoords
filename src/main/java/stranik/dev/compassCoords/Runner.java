package stranik.dev.compassCoords;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class Runner {
    public Runner() {
    }

    public static void run() {
        for (var p : Bukkit.getOnlinePlayers()) {
            if (
                    p.getInventory().getItemInMainHand().equals(ItemStack.of(Material.COMPASS)) ||
                    p.getInventory().getItemInOffHand().equals(ItemStack.of(Material.COMPASS))
            ) {
                var text = CompassCoords.getInstance().getConfig().getString("text");

                DecimalFormat df = new DecimalFormat("#.00");

                text = text
                        .replace("%x", df.format(p.getX()))
                        .replace("%y", df.format(p.getY()))
                        .replace("%z", df.format(p.getZ()))
                        .replace("%dir", getCardinalDirection(p))
                
                ;

                p.sendActionBar(Component.text(text));;
            }
        }
    }

    private static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        
        if (rotation < 0) {
            rotation += 360.0;
        }
        
        if (0 <= rotation && rotation < 22.5) {
            return "N";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NE";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "E";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "SE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "S";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SW";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "W";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "NW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "N";
        }

        return "";
    }
}
