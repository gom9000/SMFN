package net.gommagomma.smfn.graphics.plotting;

import net.gommagomma.smfn.graphics.core.CoordinateMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.math.algebra.numeric.Complex;

public class LinearComplexCoordinateMapper
implements CoordinateMapper<Complex>
{
    @Override
    public Complex mapPixelToElement(int x, int y, Viewport<Complex> viewport)
    {
        double rangeX = viewport.getMaxX() - viewport.getMinX();
        double rangeY = viewport.getMaxY() - viewport.getMinY();
        
        // Calcolo della parte reale
        double realPart = viewport.getMinX() + ((double) x / viewport.getPixelWidth()) * rangeX;
        
        // Calcolo della parte immaginaria (asse Y invertito per lo schermo)
        double imaginaryPart = viewport.getMaxY() - ((double) y / viewport.getPixelHeight()) * rangeY; 
        
        return new Complex(realPart, imaginaryPart);
    }
}
