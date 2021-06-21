package draylar.spigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.v1_16_R3.CommandList;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.command.ServerCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SpigadierCommand extends Command {

    private static final CommandDispatcher<CommandListenerWrapper> root = new CommandDispatcher<>();

    public SpigadierCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        @Nullable CommandListenerWrapper wrapper = getWrapper(sender);

        if(wrapper != null) {
            try {
                if (root.execute(getInputString(args), wrapper) == 1) {
                    return true;
                }
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args, Location location) throws IllegalArgumentException {
        return tabComplete(sender, alias, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        @Nullable CommandListenerWrapper wrapper = getWrapper(sender);

        if(wrapper != null) {
            Suggestions suggestions = null;
            try {
                suggestions = root.getCompletionSuggestions(root.parse(getInputString(args), wrapper)).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            return suggestions == null ? Collections.emptyList() : suggestions.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Nullable
    private CommandListenerWrapper getWrapper(CommandSender sender) {
        if(sender instanceof CraftPlayer player) {
            return player.getHandle().getCommandListener();
        } else if (sender instanceof ServerCommandSender server) {
            if(server.getServer() instanceof CraftServer craftServer) {
                return craftServer.getServer().getServerCommandListener();
            }
        }

        return null;
    }

    private String getInputString(String[] args) {
        return args.length > 0 ? String.format("%s %s", getName(), String.join(" ", Arrays.asList(args))) : getName();
    }

    public static LiteralArgumentBuilder<CommandListenerWrapper> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<CommandListenerWrapper, T> argument(String literal, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(literal, type);
    }

    public void addRootElement(CommandNode<CommandListenerWrapper> node) {
        root.getRoot().addChild(node);
    }
}
