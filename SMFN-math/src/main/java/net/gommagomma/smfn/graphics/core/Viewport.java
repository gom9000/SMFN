package net.gommagomma.smfn.graphics.core;


/**
 * Gestisce la trasformazione tra coordinate del mondo matematico e coordinate pixel dello schermo,
 * mantenendo il corretto aspect ratio matematico.
 */
public final class Viewport
{
    public final double minX;
    public final double maxX;
    public final double minY;
    public final double maxY;
    public final double rangeX;
    public final double rangeY;
    public final int pixelWidth;
    public final int pixelHeight;
    private final double scale;
    private final double offsetX;
    private final double offsetY;


    public Viewport(double minX, double maxX, double minY, double maxY, int pixelWidth, int pixelHeight)
    {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.rangeX = maxX - minX;
        this.rangeY = maxY - minY;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;

        // Calcola l'aspect ratio matematico e quello in pixel
        double mathAspectRatio = (maxX - minX) / (maxY - minY);
        double pixelAspectRatio = (double) pixelWidth / pixelHeight;

        // Calcola il fattore di scala uniforme e l'offset per centrare il contenuto
        if (mathAspectRatio > pixelAspectRatio) {
            // La larghezza matematica è dominante, scala in base alla larghezza pixel
            this.scale = pixelWidth / (maxX - minX);
            this.offsetY = (pixelHeight - (maxY - minY) * this.scale) / 2.0;
            this.offsetX = 0;
        } else {
            // L'altezza matematica è dominante, scala in base all'altezza pixel
            this.scale = pixelHeight / (maxY - minY);
            this.offsetX = (pixelWidth - (maxX - minX) * this.scale) / 2.0;
            this.offsetY = 0;
        }
    }


    // Metodi di conversione Math -> Pixel
    public int convertMathXToPixelX(double mathX)
    {
        return (int) ((mathX - minX) * scale + offsetX);
    }
    public int convertMathYToPixelY(double mathY)
    {
        // La Y va invertita per la convenzione grafica (Y=0 in alto)
        int yPixelFromMath = (int) ((mathY - minY) * scale);
        return pixelHeight - yPixelFromMath - (int)offsetY;
    }


    // Metodi di conversione Pixel -> Math
    public double convertPixelXToMathX(int pixelX)
    {
        return (pixelX - offsetX) / scale + minX;
    }
    public double convertPixelYToMathY(int pixelY)
    {
    	// La Y va invertita per la convenzione grafica (Y=0 in alto)
        int standardY = pixelHeight - pixelY; 
        return (standardY - offsetY) / scale + minY;
    }
}
