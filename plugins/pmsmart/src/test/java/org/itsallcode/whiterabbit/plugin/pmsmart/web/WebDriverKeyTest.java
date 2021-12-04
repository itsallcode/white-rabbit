package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WebDriverKeyTest
{
    @Test
    void repeat()
    {
        final WebDriverKey key = WebDriverKey.BACKSPACE;
        assertThat(key.repeat(20).toString()).hasSize(key.toString().length() * 20);
    }
}
