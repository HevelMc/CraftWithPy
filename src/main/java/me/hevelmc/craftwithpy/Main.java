package me.hevelmc.craftwithpy;

import me.hevelmc.craftwithpy.commands.PyCommand;
import me.hevelmc.craftwithpy.events.PyEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class Main extends JavaPlugin {
    private static final ArrayList<Script> scriptsList = new ArrayList<>();
    public static JavaPlugin INSTANCE;

    public static ArrayList<Script> getScriptsList() {
        return scriptsList;
    }

    public static void sendEvent(Event event) {
        for (Script script : scriptsList) {
            ArrayList<String> methods = script.getEventMethods(event.getEventName());
            if (methods != null) for (String eventMethod : methods) script.runFunctionHandle(eventMethod, event);
        }
    }

    public static Script getScript(String script_name) {
        for (Script script : scriptsList) {
            if (script.getName().equals(script_name)) return script;
        }
        return null;
    }

    public static void loadEnabledScripts(String path, boolean recursive) {
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                // TODO recursive loading
                if (child.getName().endsWith(".py") && !child.getName().startsWith("-")) {
                    PyCommand.enableScript(
                            child.getName().replace(".py", ""),
                            Bukkit.getConsoleSender()
                    );
                }
            }
        } else {
            if (recursive) return;
            if (dir.mkdir()) {
                Bukkit.getConsoleSender().sendMessage("§aThe folder §b" + path + "§a was missing, it has been created!");
                return;
            }
            Bukkit.getConsoleSender().sendMessage("§cThe folder §b" + path + "§c cannot be created, operation aborted!");
        }
    }

    @Override
    public void onEnable() {
        System.setProperty("file.encoding","UTF-8");
        INSTANCE = this;

        PluginCommand cmd = this.getCommand("py");
        if (cmd != null) cmd.setExecutor(new PyCommand());

        PyEvent.initAllEvents();

        // TODO script folder configurable in config
        loadEnabledScripts("Scripts/", false);

        Bukkit.getConsoleSender().sendMessage(
                "\n§7------ ------ ------ ------§r"
                + "\n§2CraftWithPy has been enabled!§r"
                + "\n§3 - Enjoy your code!§r"
                + "\n§7------ ------ ------ ------§r"
        );
    }
}
