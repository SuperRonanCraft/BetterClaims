package me.RonanCraft.Pueblos.resources;

public enum PermissionNodes {

    USE("use"),
    RELOAD("reload");

    public String node;

    PermissionNodes(String node) {
        this.node = "pueblos." + node;
    }
}
