import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Frame extends JFrame
{
    Context context;
    public Frame()
    {
        context = new Context(getWidth(), getHeight());
        RigidBody body = new RigidBody(50, 20, 50, 50, 14, 18, false, Color.red);
        RigidBody body1 = new RigidBody(400, 400, 50, 50, -16, -12, false, Color.blue);
        RigidBody body2 = new RigidBody(400, 50, 50, 50, -8, 2, false, Color.green);

        RigidBody border1 = new RigidBody(10, 510, 500, 10, 0, 0, true);
        RigidBody border2 = new RigidBody(510, 10, 10, 500, 0, 0, true);
        RigidBody border3 = new RigidBody(0, 10, 10, 500, 0, 0, true);
        RigidBody border4 = new RigidBody(10, 0, 500, 10, 0, 0, true);
        context.add(body);
        context.add(body1);
        context.add(body2);

        context.add(border1);
        context.add(border2);
        context.add(border3);
        context.add(border4);

        add(context);
        setSize(800, 800);
        setLayout(null);
        setVisible(true);

        Timer timer = new Timer(0, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                context.tick();
                validate();
                repaint();
            }
        });

        timer.start();
    }
}
