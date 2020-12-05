package org.itsallcode.whiterabbit.logic.autocomplete;

import java.util.Comparator;

public class AutocompleteProposal implements Comparable<AutocompleteProposal>
{
    private static final Comparator<AutocompleteProposal> COMPARATOR = Comparator
            .comparing(AutocompleteProposal::getPriority).thenComparing(AutocompleteProposal::getText);
    private final String text;
    private int priority;
    private final int matchPositionStart;
    private final int matchLength;

    AutocompleteProposal(String text, int priority, int matchPositionStart, int matchLength)
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

    public int getPriority()
    {
        return priority;
    }

    @Override
    public int compareTo(AutocompleteProposal other)
    {
        return COMPARATOR.compare(this, other);
    }
}
