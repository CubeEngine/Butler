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
package org.cubeengine.butler.parameter.parser;

import java.util.ArrayList;
import java.util.List;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.parameter.argument.Completer;
import org.cubeengine.butler.parameter.Parameter;
import org.cubeengine.butler.parameter.ParsedParameter;
import org.cubeengine.butler.parameter.property.Properties;
import org.cubeengine.butler.parameter.argument.ArgumentParser;

/**
 * A Parameter implementation.
 */
public abstract class SimpleParser implements ParameterParser
{
    protected final Parameter parameter;

    public SimpleParser(Parameter parameter)
    {
        this.parameter = parameter;
    }

    @Override
    public void parse(CommandInvocation invocation, List<ParsedParameter> params, List<Parameter> suggestions)
    {
        ParsedParameter pParam = this.parseValue(invocation);
        if (!params.isEmpty() && params.get(params.size() - 1).getParameter().equals(pParam.getParameter()))
        {
            ParsedParameter last = params.remove(params.size() - 1);
            String joined = last.getParsedValue() + " " + pParam.getParsedValue();
            pParam = ParsedParameter.of(pParam.getParameter(), joined, joined);
        }
        params.add(pParam);
    }

    @Override
    public boolean isPossible(CommandInvocation invocation)
    {
        // Just checking if the parameter is possible here
        int remainingTokens = invocation.tokens().size() - invocation.consumed();
        return remainingTokens >= 1 && remainingTokens >= parameter.getGreed();
    }

    @Override
    public List<String> getSuggestions(CommandInvocation invocation)
    {
        List<String> result = new ArrayList<>();
        Class completerClass = parameter.getProperty(Properties.COMPLETER);
        if (completerClass == null)
        {
            completerClass = parameter.getType();
        }

        Completer completer = invocation.providers().completers().get(completerClass);
        if (completer != null)
        {
            result.addAll(completer.suggest(parameter.getType(), invocation));
        }
        else
        {
            System.out.println("No completer found for " + completerClass);
        }
        return result;
    }

    /**
     * Parses this parameter using given CommandInvocation
     *
     * @param invocation the CommandInvocation
     *
     * @return the ParsedParameter
     */
    protected ParsedParameter parseValue(CommandInvocation invocation)
    {
        return parseValue(invocation, parameter);
    }

    public static ParsedParameter parseValue(CommandInvocation invocation, Parameter parameter)
    {
        int consumed = invocation.consumed();
        ArgumentParser reader = parameter.getProperty(Properties.ARGUMENT_PARSER);
        Object read;
        if (reader != null)
        {
            read = reader.parse(parameter.getType(), invocation);
        }
        else
        {
            read = invocation.providers().read(parameter, invocation);
        }
        String tokens = invocation.tokensSince(consumed);
        return ParsedParameter.of(parameter, read, tokens);
    }
}
