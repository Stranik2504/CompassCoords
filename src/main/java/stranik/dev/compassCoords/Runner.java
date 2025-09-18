package stranik.dev.compassCoords;

import com.comphenix.protocol.PacketType;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
                var loc = p.getLocation();

                text = text
                        .replace("%dx", df.format(loc.getX()))
                        .replace("%dy", df.format(loc.getY()))
                        .replace("%dz", df.format(loc.getZ()))
                        .replace("%x", String.valueOf((int)loc.getX()))
                        .replace("%y", String.valueOf((int)loc.getY()))
                        .replace("%z", String.valueOf((int)loc.getZ()))
                        .replace("%dir", getCardinalDirection(p))
                        .replace("%world", p.getWorld().getName())
                        .replace("%dyaw", df.format(loc.getYaw()))
                        .replace("%dpitch", df.format(loc.getPitch()))
                        .replace("%yaw", String.valueOf(loc.getYaw()))
                        .replace("%pitch", String.valueOf(loc.getPitch()))
                ;

                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
            }
            else if (_players.contains(p)) {
                setReducedDebugInfo(p, true);
                
                // clear action bar
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));

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
