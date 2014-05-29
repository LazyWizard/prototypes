package org.lazywizard.conversation;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.lazywizard.conversation.ResponseStatusScript.Status;

class Node
{
    private final String text;
    private final List<Response> responses;

    Node(String nodeId, JSONObject data) throws JSONException
    {
        text = data.getString("text");
        responses = new ArrayList<>();

        JSONObject rData = data.getJSONObject("responses");
        for (Iterator keys = rData.keys(); keys.hasNext();)
        {
            String responseId = (String) keys.next();
            responses.add(new Response(responseId, rData.getJSONObject(responseId)));
        }
    }

    String getText()
    {
        return text;
    }

    List<Response> getResponses()
    {
        return responses;
    }

    class Response
    {
        private final String responseId, text, tooltip;
        private final ResponseStatusScript status;
        private final String leadsTo;
        private final OnChosenScript script;

        Response(String responseId, JSONObject data) throws JSONException
        {
            this.responseId = responseId;
            text = data.getString("text");
            leadsTo = JSONParser.getObjectOrNull(data, "leadsTo", String.class);
            tooltip = JSONParser.getObjectOrNull(data, "mouseOverText", String.class);
            status = null;
            script = null;
        }

        void onChosen(SectorEntityToken talkingTo, InteractionDialogAPI dialog)
        {
            if (script != null)
            {
                script.onChosen(talkingTo, dialog);
            }
        }

        String getText()
        {
            return text;
        }

        String getTooltip()
        {
            return tooltip;
        }

        Status getStatus()
        {
            if (status != null)
            {
                return status.getStatus();
            }

            return ResponseStatusScript.Status.ENABLED;
        }

        String getNodeLedTo()
        {
            return leadsTo;
        }
    }
}
