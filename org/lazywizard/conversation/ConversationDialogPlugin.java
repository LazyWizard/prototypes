package org.lazywizard.conversation;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import java.awt.Color;
import org.apache.log4j.Level;

class ConversationDialogPlugin implements InteractionDialogPlugin
{
    private final SectorEntityToken talkingTo;
    private final boolean devMode;
    private InteractionDialogAPI dialog;
    private TextPanelAPI text;
    private OptionPanelAPI options;
    private final Conversation conv;
    private Node currentNode;

    ConversationDialogPlugin(SectorEntityToken talkingTo, Conversation conv)
    {
        this.talkingTo = talkingTo;
        devMode = Global.getSettings().isDevMode();
        this.conv = conv;
    }

    @Override
    public void init(InteractionDialogAPI dialog)
    {
        this.dialog = dialog;
        this.text = dialog.getTextPanel();
        this.options = dialog.getOptionPanel();

        goToNode(conv.getStartingNode());
    }

    private void endConversation()
    {
        dialog.dismiss();
    }

    private void checkAddResponse(Node.Response response)
    {
        // Dev mode = on, allow player to choose even disabled/hidden options
        if (devMode)
        {
            switch (response.getStatus())
            {
                case ENABLED:
                    options.addOption(response.getText(), response, response.getTooltip());
                    break;
                case DISABLED:
                    options.addOption("[DISABLED] " + response.getText(),
                            response, Color.YELLOW, response.getTooltip());
                    break;
                case HIDDEN:
                    options.addOption("[HIDDEN] " + response.getText(),
                            response, Color.RED, response.getTooltip());
                    break;
                default:
                    Global.getLogger(ConversationDialogPlugin.class).log(Level.ERROR,
                            "Unsupported status: " + response.getStatus().name());
            }

            return;
        }

        // Dev mode = off, respect visibility status
        switch (response.getStatus())
        {
            case ENABLED:
                options.addOption(response.getText(), response, response.getTooltip());
                break;
            case DISABLED:
                options.addOption(response.getText(), response, response.getTooltip());
                options.setEnabled(response, false);
                break;
            default:
                Global.getLogger(ConversationDialogPlugin.class).log(Level.ERROR,
                        "Unsupported status: " + response.getStatus().name());
        }
    }

    private void goToNode(Node node)
    {
        // Conversation ends when the response chosen doesn't lead to another node
        if (node == null)
        {
            endConversation();
            return;
        }

        currentNode = node;
        text.addParagraph(node.getText());
        options.clearOptions();

        for (Node.Response response : node.getResponses())
        {
            checkAddResponse(response);
        }
    }

    @Override
    public void optionSelected(String optionText, Object optionData)
    {
        Node.Response response = (Node.Response) optionData;
        text.addParagraph(response.getText(), Color.CYAN);
        response.onChosen(talkingTo, dialog);
        goToNode(conv.getNodes().get(response.getNodeLedTo()));
    }

    @Override
    public void optionMousedOver(String optionText, Object optionData)
    {
    }

    @Override
    public void advance(float amount)
    {
    }

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult)
    {
    }

    @Override
    public Object getContext()
    {
        return null;
    }
}
