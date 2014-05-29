package org.lazywizard.conversation;

import com.fs.starfarer.api.campaign.SectorEntityToken;

public interface ResponseData
{
    public enum Status
    {
        HIDDEN,
        DISABLED,
        ENABLED
    }

    public Status getStatus();
    public void onChosen(SectorEntityToken talkingTo);
}
