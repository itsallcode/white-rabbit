package org.itsallcode.whiterabbit.jfxui.testutil;

import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.Node;
import javafx.scene.Parent;

public class UiDebugTool
{
    private static final Logger LOG = LogManager.getLogger(UiDebugTool.class);

    public static void printNode(Node node)
    {
        printChildren(node, 0);
    }

    private static void printChildren(Node node, int level)
    {
        printNode(node, level);

        if (node instanceof Parent)
        {
            for (final Node child : ((Parent) node).getChildrenUnmodifiable())
            {
                printChildren(child, level + 2);
            }
        }
    }

    private static void printNode(Node node, int level)
    {
        LOG.info("{} {}", createIndentation(level), node);
    }

    private static String createIndentation(int level)
    {
        return String.join("", Collections.nCopies(level, " "));
    }
}
