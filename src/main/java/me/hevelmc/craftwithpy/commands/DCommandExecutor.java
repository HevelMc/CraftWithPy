package me.hevelmc.craftwithpy.commands;

import me.hevelmc.craftwithpy.Main;
import me.hevelmc.craftwithpy.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        for (Script script : Main.getScriptsList()) {
            script.runFunctionHandle("on_command", sender, cmd, alias, args);
        }
        return true;
    }
}
