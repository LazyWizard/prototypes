package org.lazywizard.playerhq;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import java.util.LinkedHashMap;
import java.util.Map;

// TODO: Module system so new features can be added simply
// TODO: Integrate Omnifactory as a module
// TODO: Simulation battles w/ whole player fleet vs customized enemy fleet
public class PlayerHQMaster
{
    private static final String PERSISTENT_DATA_ID = "lw_playerhq";

    public static Map<String, Object> getDataMap()
    {
        Map<String, Object> persistentData, dataMap;
        persistentData = Global.getSector().getPersistentData();
        if (!persistentData.containsKey(PERSISTENT_DATA_ID))
        {
            dataMap = new LinkedHashMap<>();
            persistentData.put(PERSISTENT_DATA_ID, dataMap);
        }
        else
        {
            dataMap = (Map<String, Object>) persistentData.get(PERSISTENT_DATA_ID);
        }

        return dataMap;
    }

    public static boolean isHeadquarters(SectorEntityToken token)
    {
        return false;
    }

    static void checkForNewSimOpponents(SectorEntityToken opponent)
    {
        // TODO: Keep track of all opponents faced for custom 'simulation' battles
    }

    static InteractionDialogPlugin createDialog(SectorEntityToken interactionTarget)
    {
        // TODO
        return null;
    }
}
