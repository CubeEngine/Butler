package org.cubeengine.butler;

import org.cubeengine.butler.parameter.argument.Completer;
import org.cubeengine.butler.parameter.argument.ArgumentParser;

/**
 * ArgumentParser and Completer all in one: Your Butler.
 *
 * @param <T> the type
 */
public interface Butler<T> extends ArgumentParser<T>, Completer
{

}
