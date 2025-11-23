// net.gommagomma.smfn.graphics.plotting.AxisRenderable
package net.gommagomma.smfn.graphics.plotting;

import net.gommagomma.smfn.graphics.core.Renderable;
import net.gommagomma.smfn.math.algebra.numeric.Complex; // Dipendenza specifica da Complex

public class AxisRenderable
implements Renderable<Complex>
{
    private final double threshold;
    private final int axisColorRGB;

    public AxisRenderable(double threshold, int axisColorRGB) {
        this.threshold = threshold; // Es. 0.01 per uno spessore visibile
        this.axisColorRGB = axisColorRGB;
    }

    @Override
    public Integer renderPixel(Complex coordinate) {
        // Controlla se la distanza dall'asse X (componente immaginaria) o Y (componente reale) è minore della soglia
        if (Math.abs(coordinate.getRe()) < threshold || Math.abs(coordinate.getIm()) < threshold) {
            return axisColorRGB; 
        }
        return null; // Trasparente
    }
}
