package net.gommagomma.smfn.graphics.plotting;

import net.gommagomma.smfn.graphics.core.ColorMapper;

public class LinearColorMapper
implements ColorMapper
{    
    // Potresti voler parametrizzare questi colori nel costruttore
    private final int startColor = 0x000000; // Nero
    private final int endColor = 0xFFFFFF;   // Bianco
    private final double minValue = 0.0;
    private final double maxValue = 100.0; // Esempio: max iterazioni

    @Override
    public int toRGB(double value)
    {
        // Logica di interpolazione lineare semplice per mappare il valore nel range [0, 1]
        double normalized = (value - minValue) / (maxValue - minValue);
        normalized = Math.max(0.0, Math.min(1.0, normalized)); // Clampa il valore tra 0 e 1

        int r = (int) (((startColor >> 16) & 0xFF) * (1 - normalized) + ((endColor >> 16) & 0xFF) * normalized);
        int g = (int) (((startColor >> 8) & 0xFF) * (1 - normalized) + ((endColor >> 8) & 0xFF) * normalized);
        int b = (int) (((startColor >> 0) & 0xFF) * (1 - normalized) + ((endColor >> 0) & 0xFF) * normalized);

        return (r << 16) | (g << 8) | b;
    }
}
