package org.lazywizard.console.commands.personal;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import data.scripts.conv.AnimatedPortraitPlugin;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class TestAnimatedPortrait implements BaseCommand
{
    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        if (context != CommandContext.CAMPAIGN_MAP)
        {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

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
                    Global.getSector().getCampaignUI().showInteractionDialog(
                            new TestAnimatedPortraitInteractionDialogPlugin(), null);
                }
            }
        });

        return CommandResult.SUCCESS;
    }

    private static class TestAnimatedPortraitInteractionDialogPlugin implements InteractionDialogPlugin
    {
        private InteractionDialogAPI dialog;

        private enum OPTIONS
        {
            LEAVE
        }

        @Override
        public void init(InteractionDialogAPI dialog)
        {
            this.dialog = dialog;

            dialog.getVisualPanel().showCustomPanel(400f, 400f,
                    new AnimatedPortraitPlugin("sinistar", "frame_", 19, 5f, .8f));
                    //new AnimatedPortraitPlugin("graphics/sinistar/sinistar_", 19, 5f, .8f));

            dialog.getOptionPanel().addOption("Leave", OPTIONS.LEAVE);
            dialog.setOptionOnEscape("Leave", OPTIONS.LEAVE);
        }

        @Override
        public void optionSelected(String optionText, Object optionData)
        {
            if (optionData == OPTIONS.LEAVE)
            {
                dialog.dismiss();
            }
        }

        @Override
        public void optionMousedOver(String optionText, Object optionData)
        {
        }

        @Override
        public void advance(float amount)
        {
        }

        @Override
        public void backFromEngagement(EngagementResultAPI battleResult)
        {
        }

        @Override
        public Object getContext()
        {
            return null;
        }
    }
}
