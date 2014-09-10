/**
 * This file is part of CubeEngine.
 * CubeEngine is licensed under the GNU General Public License Version 3.
 *
 * CubeEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CubeEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CubeEngine.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cubeisland.engine.command.old.base.method;

import de.cubeisland.engine.command.old.Completer;

public @interface Indexed
{
    Class type() default String.class;
    Class reader() default Void.class;
    int greed() default 1;
    boolean req() default true;

    String label();
    String desc() default "";

    String[] staticValues() default {};

    Class<? extends Completer> completer() default Completer.class;
}
