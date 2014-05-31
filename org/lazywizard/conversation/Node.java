package org.lazywizard.conversation;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Level;
import org.json.JSONArray;
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

        JSONArray rData = data.getJSONArray("responses");
        for (int x = 0; x < rData.length(); x++)
        {
            responses.add(new Response(rData.getJSONObject(x)));
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
        private final String text, tooltip;
        private final ResponseStatusScript status;
        private final String leadsTo;
        private final OnChosenScript script;

        Response(JSONObject data) throws JSONException
        {
            text = data.getString("text");
            leadsTo = data.optString("leadsTo", null);
            tooltip = data.optString("mouseOverText");
            status = null;
            script = null;
        }

        void onChosen(SectorEntityToken talkingTo, InteractionDialogAPI dialog)
        {
            Global.getLogger(Response.class).log(Level.DEBUG,
                    "Chose response: \"" + text + "\"\nLeads to: " + leadsTo);

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
