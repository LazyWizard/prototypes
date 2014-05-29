package org.lazywizard.conversation;

import com.fs.starfarer.api.campaign.SectorEntityToken;

public interface ResponseStatusScript
{
    public enum Status
    {
        HIDDEN,
        DISABLED,
        ENABLED
    }

    public Status getStatus();
}
