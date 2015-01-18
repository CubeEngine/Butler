/**
 * The MIT License
 * Copyright (c) 2014 Cube Island
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
package de.cubeisland.engine.command.util.property;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a Set of Properties
 */
public class PropertyHolder
{
    protected final Map<Class<? extends Property>, Property> properties = new HashMap<>();

    /**
     * Checks if given property is set
     *
     * @param property the property to check
     *
     * @return whether this parameter has the property
     */
    public final boolean hasProperty(Class<? extends Property> property)
    {
        return this.properties.containsKey(property);
    }

    /**
     * Sets given property
     *
     * @param property the property to set
     */
    public void setProperty(Property property)
    {
        this.properties.put(property.getClass(), property);
    }

    /**
     * Gets the value of given property
     *
     * @param propertyT the property
     * @param <ValueT> the values Type
     * @return the value of the property or null if not given
     */
    @SuppressWarnings("unchecked")
    public final <ValueT> ValueT valueFor(Class<? extends Property<ValueT>> propertyT)
    {
        Property property = this.properties.get(propertyT);
        return property == null ? null : (ValueT)property.value();
    }

    /**
     * Sets multiple properties at once
     *
     * @param properties the properties to set
     */
    public final void setProperties(Property... properties)
    {
        for (Property property : properties)
        {
            this.setProperty(property);
        }
    }
}
