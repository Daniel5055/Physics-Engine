public class Vector
{
    // Attributes
    private double x;
    private double y;

    // Constructors
    public Vector()
    {

    }

    public Vector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    // Copy Constructor
    public Vector(Vector vector)
    {
        this.x = vector.x;
        this.y = vector.y;
    }

    // Accessors
    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public void set(int id, double value)
    {
        if (id == 0)
        {
            x = value;
        }
        else if (id == 1)
        {
            y = value;
        }
        else
        {
            System.out.println("problem in Vector");
        }
    }

    public double get(int id)
    {
        if (id == 0)
        {
            return x;
        }
        else if (id == 1)
        {
            return y;
        }
        else
        {
            System.out.println("Wrong id given in vector class");
            return 0;
        }
    }

    // Methods
    public void addX(double value)
    {
        this.x += value;
    }

    public void addY(double value)
    {
        this.y += value;
    }

    public void add(double xValue, double yValue)
    {
        this.x += xValue;
        this.y += yValue;
    }

    public void add(Vector vector)
    {
        add(vector.x, vector.y);
    }

}
