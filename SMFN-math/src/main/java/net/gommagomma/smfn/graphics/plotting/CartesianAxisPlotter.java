package net.gommagomma.smfn.graphics.plotting;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.Viewport;

/**
 * Utility per plottare gli assi cartesiani su un Renderer1D.
 */
public final class CartesianAxisPlotter
{	
	private static final DecimalFormat DF = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));
	private CartesianAxisPlotter() {}


    public static void plotAxes(Renderer renderer, Viewport viewport, Color color, boolean showTicksAndLabels)
    {
    	renderer.setColor(color);

    	int xZeroPixel = viewport.convertMathXToPixelX(0.0);
        int yZeroPixel = viewport.convertMathYToPixelY(0.0);

        if (yZeroPixel >= 0 && yZeroPixel <= renderer.getHeight()) {
        	renderer.drawLine(0, yZeroPixel, renderer.getWidth(), yZeroPixel); // Asse X
        }
        if (xZeroPixel >= 0 && xZeroPixel <= renderer.getWidth()) {
        	renderer.drawLine(xZeroPixel, 0, xZeroPixel, renderer.getHeight()); // Asse Y
        }

        if (showTicksAndLabels) drawTicksAndLabels(renderer, viewport, xZeroPixel, yZeroPixel);
    }


    private static void drawTicksAndLabels(Renderer renderer, Viewport viewport, int xZeroPixel, int yZeroPixel) {
        double rangeX = viewport.maxX - viewport.minX;
        double rangeY = viewport.maxY - viewport.minY;
        double tickSpacingX = calculateNiceTickSpacing(rangeX, renderer.getWidth());
        double tickSpacingY = calculateNiceTickSpacing(rangeY, renderer.getHeight());

        int tickSize = 5;

        // --- Logica Asse X ---
        double startX = Math.ceil(viewport.minX / tickSpacingX) * tickSpacingX;

        // Usiamo un piccolo epsilon per gestire gli errori di virgola mobile nella condizione <=
        for (double x = startX; x <= viewport.maxX + (tickSpacingX / 2.0); x += tickSpacingX) {
        	if (Math.abs(x) < 1e-9) continue; 
            int pixelX = viewport.convertMathXToPixelX(x);

            renderer.drawLine(pixelX, yZeroPixel - tickSize, pixelX, yZeroPixel + tickSize);            
            renderer.drawText(DF.format(x), pixelX - 10, yZeroPixel + tickSize + 15);
        }

        // --- Logica Asse Y ---
        double startY = Math.ceil(viewport.minY / tickSpacingY) * tickSpacingY;

        // Usiamo un piccolo epsilon per gestire gli errori di virgola mobile nella condizione <=
        for (double y = startY; y <= viewport.maxY + (tickSpacingY / 2.0); y += tickSpacingY) {
        	if (Math.abs(y) < 1e-9) continue;
            int pixelY = viewport.convertMathYToPixelY(y);
            
            renderer.drawLine(xZeroPixel - tickSize, pixelY, xZeroPixel + tickSize, pixelY);
            renderer.drawText(DF.format(y), xZeroPixel - tickSize - 30, pixelY + 5);
        }
    }


    private static double calculateNiceTickSpacing(double range, int screenPixelSize) {
        if (range == 0) return 1.0;
        
        // Stima il numero desiderato di tacche (es. 10 tacche lungo l'asse)
        // Puoi regolare '10.0' in base a quanti tick desideri in media
        double desiredTickCount = 10.0; 
        
        // Calcola una spaziatura grezza
        double roughSpacing = range / desiredTickCount;

        double magnitude = Math.floor(Math.log10(roughSpacing));
        double magnitudePower = Math.pow(10, magnitude);
        
        // Normalizza la spaziatura grezza
        double normalizedRough = roughSpacing / magnitudePower;

        double niceNormalized;
        // Sceglie il numero "bello" più vicino (1, 2, 5, 10...)
        if (normalizedRough < 1.0) niceNormalized = 1.0;
        else if (normalizedRough < 2.0) niceNormalized = 2.0;
        else if (normalizedRough < 5.0) niceNormalized = 5.0;
        else niceNormalized = 10.0; // Includendo da 5.0 a 10.0
        
        return niceNormalized * magnitudePower;
    }
}
