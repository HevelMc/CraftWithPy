package me.hevelmc.craftwithpy.commands;

import me.hevelmc.craftwithpy.Main;
import me.hevelmc.craftwithpy.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DCommandExecutor implements CommandExecutor {

    private final String script_name;
    private final String function_name;

    public DCommandExecutor(String script, String function_name) {
        this.script_name = script;
        if (this.script_name == null) System.out.println("script is null!");
        this.function_name = function_name;
        if (this.function_name == null) System.out.println("function is null!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        Script script = Main.getScript(script_name);
        if (script == null) return false;
        script.runFunctionHandle(function_name, sender, cmd, alias, args);
        return true;
    }
}
