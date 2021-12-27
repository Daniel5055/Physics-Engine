import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

public class Context extends JPanel
{
    // Attributes
    double tickLength;
    double timeInTick;
    int totalTicks;

    TreeMap<Double, Collision> collisions;


    // Constructor
    public Context(int width, int height)
    {
        collisions = new TreeMap<Double, Collision>();

        timeInTick = 0;
        tickLength = 0.2;
        totalTicks = 0;

        setLayout(null);
        setBounds(0, 0, 800, 800);

        // Set border
    }

    // Methods
    public void tick()
    {
        timeInTick = 0.0;

        Component[] components = getComponents();
        for (Component component : components)
        {
            RigidBody body;
            if (component instanceof RigidBody)
            {
                body = (RigidBody) component;
            }
            else
            {
                continue;
            }

            // Calculating the distance moved (including remainder of distance not included)
            calculateInfluenceRect(body, tickLength);
        }

        // Iterate through influence rects to find collisions
        RigidBody body1;
        RigidBody body2;
        for (int i = 0; i < components.length; i++)
        {
            if (components[i] instanceof RigidBody)
            {
                body1 = (RigidBody) components[i];
            }
            else
            {
                continue;
            }

            for (int j = i + 1; j < components.length; j++)
            {
                if (components[j] instanceof RigidBody)
                {
                    body2 = (RigidBody) components[j];
                }
                else
                {
                    continue;
                }

                if (body1.getInfluence().intersects(body2.getInfluence()))
                {
                    findCollision(body1, body2);
                }
            }
        }

        // Next stage is collision calculations
        if (collisions.size() > 0)
        {
            for (double cTime = collisions.firstKey(); collisions.size() > 0; cTime = collisions.firstKey())
            {
                Collision collision = collisions.get(cTime);

                // Go forward in time to current time within the tick;
                for (Component component : components)
                {
                    RigidBody body;
                    if (component instanceof RigidBody)
                    {
                        body = (RigidBody) component;
                    } else
                    {
                        continue;
                    }

                    body.tick(cTime - timeInTick);
                }

                // "Axis direction"
                int ad = 1;
                if (collision.isHorizontal())
                {
                    ad = 0;
                }

                // Find velocities (only works for elastic collision)
                double v1 = (collision.getTarget(0).getVelocity().get(ad) * (collision.getTarget(0).getMass() -
                        collision.getTarget(1).getMass()) + 2 * collision.getTarget(1).getVelocity().get(ad) *
                        collision.getTarget(1).getMass()) /
                        (collision.getTarget(0).getMass() + collision.getTarget(1).getMass());
                double v2 = v1 + collision.getTarget(0).getVelocity().get(ad) - collision.getTarget(1).getVelocity().get(ad);

                if (ad == 0)
                {
                    collision.getTarget(0).applyInstantForce(new Vector(v1, collision.getTarget(0).getVelocity().getY()));
                    collision.getTarget(1).applyInstantForce(new Vector(v2, collision.getTarget(1).getVelocity().getY()));
                }
                else
                {
                    collision.getTarget(0).applyInstantForce(new Vector(collision.getTarget(0).getVelocity().getX(), v1));
                    collision.getTarget(1).applyInstantForce(new Vector(collision.getTarget(1).getVelocity().getX(), v2));
                }

                // Remove this collision and any collisions relating to the colliding bodies
                ArrayList<Double> removals = new ArrayList<Double>();
                for (Map.Entry<Double, Collision> c : collisions.entrySet())
                {
                    if (c.getValue().getTarget(0) == collision.getTarget(0) ||
                            c.getValue().getTarget(0) == collision.getTarget(1) ||
                            c.getValue().getTarget(1) == collision.getTarget(0) ||
                            c.getValue().getTarget(1) == collision.getTarget(1))
                    {
                        removals.add(c.getKey());
                    }
                }

                for (double key : removals)
                {
                    collisions.remove(key);
                }

                // Recalculate new influence rects of colliding bodies and see if they intersect
                calculateInfluenceRect(collision.getTarget(0), tickLength - cTime);
                calculateInfluenceRect(collision.getTarget(1), tickLength - cTime);

                for (Component component : components)
                {
                    RigidBody body;
                    if (component instanceof RigidBody)
                    {
                        body = (RigidBody) component;
                    } else
                    {
                        continue;
                    }

                    if (body != collision.getTarget(0) && body != collision.getTarget(1))
                    {
                        if (collision.getTarget(0).getInfluence().intersects(body.getInfluence()))
                        {
                            findCollision(collision.getTarget(0), body);
                        }

                        if (collision.getTarget(1).getInfluence().intersects(body.getInfluence()))
                        {
                            findCollision(collision.getTarget(1), body);
                        }
                    }
                }
                if (collisions.size() == 0)
                {
                    break;
                }
            }
        }

        // Finally, move the body the final distances
        for (Component component : components)
        {
            RigidBody body;
            if (component instanceof RigidBody)
            {
                body = (RigidBody) component;
            }
            else
            {
                continue;
            }

            body.tick(tickLength - timeInTick);
        }

        totalTicks++;
    }

    private void calculateInfluenceRect(RigidBody body, double duration)
    {
        // Calculating the distance moved (including remainder of distance not included)
        Vector distanceMoved = new Vector();
        distanceMoved.addX(duration * body.getVelocity().getX() + 0.5 * body.getAcceleration().getX() *
                Math.pow(duration, 2));
        distanceMoved.addY(duration * body.getVelocity().getY() + 0.5 * body.getAcceleration().getY() *
                Math.pow(duration, 2));

        // Creating the influence rect
        Rectangle2D.Double influence = new Rectangle2D.Double(body.getPosition().getX(), body.getPosition().getY(),
                body.getShape().getX() + Math.abs(distanceMoved.getX()),
                body.getShape().getY() + Math.abs(distanceMoved.getY()));

        if (distanceMoved.getX() < 0)
        {
            influence.setRect(influence.getX() + distanceMoved.getX(), influence.getY(),
                    influence.getWidth(), influence.getHeight());
        }

        if (distanceMoved.getY() < 0)
        {
            influence.setRect(influence.getX(), influence.getY() + distanceMoved.getY(),
                    influence.getWidth(), influence.getHeight());
        }

        body.setInfluence(influence);
    }

    private void findCollision(RigidBody body1, RigidBody body2)
    {
        double time = doesCollide(body1, body2, 0);
        if (time >= 0)
        {
            Collision collision = new Collision(body1, body2, true);
            collisions.put(time, collision);
        }
        else
        {
            time = doesCollide(body1, body2, 1);
            if (time >= 0)
            {
                Collision collision = new Collision(body1, body2, false);
                collisions.put(time, collision);
            }
        }
    }

    private double doesCollide(RigidBody body1, RigidBody body2, int ad)
    {
        // First find distance
        double distance = body1.getPosition().get(ad) - (body2.getPosition().get(ad) + body2.getShape().get(ad));
        if (distance < 0)
        {
            distance *= -1;
            distance -= body1.getShape().get(ad) + body2.getShape().get(ad);
        }

        // Calculate new velocity to include remainder distance;
        double time = -1;
        double a = body1.getAcceleration().get(ad) + body2.getAcceleration().get(ad);
        double b = body1.getVelocity().get(ad) + body2.getVelocity().get(ad);
        double c = distance;

        // Find time (that is earliest and reasonable)
        if (a != 0)
        {
            double t1, t2;

            t1 = (-b + Math.sqrt(b * b + 2 * a * c)) / a;
            t2 = (-b - Math.sqrt(b * b + 2 * a * c)) / a;

            if (t1 <= t2)
            {
                if (t1 >= 0 && t1 <= tickLength)
                {
                    time = t1;
                }
                else
                {
                    return -1;
                }
            }
            if (t2 < t1)
            {
                if (t2  >= 0 && t2 <= tickLength)
                {
                    time = t2;
                }
                else
                {
                    return -1;
                }
            }
        }
        else
        {
            time = c / Math.abs(body1.getVelocity().get(ad) - body2.getVelocity().get(ad));
            if (time < 0 || time > tickLength)
            {
                return -1;
            }
        }

        // Check if other axis matches

        // Get y distance travelled at time
        double diff1 = time * body1.getVelocity().get(1 - ad) + 0.5 * body1.getAcceleration().get(1 - ad)
                * Math.pow(time, 2);
        double diff2 = time * body2.getVelocity().get(1 - ad) + 0.5 * body2.getAcceleration().get(1 - ad)
                * Math.pow(time, 2);

        double low1 = 0;
        double low2 = 0;
        double high1 = 0;
        double high2 = 0;

        if (diff1 > 0)
        {
            high1 = diff1;
        }
        else
        {
            low1 = diff1;
        }

        if (diff2 > 0)
        {
            high2 = diff2;
        }
        else
        {
            low2 = diff2;
        }

        // If making contact
        if (low1 + body1.getPosition().get(1 - ad) <= high2 + body2.getPosition().get(1 - ad) +
                body2.getShape().get(1 - ad) + 1 &&
                high1 + body1.getPosition().get(1 - ad) + body1.getShape().get(1 - ad) + 1 >=
                        low2 + body2.getPosition().get(1 - ad))
        {
            return time;
        }

        return -1;
    }
}
