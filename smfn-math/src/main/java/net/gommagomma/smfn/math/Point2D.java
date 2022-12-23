package net.gommagomma.smfn.math;

/**
 * Represents a 2D Point.
 * @author gommagomma.net
 */
public class Point2D
{
    private Measure x, y;


	public Point2D(double x, double y)
	{
		this(x, y, OrderOfMagnitude.UNITY);
	}


	public Point2D(double x, double y, OrderOfMagnitude order)
    {
    	this.x = new Measure(x, order);
    	this.y = new Measure(y, order);
    }


    public Measure getXMeasure()
    {
    	return x;
    }


    public Measure getYMeasure()
    {
    	return y;
    }


    public double getMagnitudeX()
    {
    	return x.getMagnitude();
    }


    public double getMagnitudeY()
    {
    	return y.getMagnitude();
    }

	/**
	 * Return the x-coord of the point.
     * @return x-coord of the point
     */
    public double getX()
    {
        return x.getValue();
    }


	/**
	 * Return the y-coord of the point.
     * @return y-coord of the point
     */
    public double getY()
    {
        return y.getValue();
    }


    public OrderOfMagnitude getOrder()
    {
    	return x.getOrder();
    }


    /**
     * Compute the distance between this point and the given one.
     * @param p
     * @return the distance between this point and the given one
     */
    public double distance(Point2D p) { return 0; }
}
