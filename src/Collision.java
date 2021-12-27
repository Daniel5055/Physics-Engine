public class Collision
{
    // Attributes
    private RigidBody target1;
    private RigidBody target2;
    private boolean isHorizontal;

    // Constructor
    public Collision(RigidBody target1, RigidBody target2, boolean isHorizontal)
    {
        this.target1 = target1;
        this.target2 = target2;
        this.isHorizontal = isHorizontal;
    }

    // Methods
    public RigidBody getTarget(int i)
    {
        if (i == 0)
        {
            return target1;
        }
        else if (i == 1)
        {
            return target2;
        }
        else
        {
            System.out.println("problem with colliding body getting");
            return null;
        }
    }

    public boolean isHorizontal()
    {
        return isHorizontal;
    }
}
