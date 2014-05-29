package org.lazywizard.conversation;

import org.json.JSONException;
import org.json.JSONObject;

class JSONParser
{
    static <T> T getObjectOrNull(JSONObject data, String key, Class<T> type)
    {
        try
        {
            return type.cast(data.get(key));
        }
        catch (JSONException | ClassCastException ex)
        {
            return null;
        }
    }

    private JSONParser()
    {
    }
}
