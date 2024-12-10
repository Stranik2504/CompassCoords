package stranik.dev.compassCoords;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class CompassCoords extends JavaPlugin {
    private BukkitTask _task;
    
    public static CompassCoords getInstance() { return getPlugin(CompassCoords.class); }
    public static ProtocolManager getProtocolManager() { return ProtocolLibrary.getProtocolManager(); }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        if (getConfig().getBoolean("enable")) startTask();
    }

    @Override
    public void onDisable() {
        stopTask();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("smallifyEventHandler.commands"))
            return false;

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            
            if (getConfig().getBoolean("enable")) startTask(); else stopTask();

            sender.sendMessage("Config successfully reloaded");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("enable")) {
            getConfig().set("enable", !getConfig().getBoolean("enable"));
            saveConfig();

            if (getConfig().getBoolean("enable")) startTask(); else stopTask();

            sender.sendMessage("Enable plugin state now is " + (getConfig().getBoolean("enable") ? "enabled" : "disabled"));
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("text")) {
            StringBuilder result = new StringBuilder();
            
            for (var i = 1; i < args.length; i++) {
                result.append(args[i]);
                
                if (i + 1 < args.length)
                    result.append(" ");
            }
            
            getConfig().set("text", result.toString());
            saveConfig();

            sender.sendMessage("Text set to : " + result + " successfully");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("delay")) {
            getConfig().set("delay", Integer.valueOf(args[1]));
            saveConfig();

            sender.sendMessage("Delay set to : " + args[1] + " successfully");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            sender.sendMessage(
                    "Enabled: " + getConfig().getBoolean("enable") + "\n" +
                        "Delay: " + getConfig().getInt("delay") + "\n" +
                        "Text: " + getConfig().getString("text") + "\n"
            );
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].trim().isEmpty())
                return List.of("reload", "enable", "delay", "text", "info");

            ArrayList<String> hints = new ArrayList<>();

            if ("reload".startsWith(args[0]) && !"reload".equals(args[0]))
                hints.add("reload");

            if ("enable".startsWith(args[0]) && !"enable".equals(args[0]))
                hints.add("enable");

            if ("delay".startsWith(args[0]) && !"delay".equals(args[0]))
                hints.add("delay");

            if ("text".startsWith(args[0]) && !"text".equals(args[0]))
                hints.add("text");

            if ("info".startsWith(args[0]) && !"info".equals(args[0]))
                hints.add("info");

            return hints;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("delay")) {
            return List.of("<delay>");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("text")) {
            return List.of("<text>");
        }

        return new ArrayList<>();
    }
    
    private void startTask() {
        if (_task != null && !_task.isCancelled())
            return;

        _task = Bukkit
            .getScheduler()
            .runTaskTimerAsynchronously(
                    getInstance(),
                    Runner::run,
                    getConfig().getInt("delay"),
                    getConfig().getInt("delay")
            );
    }
    
    private void stopTask() {
        if (_task == null || _task.isCancelled())
            return;
        
        _task.cancel();
    }
}

