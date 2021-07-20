package org.itsallcode.whiterabbit.jfxui.tray;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dimension;
import java.awt.Image;

import org.junit.jupiter.api.Test;

class TrayUtilTest
{
    @Test
    void test()
    {
        final Image image = TrayUtil.loadImage(new Dimension(32, 32));
        assertThat(image).isNotNull();
        assertThat(image.getWidth(null)).isEqualTo(32);
        assertThat(image.getHeight(null)).isEqualTo(32);
    }
}
