package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class EllipticalOrbit implements OrbitAPI
{
    private final SectorEntityToken focus;
    private final float orbitAngle, orbitWidth, orbitHeight, orbitSpeed;
    private SectorEntityToken entity;
    private float currentAngle;

    public EllipticalOrbit(SectorEntityToken focus, float startAngle,
            float orbitWidth, float orbitHeight, float orbitAngle, float daysPerOrbit)
    {
        this.focus = focus;
        this.orbitWidth = orbitWidth;
        this.orbitHeight = orbitHeight;
        this.orbitAngle = (float) Math.toRadians(orbitAngle);
        this.orbitSpeed = 360f / daysPerOrbit;
        currentAngle = startAngle;
    }

    @Override
    public SectorEntityToken getFocus()
    {
        return focus;
    }

    public void setAngle(float angle)
    {
        currentAngle = angle;
        angle = (float) Math.toRadians(angle);
        float sin = (float) FastTrig.sin(angle),
                cos = (float) FastTrig.cos(angle);

        // Get point on unrotated ellipse around origin (0, 0)
        final float x = orbitWidth * cos;
        final float y = orbitHeight * sin;

        // Rotate to match actual rotated elliptical path
        sin = (float) FastTrig.sin(orbitAngle);
        cos = (float) FastTrig.cos(orbitAngle);
        Vector2f newLoc = new Vector2f((x * cos) - (y * sin),
                (x * sin) + (y * cos));

        // Translate from origin to final location
        Vector2f.add(newLoc, focus.getLocation(), newLoc);
        entity.getLocation().set(newLoc);
    }

    @Override
    public void advance(float amount)
    {
        if (entity == null)
        {
            return;
        }

        // Advance rotation
        setAngle(MathUtils.clampAngle(currentAngle + (orbitSpeed
                * Global.getSector().getClock().convertToDays(amount))));
    }

    @Override
    public void setEntity(SectorEntityToken entity)
    {
        this.entity = entity;
        setAngle(currentAngle);
    }
}
