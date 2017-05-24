/*
 * The MIT License
 * Copyright © 2014 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cubeengine.butler.alias;

public class AliasConfiguration
{
    private final String name;
    private final String[] dispatcher;
    private String prefix;
    private String suffix;

    /**
     * An alias on the same dispatcher as the commands that gets registered
     *
     * @param name the name
     */
    public AliasConfiguration(String name)
    {
        this.name = name;
        this.dispatcher = null;
    }

    /**
     * An alias on a specific dispatcher relative to the base dispatcher
     *
     * @param name the alias
     * @param dispatcher the dispatcher names
     */
    public AliasConfiguration(String name, String... dispatcher)
    {
        this.name = name;
        this.dispatcher = dispatcher;
    }

    /**
     * Returns the name of the alias
     *
     * @return the name of the alias
     */
    public String getName()
    {
        return name;
    }

    /**
     * The dispatcher names or null if to add under the same dispatcher as the main command
     *
     * @return the dispatcher or null
     */
    public String[] getDispatcher()
    {
        return dispatcher;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }

    public String getSuffix()
    {
        return suffix;
    }
}
