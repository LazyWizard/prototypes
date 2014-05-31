package org.lazywizard.conversation;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lazywizard.conversation.ResponseScript.Visibility;

// TODO: Move all JSON parsing into single method for easier debugging
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
        private final String leadsTo;
        private final ResponseScript script;

        Response(JSONObject data) throws JSONException
        {
            text = data.getString("text");
            leadsTo = data.optString("leadsTo", null);
            tooltip = data.optString("tooltip", null);

            String scriptPath = data.optString("responseScript", null);
            if (scriptPath == null)
            {
                script = null;
            }
            else
            {
                ResponseScript tmp = null;

                try
                {
                    tmp = (ResponseScript) Global.getSettings()
                            .getScriptClassLoader().loadClass(scriptPath).newInstance();
                }
                catch (ClassNotFoundException ex)
                {
                    Global.getLogger(Response.class).log(Level.ERROR,
                            "ResponseScript not found: " + scriptPath, ex);
                }
                catch (InstantiationException | IllegalAccessException ex)
                {
                    Global.getLogger(Response.class).log(Level.ERROR,
                            "Failed to create ResponseScript: " + scriptPath, ex);
                }

                script = tmp;
            }
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

        Visibility getVisibility()
        {
            if (script != null)
            {
                return script.getVisibility();
            }

            return ResponseScript.Visibility.VISIBLE;
        }

        String getNodeLedTo()
        {
            return leadsTo;
        }
    }
}
