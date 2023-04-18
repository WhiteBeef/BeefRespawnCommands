package ru.whitebeef.respawncommands;

import org.bukkit.entity.Player;

import java.util.Objects;

public class Command {

    private final String namespace;
    private final String command;
    private final String permission;

    public Command(String namespace, String command, String permission) {
        this.namespace = namespace;
        this.command = command;
        this.permission = permission;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getCommand() {
        return command;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(permission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Command command = (Command) o;
        return Objects.equals(namespace, command.namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace);
    }
}
