package ru.whitebeef.respawncommands;

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
}
