package org.cubeengine.butler;

import java.util.List;
import org.cubeengine.butler.parameter.enumeration.EnumName;
import org.cubeengine.butler.parameter.enumeration.SimpleEnumButler;
import org.cubeengine.butler.provider.ProviderManager;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@EnumName("Tests")
enum TestEnum {
    AA,
    AB,
    BB,
    CC_DD
}

public class EnumerationTest
{
    private static CommandInvocation cmd(String commandLine)
    {
        return new CommandInvocation(null, commandLine, new ProviderManager());
    }

    @Test
    public void testEnumCompleter()
    {
        List<String> expected = asList("AA", "AB");
        SimpleEnumButler simpleEnumButler = new SimpleEnumButler();
        List<String> actual = simpleEnumButler.suggest(TestEnum.class, cmd("a"));
        assertEquals(expected, actual);

    }
}
