package org.lazywizard.conversation;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConversationMaster
{
    // TODO: Split into own mod and change mod id
    private static final String MOD_ID = "lw_personal";
    private static final String CSV_PATH = "data/conv/conversations.csv";
    private static final Map<String, Conversation> conversations = new HashMap<>();

    // Only throws Exceptions if CSV is malformed, not on errors in individual JSON files
    public static void reloadConversations() throws IOException, JSONException
    {
        conversations.clear();
        JSONArray csv = Global.getSettings().getMergedSpreadsheetDataForMod(
                "id", CSV_PATH, MOD_ID);
        for (int x = 0; x < csv.length(); x++)
        {
            JSONObject row = csv.getJSONObject(x);

            try
            {
                conversations.put(row.getString("id"),
                        new Conversation(row.getString("filePath")));
            }
            catch (IOException ex)
            {
                Global.getLogger(ConversationMaster.class).log(Level.ERROR,
                        "Unable to find covnersation " + row.toString(), ex);
            }
            catch (JSONException ex)
            {
                Global.getLogger(ConversationMaster.class).log(Level.ERROR,
                        "Unable to create conversation " + row.toString(), ex);
            }
        }
    }

    public static List<String> getLoadedConversations()
    {
        return new ArrayList<>(conversations.keySet());
    }

    public static boolean hasConversation(String id)
    {
        return conversations.containsKey(id);
    }

    public static void showConversation(String id, SectorEntityToken talkingTo)
    {
        if (!conversations.containsKey(id))
        {
            throw new RuntimeException("Conversation \"" + id + "\" not found!");
        }

        Global.getSector().getCampaignUI().showInteractionDialog(
                new ConversationDialogPlugin(conversations.get(id), talkingTo), talkingTo);
    }

    private ConversationMaster()
    {
    }
}
