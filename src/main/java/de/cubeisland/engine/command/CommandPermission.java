package de.cubeisland.engine.command;

public interface CommandPermission
{
    public boolean isAuthorized(Permissible permissible);

    String getName();

    public static class PermDefault
    {
        public static final byte DEFAULT = 0;
        public static final byte OP = 1;
        public static final byte TRUE = 2;
        public static final byte FALSE = 3;
        public static final byte NOT_OP = 4;
    }
}
