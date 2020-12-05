package org.itsallcode.whiterabbit.logic.autocomplete;

import java.util.Comparator;

public class AutocompleteProposal implements Comparable<AutocompleteProposal>
{
    private static final Comparator<AutocompleteProposal> COMPARATOR = Comparator
            .comparing(AutocompleteProposal::getPriority).reversed().thenComparing(AutocompleteProposal::getText);
    private final String text;
    private long priority;
    private final int matchPositionStart;
    private final int matchLength;

    AutocompleteProposal(String text, long priority, int matchPositionStart, int matchLength)
    {
        this.text = text;
        this.priority = priority;
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

    public long getPriority()
    {
        return priority;
    }

    @Override
    public int compareTo(AutocompleteProposal other)
    {
        return COMPARATOR.compare(this, other);
    }
}
