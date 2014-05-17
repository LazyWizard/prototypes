package org.lazywizard.personal;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin.PickPriority;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAIPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import org.apache.log4j.Level;
import org.lazywizard.localmp.JoypadControls;
import org.lazywizard.playerhq.PlayerHQCampaignPlugin;

public class ModPlugin extends BaseModPlugin
{
    private static final boolean ENABLE_JOYPAD_CONTROLS = false;
    private static final boolean OVERRIDE_ALL_AI = false;

    @Override
    public PluginPick<ShipAIPlugin> pickShipAI(FleetMemberAPI member, ShipAPI ship)
    {
        if (!ENABLE_JOYPAD_CONTROLS)
        {
            return null;
        }

        CombatEngineAPI engine = Global.getCombatEngine();
        if (OVERRIDE_ALL_AI || (engine != null && ship == engine.getPlayerShip()))
        {
            Global.getLogger(ModPlugin.class).log(Level.INFO,
                    "Setting joypad controls to true for "
                    + ship.getHullSpec().getHullName());
            return new PluginPick<ShipAIPlugin>(new JoypadControls(ship),
                    PickPriority.HIGHEST);
        }

        return null;
    }

    @Override
    public void onGameLoad()
    {
        Global.getSector().registerPlugin(new PlayerHQCampaignPlugin());
    }
}
