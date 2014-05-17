package org.lazywizard.playerhq;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;
import com.fs.starfarer.api.campaign.BattleAutoresolverPlugin;
import com.fs.starfarer.api.campaign.BattleCreationPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OrbitalStationAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;

public class PlayerHQCampaignPlugin extends BaseCampaignPlugin
{
    @Override
    public String getId()
    {
        return "lw_playerhq";
    }

    @Override
    public boolean isTransient()
    {
        return true;
    }

    @Override
    public PluginPick<InteractionDialogPlugin> pickInteractionDialogPlugin(SectorEntityToken interactionTarget)
    {
        if (interactionTarget instanceof OrbitalStationAPI
                && PlayerHQMaster.isHeadquarters(interactionTarget))
        {
            return new PluginPick<InteractionDialogPlugin>(PlayerHQMaster.createDialog(interactionTarget),
                    PickPriority.MOD_SPECIFIC);
        }

        return null;
    }

    @Override
    public PluginPick<BattleCreationPlugin> pickBattleCreationPlugin(SectorEntityToken opponent)
    {
        PlayerHQMaster.checkForNewSimOpponents(opponent);
        return null;
    }

    @Override
    public PluginPick<BattleAutoresolverPlugin> pickBattleAutoresolverPlugin(SectorEntityToken one, SectorEntityToken two)
    {
        return null;
    }
}
