package net.gommagomma.smfn.math.geometry;


public class DiscreteCartesianPlane
{
	private double leftUpX, leftUpY;
	private double d;
	private int sizeX, sizeY;
	private double[] plane[][];


	public DiscreteCartesianPlane(double leftUpX, double leftUpY, double rightDownX, double rightDownY, double d)
	{
		this.d = d;
		this.leftUpX = leftUpX; this.leftUpY = leftUpY;
		this.sizeX = Math.abs((int)((rightDownX - leftUpX) / d))+1;
		this.sizeY = Math.abs((int)((rightDownY - leftUpY) / d))+1;
		this.plane = new double[sizeX][sizeY][];

		setPlanePoints();
	}


    private void setPlanePoints()
    {
    	for (int xx = 0; xx < sizeX; xx++)
    	{
    		for (int yy = 0; yy < sizeY; yy++)
    		{
    			plane[xx][yy] = new double[]{leftUpX + (xx * d), leftUpY - (yy * d)};
    		}
    	}
    }


	public double getPointX(int x, int y)
	{
		return plane[x][y][0];
	}

	
	public double getPointY(int x, int y)
	{
		return plane[x][y][1];
	}


	public int getSizeX()
	{
		return sizeX;
	}


	public int getSizeY()
	{
		return sizeY;
	}
}
