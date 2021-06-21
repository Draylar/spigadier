package draylar.spigadier;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;

/**
 * This class provides utilities for registering commands without {@code plugin.yml} files.
 */
public class CommandRegistry {

    /**
     * Registers a {@link Command} with the specified prefix.
     *
     * <p>
     * The {@code prefix} is primarily used for conflict avoidance, and is essentially
     *  the namespace of a Identifier/ResourceLocation from vanilla Minecraft.
     *
     * @param prefix prefix of the command
     * @param command command instance to register
     */
    public static void register(String prefix, Command command) {
        try {
            Field field = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) field.get(Bukkit.getPluginManager());
            commandMap.register(prefix, command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private CommandRegistry() {
        // NO-OP
    }
}
