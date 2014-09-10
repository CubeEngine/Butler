package de.cubeisland.engine.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.cubeisland.engine.command.parameter.Parameter;
import de.cubeisland.engine.command.parameter.ParsedParameter;

public class ContextDescriptor implements Parameter
{
    private List<Parameter> inOrder;
    private List<Parameter> noOrder;
    private List<Parameter> flags;

    // TODO Build ME!!!

    public List<Parameter> getInOrder()
    {
        return inOrder;
    }

    public List<Parameter> getNoOrder()
    {
        return noOrder;
    }

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
    public ParsedParameter parse(String[] tokens, int beginOffset)
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
                ParsedParameter parsed = this.parse(parameters, paramFlags, tokens, offset, false);
                if (parsed == null)
                {
                    parsed = this.parse(parameters, paramNoOrder, tokens, offset, false);
                    if (parsed == null)
                    {
                        parsed = this.parse(parameters, paramInOrder, tokens, offset, true); // only check take first to preserve order
                        if (parsed == null)
                        {
                            throw new IllegalArgumentException(); // TODO CmdException cannot parse input!!! too many arguments
                        }
                    }
                }
                offset += parsed.getConsumed();
            }
        }
        return ParsedParameter.of(this, parameters);
    }

    private ParsedParameter parse(LinkedList<ParsedParameter> resultList, List<Parameter> searchList, String[] tokens, int offset,
                      boolean onlyFirst)
    {
        ParsedParameter parsed = null;
        for (Parameter parameter : searchList)
        {
            if (parameter.accepts(tokens, offset))
            {
                parsed = parameter.parse(tokens, offset);
                resultList.add(parsed);
                break;
            }
            if (onlyFirst)
            {
                break;
            }
        }
        if (parsed != null)
        {
            searchList.remove(resultList.getLast().getParameter()); // No reuse
        }
        return parsed;
    }
}
