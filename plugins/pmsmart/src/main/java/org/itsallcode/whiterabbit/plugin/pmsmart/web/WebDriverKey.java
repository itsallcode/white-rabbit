package org.itsallcode.whiterabbit.plugin.pmsmart.web;

/**
 * see https://w3c.github.io/webdriver/#keyboard-actions
 */
public enum WebDriverKey
{

    BACKSPACE("\uE003"), //
    END("\uE010"), //
    RETURN("\uE006");

    private final String code;

    private WebDriverKey(String code)
    {
        this.code = code;
    }

    public String repeat(int times)
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++)
        {
            sb.append(code);
        }
        return sb.toString();
    }

    @Override
    public String toString()
    {
        return code;
    }
}
