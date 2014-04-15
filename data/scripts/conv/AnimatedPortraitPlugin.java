package data.scripts.conv;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.PositionAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class AnimatedPortraitPlugin implements CustomUIPanelPlugin
{
    // Controls how much of the host visual panel this sprite takes up
    private final float SPRITE_SIZE_MOD, SPRITE_SIZE_BUFFER;
    // Controls frames per second
    private final float TIME_BETWEEN_FRAMES;
    // The SpriteAPIs that make up each frame of animation
    private final List sprites = new ArrayList();
    private int curFrame = 0;
    private float timeSinceLastFrame = 0f;
    private PositionAPI position;

    /**
     * Creates an animated portrait. You must have all of your frames registered
     * in their own mapping within the {@code "graphics"} section of
     * {@code settings.json}, otherwise you'll only see a black box for each
     * missing frame.
     * <p>
     * @param spriteCategory  The 'key' in the {@code "graphics"} section of
     *                        {@code settings.json} for your sprite.
     * @param spritePrefix    The 'value' of your sprite's mapping in the
     *                        {@code "graphics"} section of {@code settings.json},
     *                        minus the frame number. So if your naming convention
     *                        was {@code "frame_00"}, {@code "frame_01"} etc, you'd
     *                        enter {@code "frame_"} for this argument.
     * @param numFrames       How many frames total your animation is.
     *                        Frames start at 00, so this will be your
     *                        last frame's number + 1. This plugin currently
     *                        doesn't support more than 100 frames.
     * @param framesPerSecond How many frames per second the animation
     *                        should play at.
     * @param spriteSizeMod   How much of the visual panel should be taken up by
     *                        the sprite. So {@code .8f} would mean the sprite
     *                        takes up 80% of the visual panel, and thus has a
     *                        10% buffer of empty space on each side.
     */
    public AnimatedPortraitPlugin(String spriteCategory, String spritePrefix,
            int numFrames, float framesPerSecond, float spriteSizeMod)
    {
        // Internally frames per second isn't used; instead we keep track of
        // the time until we need to switch to the next frame
        TIME_BETWEEN_FRAMES = (1f / framesPerSecond);

        // Load all of the animation's sprites in advance
        // This will force an exception for missing graphics immediately
        // instead of only when that frame is reached
        for (int x = 0; x < numFrames; x++)
        {
            sprites.add(Global.getSettings().getSprite(spriteCategory,
                    spritePrefix + (x < 10 ? "0" : "") + x));
        }

        // Controls how much of the visual panel the sprite takes up
        SPRITE_SIZE_MOD = spriteSizeMod;
        SPRITE_SIZE_BUFFER = (1f - SPRITE_SIZE_MOD) / 2f;
    }

    /**
     * Creates an animated portrait without requiring a custom definition in
     * settings.json. The files still must be loaded by the game beforehand for
     * this to work, therefore this constructor is only useful for sprites that
     * aren't included in settings.json (such as animated weapons).
     *
     * @param spritePathUpToFrameNumber The path to your sprite without the
     *                                  frame number. So if your first sprite is
     *                                  {@code "graphics/example/example_00.png"},
     *                                  you'd pass in
     *                                  {@code "graphics/example/example_"}.
     * @param numFrames                 How many frames total your animation is.
     *                                  Frames start at 00, so this will be your
     *                                  last frame's number + 1. This plugin
     *                                  currently doesn't support more than
     *                                  100 frames.
     * @param framesPerSecond           How many frames per second the animation
     *                                  should play at.
     * @param spriteSizeMod             How much of the visual panel should be
     *                                  taken up by the sprite. So {@code .8f}
     *                                  would mean the sprite takes up 80% of
     *                                  the visual panel, and thus has a 10%
     *                                  buffer of empty space on each side.
     */
    public AnimatedPortraitPlugin(String spritePathUpToFrameNumber,
            int numFrames, float framesPerSecond, float spriteSizeMod)
    {
        // Internally, frames per second isn't used; instead we keep track of
        // the time until we need to switch to the next frame
        TIME_BETWEEN_FRAMES = (1f / framesPerSecond);

        // Load all of the animation's sprites in advance
        // This will force an exception for missing graphics immediately
        // instead of only when that frame is reached
        for (int x = 0; x < numFrames; x++)
        {
            sprites.add(Global.getSettings().getSprite(spritePathUpToFrameNumber
                    + (x < 10 ? "0" : "") + x + ".png"));
        }

        // Controls how much of the visual panel the sprite takes up
        SPRITE_SIZE_MOD = spriteSizeMod;
        SPRITE_SIZE_BUFFER = (1f - SPRITE_SIZE_MOD) / 2f;
    }

    public int getCurrentFrame()
    {
        return curFrame;
    }

    public void setCurrentFrame(int frame)
    {
        curFrame = Math.max(0, Math.min(frame, sprites.size() - 1));
    }

    public float getFramesPerSecond()
    {
        return 1f * TIME_BETWEEN_FRAMES;
    }

    public void setFramesPerSecond(float framesPerSecond)
    {
    }

    @Override
    public void positionChanged(PositionAPI position)
    {
        this.position = position;

        // Resize sprites to match visual panel size
        // Draw the sprite slightly smaller than the box it's in
        float sizeX = position.getWidth() * SPRITE_SIZE_MOD,
                sizeY = position.getHeight() * SPRITE_SIZE_MOD;
        for (Iterator iter = sprites.iterator(); iter.hasNext();)
        {
            SpriteAPI sprite = (SpriteAPI) iter.next();
            sprite.setSize(sizeX, sizeY);
        }
    }

    @Override
    public void render(float alphaMult)
    {
        if (position == null)
        {
            return;
        }

        // Set OpenGL flags before drawing
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Draw a background for the sprite
        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(position.getX(),
                    position.getY());
            GL11.glVertex2f(position.getX(),
                    position.getY() + position.getHeight());
            GL11.glVertex2f(position.getX() + position.getWidth(),
                    position.getY() + position.getHeight());
            GL11.glVertex2f(position.getX() + position.getWidth(),
                    position.getY());
        }
        GL11.glEnd();
        GL11.glColor4f(.7f, .7f, .7f, alphaMult);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        {
            GL11.glVertex2f(position.getX(),
                    position.getY());
            GL11.glVertex2f(position.getX(),
                    position.getY() + position.getHeight());
            GL11.glVertex2f(position.getX() + position.getWidth(),
                    position.getY() + position.getHeight());
            GL11.glVertex2f(position.getX() + position.getWidth(),
                    position.getY());
        }
        GL11.glEnd();

        // Draw the actual sprite
        SpriteAPI sprite = (SpriteAPI) sprites.get(curFrame);
        sprite.setAlphaMult(alphaMult);
        sprite.render(position.getX() + (position.getWidth() * SPRITE_SIZE_BUFFER),
                position.getY() + (position.getHeight() * SPRITE_SIZE_BUFFER));
    }

    @Override
    public void advance(float amount)
    {
        // Update current frame based on time passed
        // Frames can be skipped if the game's framerate drops below our animation speed
        timeSinceLastFrame += amount;
        while (timeSinceLastFrame >= TIME_BETWEEN_FRAMES)
        {
            timeSinceLastFrame -= TIME_BETWEEN_FRAMES;

            curFrame++;
            if (curFrame >= sprites.size())
            {
                curFrame = 0;
            }
        }
    }

    @Override
    public void processInput(List events)
    {
    }
}
