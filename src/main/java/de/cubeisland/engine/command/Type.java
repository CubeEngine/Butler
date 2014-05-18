package de.cubeisland.engine.command;

public enum Type
{
    ANY,
    NOTHING,
    FLAG_OR_INDEXED,
    INDEXED_OR_PARAM,
    PARAM_VALUE;

    public static class LastType
    {
        public Type last;
    }
}
