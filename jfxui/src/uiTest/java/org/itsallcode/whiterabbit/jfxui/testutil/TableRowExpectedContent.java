package org.itsallcode.whiterabbit.jfxui.testutil;

public interface TableRowExpectedContent
{
    Object[] expectedCellContent();

    public static TableRowExpectedContent forValues(Object... cellValues)
    {
        return new TableRowExpectedContent()
        {
            @Override
            public Object[] expectedCellContent()
            {
                return cellValues;
            }
        };
    }
}
