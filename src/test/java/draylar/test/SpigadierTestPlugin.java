package draylar.test;

import draylar.spigadier.CommandRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigadierTestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();

        // Register our /compliment plugin using Brigadier
        CommandRegistry.register("test", new ComplimentCommand());
    }
}
