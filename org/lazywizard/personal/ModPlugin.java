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
import org.lazywizard.localmp.JoypadController;

public class ModPlugin extends BaseModPlugin
{
    @Override
    public PluginPick<ShipAIPlugin> pickShipAI(FleetMemberAPI member, ShipAPI ship)
    {
        CombatEngineAPI engine = Global.getCombatEngine();
        if ((member != null && member.isFlagship())
                || (engine != null && ship == engine.getPlayerShip()))
        {
            Global.getLogger(ModPlugin.class).log(Level.INFO,
                    "Setting joypad controls to true for "
                    + ship.getHullSpec().getHullName());
            return new PluginPick<ShipAIPlugin>(new JoypadController(ship),
                    PickPriority.MOD_SET);
        }

        return null;
    }
}
