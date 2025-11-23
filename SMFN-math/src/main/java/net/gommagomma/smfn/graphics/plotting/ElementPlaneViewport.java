package net.gommagomma.smfn.graphics.plotting;

import net.gommagomma.smfn.graphics.core.CoordinateMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

// Classe generica per qualsiasi piano di elementi algebrici
public class ElementPlaneViewport<E extends AlgebraicElement<E>>
implements Viewport<E>
{
    private int pixelWidth, pixelHeight;
    private double minX, maxX, minY, maxY;
    private final CoordinateMapper<E> coordinateMapper;

    public ElementPlaneViewport(int w, int h, double minX, double maxX, 
                                double minY, double maxY, 
                                CoordinateMapper<E> mapper) {
        this.pixelWidth = w; this.pixelHeight = h;
        this.minX = minX; this.maxX = maxX; 
        this.minY = minY; this.maxY = maxY;
        this.coordinateMapper = mapper;

        setMathematicalArea(minX, maxX, minY, maxY);
    }

    // --- Implementazione dell'interfaccia Viewport
    
    @Override
    public int getPixelWidth() { return pixelWidth; }

    @Override
    public int getPixelHeight() { return pixelHeight; }

    @Override
    public double getMinX() { return minX; }

    @Override
    public double getMaxX() { return maxX; }

    @Override
    public double getMinY() { return minY; }

    @Override
    public double getMaxY() { return maxY; }
    
    @Override
    public E mapPixelToElement(int x, int y) {
        return coordinateMapper.mapPixelToElement(x, y, this);
    }

    @Override
    public void setMathematicalArea(double desiredMinX, double desiredMaxX, double desiredMinY, double desiredMaxY) {
        // Calcola l'aspect ratio desiderato e l'aspect ratio della finestra in pixel
        double desiredRatio = (desiredMaxX - desiredMinX) / (desiredMaxY - desiredMinY);
        double screenRatio = (double) pixelWidth / pixelHeight;

        // Calcola i range
        double desiredRangeX = desiredMaxX - desiredMinX;
        double desiredRangeY = desiredMaxY - desiredMinY;

        double actualMinX = desiredMinX;
        double actualMaxX = desiredMaxX;
        double actualMinY = desiredMinY;
        double actualMaxY = desiredMaxY;

        // Aggiusta l'asse che non combacia
        if (desiredRatio > screenRatio) {
            // L'area desiderata è "più larga" della finestra, dobbiamo estendere la Y (aggiungere spazio verticale)
            double newRangeY = desiredRangeX / screenRatio;
            double yCenter = (desiredMinY + desiredMaxY) / 2.0;
            actualMinY = yCenter - newRangeY / 2.0;
            actualMaxY = yCenter + newRangeY / 2.0;
            // X rimane invariata
        } else if (desiredRatio < screenRatio) {
            // L'area desiderata è "più alta" della finestra, dobbiamo estendere la X (aggiungere spazio orizzontale)
            double newRangeX = desiredRangeY * screenRatio;
            double xCenter = (desiredMinX + desiredMaxX) / 2.0;
            actualMinX = xCenter - newRangeX / 2.0;
            actualMaxX = xCenter + newRangeX / 2.0;
            // Y rimane invariata
        }
        
        // Imposta i valori effettivi e corretti
        this.minX = actualMinX;
        this.maxX = actualMaxX;
        this.minY = actualMinY;
        this.maxY = actualMaxY;
    }
}
