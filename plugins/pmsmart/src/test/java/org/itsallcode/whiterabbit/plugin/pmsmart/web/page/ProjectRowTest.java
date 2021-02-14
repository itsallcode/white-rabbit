package org.itsallcode.whiterabbit.plugin.pmsmart.web.page;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProjectRowTest
{
    @Test
    void shortenComment_leavesShortCommentsUnchanged()
    {
        assertShortend("short comment", "short comment");
    }

    @Test
    void shortenComment_leavesCommentWithMaxLengthUnchanged()
    {
        assertShortend("c".repeat(250), "c".repeat(250));
    }

    @Test
    void shortenComment_shortensTooLongComment()
    {
        assertShortend("c".repeat(251), "c".repeat(247) + "...");
    }

    private void assertShortend(String input, String expected)
    {
        assertThat(ProjectRow.shortenComment(input)).isEqualTo(expected);
    }
}
