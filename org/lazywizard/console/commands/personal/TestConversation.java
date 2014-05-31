package org.lazywizard.console.commands.personal;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import java.io.IOException;
import org.json.JSONException;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;
import org.lazywizard.conversation.ConversationMaster;

public class TestConversation implements BaseCommand
{
    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        if (context != CommandContext.CAMPAIGN_MAP)
        {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        if ("reload".equalsIgnoreCase(args))
        {
            try
            {
                ConversationMaster.reloadConversations();
            }
            catch (IOException | JSONException ex)
            {
                Console.showException("Failed to reload conversations:", ex);
                return CommandResult.ERROR;
            }

            Console.showMessage("Reloaded conversations.");
            return CommandResult.SUCCESS;
        }

        final String conv = (args.isEmpty() ? "testConv" : args);
        if (!ConversationMaster.hasConversation(conv))
        {
            Console.showMessage("No conversation with ID \"" + conv + "\" loaded!");
            return CommandResult.ERROR;
        }

        Console.showMessage("Showing conversation " + conv + "...");
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
                    ConversationMaster.showConversation(conv,
                            Global.getSector().getPlayerFleet());
                }
            }
        });
        return CommandResult.SUCCESS;
    }
}
