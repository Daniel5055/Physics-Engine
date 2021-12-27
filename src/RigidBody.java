import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RigidBody extends JPanel
{
    // Attributes
    private BufferedImage image;

    private Rectangle2D.Double influence;

    // These exist so I can call their values by index
    private Vector position;
    private Vector shape;

    private Vector velocity;
    private Vector acceleration;
    private int mass;

    private boolean isStatic;

    public RigidBody(int x, int y, int w, int h, double xVelocity, double yVelocity, boolean isStatic)
    {
        influence = null;
        position = new Vector(x, y);
        shape = new Vector(w, h);
        velocity = new Vector(xVelocity, yVelocity);
        acceleration = new Vector();

        if (isStatic)
        {
            mass = 10000000;
        }
        else
        {
            mass = 1;
        }

        this.isStatic = isStatic;

        try
        {
            image = ImageIO.read(new File("Box.png"));
        }
        catch (IOException e)
        {
            System.out.println("failed");
        }

        setLayout(null);
        setBackground(Color.black);
        setBounds(x, y, w, h);
    }

    // Accessors
    public Vector getPosition()
    {
        return position;
    }

    public Vector getShape()
    {
        return shape;
    }

    public Vector getVelocity()
    {
        return velocity;
    }

    public Vector getAcceleration()
    {
        return acceleration;
    }

    public Rectangle2D.Double getInfluence()
    {
        return influence;
    }

    public double getMass()
    {
        return mass;
    }

    public void setInfluence(Rectangle2D.Double rect)
    {
        this.influence = rect;
    }

    public boolean isStatic()
    {
        return isStatic;
    }

    public void setIsStatic(boolean value)
    {
        isStatic = value;
    }

    // Methods
    public void applyForce(Vector force)
    {
        for (int i : new int[]{0, 1})
        {
            acceleration.set(i, force.get(i) /  mass);
        }
    }

    public void applyInstantForce(Vector velocity)
    {
        this.velocity = new Vector(velocity);
    }

    public void tick(double time)
    {
        position.addX(velocity.getX() * time + 0.5 * acceleration.getX() * Math.pow(time, 2));
        position.addY(velocity.getY() * time + 0.5 * acceleration.getY() * Math.pow(time, 2));

        velocity.add(acceleration.getX() * time, acceleration.getY() * time);
        setLocation((int) position.getX(), (int) position.getY());
    }
}
