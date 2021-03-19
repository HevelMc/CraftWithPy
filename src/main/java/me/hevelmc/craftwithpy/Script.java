package me.hevelmc.craftwithpy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class Script implements Listener {
    private final String name;
    private final PythonInterpreter interpreter = new PythonInterpreter();
    private PyObject instance;

    public Script(String name) {
        this.name = name;
        enable();
    }

    public String getName() {
        return name;
    }

    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, Main.INSTANCE);
        interpreter.cleanup();
        try {
            // Encoding
            interpreter.exec("# -*- coding: iso-8859-1 -*-");

            // core.py
            interpreter.execfile(getClass().getClassLoader().getResourceAsStream("core.py"));

            // File
            interpreter.execfile("Scripts/" + name + ".py");
            this.instance = interpreter.eval(this.name + "()");

        } catch (Exception message) {
            String error_message = "§cError on §lenabling§c module §b" + this.name + "§c :\n§7" + message + "§r";
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.hasPermission("host.execute_command")) p.sendMessage(error_message);
            Bukkit.getConsoleSender().sendMessage(error_message);
        }
        runFunctionHandle("on_enable");
    }

    public void runFunctionHandle(String function, Object... args) {
        String message = runFunction(function, args);
        if (message != null) {
            String error_message = "§cError on module §b" + this.name + "§c, when executing function §e" + function + "§c :\n§7" + message + "§r";
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.hasPermission("host.execute_command")) p.sendMessage(error_message);
            Bukkit.getConsoleSender().sendMessage(error_message);
        }
    }

    public String runFunction(String name, Object... args) {
        try {
            PyMethod method = (PyMethod) instance.__findattr__(name);
            if (method != null) method._jcall(args);
        } catch (Exception message) {
            return message.toString();
        }
        return null;
    }

    public void disable() {
        runFunctionHandle("on_disable");
    }
}