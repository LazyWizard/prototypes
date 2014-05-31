package org.lazywizard.conversation;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;

public interface ResponseScript
{
    public enum Visibility
    {
        HIDDEN,
        DISABLED,
        VISIBLE
    }

    public Visibility getVisibility();

    public void onChosen(SectorEntityToken talkingTo, InteractionDialogAPI dialog);
}
