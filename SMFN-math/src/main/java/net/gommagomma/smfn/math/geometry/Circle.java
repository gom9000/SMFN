package net.gommagomma.smfn.math.geometry;


import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;


/**
 * Rappresenta un cerchio nel piano 2D.
 * Implementa MathFunction dove D è un punto 2D (RealVector) e C è la distanza (Real) dal centro.
 */
public class Circle
implements GeometryEntity<RealVector, Real>
{
    private final RealVector center; 
    private final Real radius;


    public Circle(RealVector center, Real radius) {
        if (center.dimension() != 2) {
            throw new IllegalArgumentException("Circle center must be a 2D vector.");
        }
        this.center = center;
        this.radius = radius;
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public boolean isOnEntity(RealVector point) {
    	return evaluate(point).isZero();
    }

    /**
     * Valuta la funzione implicita del cerchio per un dato punto P(x, y).
     * Ritorna la distanza (con segno) dal bordo del cerchio.
     */
    @Override
    public Real evaluate(RealVector inputPoint) {
        if (inputPoint.dimension() != 2) {
            throw new IllegalArgumentException("Input point must be a 2D vector.");
        }
        
        // Calcola il vettore differenza: inputPoint - center
        RealVector difference = inputPoint.subtract(center); 
        
        // Calcola la distanza euclidea (modulo del vettore differenza)
        // Assumo che RealVector abbia un metodo 'norm()' o 'modulus()'
        double distance = difference.norm().getValue(); 
        
        // Ritorna la distanza meno il raggio (funzione implicita con segno)
        return new Real(distance).subtract(radius);
    }
}
