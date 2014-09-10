package de.cubeisland.engine.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.cubeisland.engine.command.parameter.Parameter;
import de.cubeisland.engine.command.parameter.ParsedParameter;

/**
 * A ContextDescriptor providing grouped Parameters
 */
public class ContextDescriptor implements Parameter
{
    private List<Parameter> inOrder;
    private List<Parameter> noOrder;
    private List<Parameter> flags;

    // TODO Build ME!!!

    /**
     * Returns the Parameters that are in order
     *
     * @return the parameters
     */
    public List<Parameter> getInOrder()
    {
        return inOrder;
    }

    /**
     * Returns the Parameters that are not in a particular order
     *
     * @return the parameters
     */
    public List<Parameter> getNoOrder()
    {
        return noOrder;
    }

    /**
     * Returns the Parameters that are flags
     *
     * @return the flagss
     */
    public List<Parameter> getFlags()
    {
        return flags;
    }

    @Override
    public boolean accepts(String[] tokens, int offset)
    {
        return true;
    }

    @Override
    public ParsedParameter parse(CommandCall call, String[] tokens, int beginOffset)
    {
        LinkedList<Parameter> paramInOrder = new LinkedList<>(this.getInOrder());
        List<Parameter> paramNoOrder = new ArrayList<>(this.getNoOrder());
        List<Parameter> paramFlags = new ArrayList<>(this.getFlags());

        LinkedList<ParsedParameter> parameters = new LinkedList<>();
        int offset = beginOffset;
        for (; offset < tokens.length; offset++)
        {
            String token = tokens[offset];
            if (token.isEmpty())
            {
                // ignore empty args except last
                if (offset == tokens.length - 1)
                {
                    parameters.add(ParsedParameter.empty());
                }
            }
            else
            {
                ParsedParameter parsed = this.parse(call, paramFlags, tokens, offset, false);
                if (parsed == null)
                {
                    parsed = this.parse(call, paramNoOrder, tokens, offset, false);
                    if (parsed == null)
                    {
                        parsed = this.parse(call, paramInOrder, tokens, offset, true); // only check take first to preserve order
                        if (parsed == null)
                        {
                            throw new IllegalArgumentException(); // TODO CmdException cannot parse input!!! too many arguments
                        }
                    }
                }
                parameters.add(parsed);
                offset += parsed.getConsumed();
            }
        }
        return ParsedParameter.of(this, parameters);
    }

    /**
     * Creates a parsed Parameter for the current token
     *
     *
     * @param call
     * @param searchList the list to search the parameter in
     * @param tokens the tokens
     * @param offset the offset
     * @param onlyFirst when true only try the first parameter
     * @return the parsed parameter or null if not applicable
     */
    private ParsedParameter parse(CommandCall call, List<Parameter> searchList, String[] tokens, int offset,
                                  boolean onlyFirst)
    {
        ParsedParameter parsed = null;
        for (Parameter parameter : searchList)
        {
            if (parameter.accepts(tokens, offset))
            {
                parsed = parameter.parse(call, tokens, offset);
                break;
            }
            if (onlyFirst)
            {
                break;
            }
        }
        if (parsed != null)
        {
            searchList.remove(parsed.getParameter()); // No reuse
        }
        return parsed;
    }
}
