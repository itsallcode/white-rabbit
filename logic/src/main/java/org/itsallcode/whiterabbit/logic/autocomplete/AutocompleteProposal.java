package org.itsallcode.whiterabbit.logic.autocomplete;

public class AutocompleteProposal
{
    private final String text;
    private final int matchPositionStart;
    private final int matchLength;

    public AutocompleteProposal(String text, int matchPositionStart, int matchLength)
    {
        this.text = text;
        this.matchPositionStart = matchPositionStart;
        this.matchLength = matchLength;
    }

    public String getText()
    {
        return text;
    }

    public int getMatchPositionStart()
    {
        return matchPositionStart;
    }

    public int getMatchLength()
    {
        return matchLength;
    }
}
