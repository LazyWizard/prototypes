package org.lazywizard.console.commands.personal;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.Console;
import org.lazywizard.conversation.Conversations;

public class TestConversation implements BaseCommand
{
    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        Console.showMessage("Showing conversation...");
        Global.getSector().addScript(new EveryFrameScript()
        {
            private boolean isDone = false;

            @Override
            public boolean isDone()
            {
                return isDone;
            }

            @Override
            public boolean runWhilePaused()
            {
                return false;
            }

            @Override
            public void advance(float amount)
            {
                if (!isDone)
                {
                    isDone = true;
                    Conversations.showConversation("testConv", Global.getSector().getPlayerFleet());
                }
            }
        });
        return CommandResult.SUCCESS;
    }
}
