package stranik.dev.compassCoords;

import com.comphenix.protocol.PacketType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static stranik.dev.compassCoords.CompassCoords.*;

public class Runner {
    private static final ArrayList<Player> _players = new ArrayList<>();
    
    public Runner() {
    }

    public static void run() {
        var online = Bukkit.getOnlinePlayers();
        _players.removeIf(p -> !online.contains(p));
        
        for (var p : online) {
            if (
                    p.getInventory().getItemInMainHand().getType().equals(Material.COMPASS) ||
                    p.getInventory().getItemInOffHand().getType().equals(Material.COMPASS)
            ) {
                if (!_players.contains(p)) {
                    setReducedDebugInfo(p, false);

                    _players.add(p);
                }

                var text = CompassCoords.getInstance().getConfig().getString("text");

                DecimalFormat df = new DecimalFormat("0.00");

                text = text
                        .replace("%dx", df.format(p.getX()))
                        .replace("%dy", df.format(p.getY()))
                        .replace("%dz", df.format(p.getZ()))
                        .replace("%x", String.valueOf((int)p.getX()))
                        .replace("%y", String.valueOf((int)p.getY()))
                        .replace("%z", String.valueOf((int)p.getZ()))
                        .replace("%dir", getCardinalDirection(p))
                        .replace("%world", p.getWorld().getName())
                        .replace("%dyaw", df.format(p.getLocation().getYaw()))
                        .replace("%dpitch", df.format(p.getLocation().getPitch()))
                        .replace("%yaw", p.getLocation().getYaw())
                        .replace("%pitch", p.getLocation().getPitch())
                
                ;

                p.sendActionBar(Component.text(text));;
            }
            else if (_players.contains(p)) {
                setReducedDebugInfo(p, true);
                
                // clear action bar
                p.sendActionBar(Component.text(""));

                _players.remove(p);
            }
        }
    }

    private static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        
        if (rotation < 0) {
            rotation += 360.0;
        }
        
        if (0 <= rotation && rotation < 22.5) {
            return getTextDir("west");
        } else if (22.5 <= rotation && rotation < 67.5) {
            return getTextDir("northwest");
        } else if (67.5 <= rotation && rotation < 112.5) {
            return getTextDir("north");
        } else if (112.5 <= rotation && rotation < 157.5) {
            return getTextDir("northeast");
        } else if (157.5 <= rotation && rotation < 202.5) {
            return getTextDir("east");
        } else if (202.5 <= rotation && rotation < 247.5) {
            return getTextDir("southeast");
        } else if (247.5 <= rotation && rotation < 292.5) {
            return getTextDir("south");
        } else if (292.5 <= rotation && rotation < 337.5) {
            return getTextDir("southwest");
        } else if (337.5 <= rotation && rotation < 360.0) {
            return getTextDir("west");
        }

        return "";
    }
    
    private static void setReducedDebugInfo(Player player, boolean value) {
        var packet = getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_STATUS);


        packet.getIntegers().write(0, player.getEntityId());
        packet.getBytes().write(0, value ? (byte) 22 : (byte) 23);

        getProtocolManager().sendServerPacket(player, packet);
    }
    
    private static String getTextDir(String field) {
        return CompassCoords.getInstance().getConfig().getString("directions." + field);
    }
}
