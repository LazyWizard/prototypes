package org.lazywizard.conversation;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import org.json.JSONException;
import org.json.JSONObject;
import org.lazywizard.conversation.ResponseData.Status;

class Response
{
    private final String id, text, tooltip;
    private final ResponseData responseData;
    private final Node leadsTo;

    Response(String id, JSONObject data) throws JSONException
    {
        this.id = id;
        text = data.getString("text");
        tooltip = data.getString("mouseOverText");
        responseData = null;
        leadsTo = null;
    }

    void onChosen(SectorEntityToken talkingTo)
    {
        if (responseData != null)
        {
            responseData.onChosen(talkingTo);
        }
    }

    String getText()
    {
        return text;
    }

    Status getStatus()
    {
        return responseData.getStatus();
    }

    Node getNodeLedTo()
    {
        return leadsTo;
    }

    String getTooltip()
    {
        return tooltip;
    }
}
