package me.hevelmc.craftwithpy.events;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import me.hevelmc.craftwithpy.Main;
import me.hevelmc.craftwithpy.Script;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.bukkit.Bukkit.getLogger;

public class PyEvent implements Listener {
    private static final ArrayList<Class<? extends Event>> availableEvents = new ArrayList<>();
    private static final Listener listener = new Listener() {};
    private static final EventExecutor executor = (ignored, event) -> Main.sendEvent(event);

    public static void initAllEvents() {
        ClassInfoList events = new ClassGraph()
                .enableClassInfo()
                .scan() //TODO We should use try-catch-resources here instead
                .getClassInfo(Event.class.getName())
                .getSubclasses()
                .filter(info -> !info.isAbstract());

        try {
            for (ClassInfo event : events) {
                //noinspection unchecked
                Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(event.getName());

                availableEvents.add(eventClass);
            }

        } catch (ClassNotFoundException e) {
            throw new AssertionError("Scanned class wasn't found", e);
        }

        getLogger().info("Events found: " + events.size());
        getLogger().info("HandlerList size: " + HandlerList.getHandlerLists().size());
    }

    public static void registerEventClass(Class<? extends Event> eventClass) {
        for (Method method : eventClass.getMethods()) {
            if (method.getParameterCount() == 0 && method.getName().equals("getHandlers")) {
                try {
                    Method getHandlerListMethod = eventClass.getMethod("getHandlerList");
                    HandlerList handlerList = (HandlerList) getHandlerListMethod.invoke(null);
                    handlerList.unregister(listener);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                Bukkit.getPluginManager().registerEvent(eventClass, listener, EventPriority.NORMAL, executor, Main.INSTANCE);
                break;
            }
        }
    }

    public static boolean registerNewEvent(String eventName, String script_name, String function_name) {
        for (Class<? extends Event> event : availableEvents) {
            if (event.getName().endsWith("." + eventName)) {
                registerEventClass(event);
                Script script = Main.getScript(script_name);
                if (script == null) return false;
                script.addEventMethod(eventName, function_name);
                return true;
            }
        }
        return false;
    }
}
