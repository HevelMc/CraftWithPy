package me.hevelmc.craftwithpy.commands;

import me.hevelmc.craftwithpy.Main;
import me.hevelmc.craftwithpy.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Arrays;

public class PyCommand implements CommandExecutor {

    private static final DCommandExecutor executor = new DCommandExecutor();

    public static void registerNewCommand(String cmd, String usage, String desc, String permission, String[] aliases) {
        new DCommand(cmd, usage, desc, permission, Arrays.asList(aliases), executor, Main.INSTANCE);
    }

    public static void reloadScript(String name, CommandSender s) {
        if (scriptExists(name)) {
            disableScript(name, s);
            enableScript(name, s);
        } else s.sendMessage("§cScript §b" + name + "§c does not exit.");
    }

    public static void enableScript(String name, CommandSender s) {
        if (scriptExists(name)) {
            if (scriptEnabled(name)) return;
            Main.getScriptsList().add(new Script(name.toLowerCase()));
            s.sendMessage("§aScript §b" + name + "§a successfully enabled!");
        } else s.sendMessage("§cScript §b" + name + "§c does not exit.");
    }

    public static void disableScript(String name, CommandSender s) {
        if (scriptExists(name)) {
            if (!scriptEnabled(name)) return;
            Script script = getScript(name);
            if (script != null) {
                script.disable();
                Main.getScriptsList().remove(script);
            }
            s.sendMessage("§aScript §b" + name + "§a successfully disabled!");
        } else s.sendMessage("§cScript §b" + name + "§c does not exit.");
    }

    public static void TryEditInConfig(String name, boolean disabled, CommandSender s) {
        switch (editInConfig(name, disabled)) {
            case 1:
                s.sendMessage("§aFile successfully edited in config!");
                break;
            case 0:
                s.sendMessage("§bFile already " + (disabled ? "disabled" : "enabled") + " in config!");
                break;
            case -1:
                s.sendMessage("§cError when editing the config, file is missing!");
                break;
            case -2:
                s.sendMessage("§cError when editing the config, check the console for details !");
                break;
        }
    }

    public static boolean scriptEnabled(String name) {
        return getScript(name) != null;
    }

    public static Script getScript(String name) {
        for (Script script : Main.getScriptsList()) if (script.getName().equalsIgnoreCase(name)) return script;
        return null;
    }

    public static boolean scriptExists(String name) {
        File f = new File("Scripts/" + name + ".py");
        return f.exists();
    }

    public static boolean hasPermission(CommandSender s) {
        return s.hasPermission("host.execute_command");
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

        // TODO : HELP COMMAND
        if (args.length == 0) {
            s.sendMessage("§aTODO HELP");
            return true;
        }

        switch (args[0]) {
            case "reload":
                if (InvalidPermission(s)) return true;
                if (IncorrectArgs(args, 2, s, "§cYou need to specify a script.")) return false;
                reloadScript(args[1], s);
                break;

            case "enable":
                if (InvalidPermission(s)) return true;
                if (IncorrectArgs(args, 2, s, "§cYou need to specify a script.")) return false;
                TryEditInConfig(args[1], false, s);
                enableScript(args[1], s);
                break;

            case "disable":
                if (InvalidPermission(s)) return true;
                if (IncorrectArgs(args, 2, s, "§cYou need to specify a script.")) return false;
                disableScript(args[1], s);
                TryEditInConfig(args[1], true, s);
                break;

            default:
                s.sendMessage("§aTODO HELP");
        }
        return true;
    }
    
    private static boolean InvalidPermission(CommandSender s) {
        if (!hasPermission(s)) {
            s.sendMessage("§cYou don't have the permission to use this command.");
            return true;
        }
        return false;
    }

    private static boolean IncorrectArgs(String[] args, int minimum, CommandSender s, String errorMsg) {
        if (args.length < minimum) {
            s.sendMessage(errorMsg);
            return true;
        }
        return false;
    }

    public static int editInConfig(String name, boolean disabled) {
        // TODO Path from config
        File file;
        file = new File("Scripts" + "/" + name + ".py");
        if (!file.exists()) file = new File("Scripts" + "/-" + name + ".py");
        // -1: file not found
        if (!file.exists()) return -1;

        String filepath = file.getAbsolutePath();
        File renamedFile;
        if (disabled) renamedFile = new File("Scripts" + "/-" + name + ".py");
        else renamedFile = new File("Scripts" + "/" + name + ".py");
        // 0: the file is already renamed
        if (filepath.equals(renamedFile.getAbsolutePath())) return 0;
        // 1: The file has been renamed
        // -2: An error occurred
        try {
            return (file.renameTo(renamedFile)) ? 1 : -2;
        } catch (Exception error) {
            error.printStackTrace();
            return -2;
        }
    }
}
