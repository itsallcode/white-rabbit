package org.itsallcode.whiterabbit.logic.autocomplete;

import java.util.Comparator;

public final class AutocompleteProposal implements Comparable<AutocompleteProposal>
{
    private static final Comparator<AutocompleteProposal> COMPARATOR = Comparator
            .comparing(AutocompleteProposal::getPriority).reversed().thenComparing(AutocompleteProposal::getText);
    private final String text;
    private final long priority;
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + matchLength;
        result = prime * result + matchPositionStart;
        result = prime * result + (int) (priority ^ (priority >>> 32));
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final AutocompleteProposal other = (AutocompleteProposal) obj;
        if (matchLength != other.matchLength)
        {
            return false;
        }
        if (matchPositionStart != other.matchPositionStart)
        {
            return false;
        }
        if (priority != other.priority)
        {
            return false;
        }
        if (text == null)
        {
            if (other.text != null)
            {
                return false;
            }
        }
        else if (!text.equals(other.text))
        {
            return false;
        }
        return true;
    }
}
