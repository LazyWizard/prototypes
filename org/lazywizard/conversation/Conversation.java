package org.lazywizard.conversation;

import java.util.Map;

class Conversation
{
    private final Map<String, Node> nodes;
    private final Node startingNode;

    Conversation(Map<String, Node> nodes, Node startingNode)
    {
        this.nodes = nodes;
        this.startingNode = startingNode;
    }

    Map<String, Node> getNodes()
    {
        return nodes;
    }

    Node getStartingNode()
    {
        return startingNode;
    }
}
