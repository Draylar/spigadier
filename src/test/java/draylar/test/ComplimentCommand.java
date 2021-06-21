package draylar.test;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import draylar.spigadier.SpigadierCommand;
import net.minecraft.server.v1_16_R3.*;

public class ComplimentCommand extends SpigadierCommand {

    public ComplimentCommand() {
        super("compliment");

        // Root node
        LiteralCommandNode<CommandListenerWrapper> root = literal("compliment")
                .executes(context -> {
                    context.getSource().getBukkitSender().sendMessage("Who do you want to compliment?");
                    return 1;
                }).build();

        // Specific player
        ArgumentCommandNode<CommandListenerWrapper, EntitySelector> player = argument("player", ArgumentEntity.c())
                .then(argument("compliment", StringArgumentType.greedyString())
                    .suggests((context, builder) -> {
                        builder.suggest("You are EPIC!");
                        builder.suggest("I LOVE you!");
                        builder.suggest("You are cooler than a turtle!");
                        return builder.buildFuture();
                    }).executes(context -> {
                            Entity target = ArgumentEntity.a(context, "player");
                            target.getBukkitEntity().sendMessage(StringArgumentType.getString(context, "compliment"));
                            return 1;
                        })
                )
                .executes(context -> {
                    Entity target = ArgumentEntity.a(context, "player");
                    target.getBukkitEntity().sendMessage("YOU are awesome!");
                    return 1;
                }).build();

        // All players
        LiteralCommandNode<CommandListenerWrapper> everyone = literal("everyone")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    context.getSource().getWorld().getPlayers().forEach(target -> {
                        target.getBukkitEntity().sendMessage("YOU are awesome!");
                    });

                    return 1;
                }).build();

        root.addChild(player);
        root.addChild(everyone);
        addRootElement(root);
    }
}