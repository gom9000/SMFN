package net.gommagomma.smfn.math.geometry;


public class DiscreteCartesianPlane
{
	private double x_lu, y_lu;
	private double d;
	private int sizeX, sizeY;
	private double[] plane[][];


	public DiscreteCartesianPlane(double x_lu, double y_lu, double x_rd, double y_rd, double d)
	{
		this.d = d;
		this.x_lu = x_lu; this.y_lu = y_lu;
		this.sizeX = Math.abs((int)((x_rd - x_lu) / d))+1;
		this.sizeY = Math.abs((int)((y_rd - y_lu) / d))+1;
		this.plane = new double[sizeX][sizeY][];

		setPlanePoints();
	}


    private void setPlanePoints()
    {
    	for (int xx = 0; xx < sizeX; xx++)
    	{
    		for (int yy = 0; yy < sizeY; yy++)
    		{
    			plane[xx][yy] = new double[]{x_lu + (xx * d), y_lu - (yy * d)};
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
