### Spigadier

Spigadier implements Mojang's [Brigadier](https://github.com/Mojang/brigadier) library on Spigot.

---

### Usage

Spigadier implements a test command inside the /test/ source directory which showcases the power of Brigadier. 
A minimized example can be seen below:

```java
public class ComplimentCommand extends SpigadierCommand {

    public ComplimentCommand() {
        super("compliment");

        // Root node
        LiteralCommandNode<CommandListenerWrapper> root = literal("compliment")
                .executes(context -> {
                    context.getSource().getBukkitSender().sendMessage("You are awesome!");
                    return 1;
                }).build();
        
        addRootElement(root);
    }
}
```

```java
public class SpigadierTestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();

        // Register our /compliment plugin using Brigadier
        CommandRegistry.register("test", new ComplimentCommand());
    }
}
```