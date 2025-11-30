package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;


/**
 * Rappresenta un'ellisse nel piano 2D.
 * Implementa MathFunction dove D è un punto 2D (RealVector) e C è il valore della funzione implicita.
 */
public class Ellipse
implements GeometryEntity<RealVector, Real>
{    
    private final RealVector center; 
    private final Real semiAxisA; // Semiasse lungo l'asse X locale
    private final Real semiAxisB; // Semiasse lungo l'asse Y locale


    public Ellipse(RealVector center, Real semiAxisA, Real semiAxisB) {
        if (center.dimension() != 2) {
            throw new IllegalArgumentException("Ellipse center must be a 2D vector.");
        }
        this.center = center;
        this.semiAxisA = semiAxisA;
        this.semiAxisB = semiAxisB;
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
     * Valuta la funzione implicita dell'ellisse per un dato punto P(x, y).
     * Ritorna:
     * - Circa 0 sul bordo dell'ellisse.
     * - Negativo all'interno.
     * - Positivo all'esterno.
     */
    @Override
    public Real evaluate(RealVector inputPoint) {
        if (inputPoint.dimension() != 2) {
            throw new IllegalArgumentException("Input point must be a 2D vector.");
        }
        
        // Vettore differenza: P - Centro
        RealVector diff = inputPoint.subtract(center);
        Real x = new Real(diff.get(0).getValue()); // Assumendo get(0) per X, get(1) per Y
        Real y = new Real(diff.get(1).getValue());

        // Calcola l'equazione implicita: (x^2 / a^2) + (y^2 / b^2) - 1
        Real termX = x.multiply(x).divide(semiAxisA.multiply(semiAxisA));
        Real termY = y.multiply(y).divide(semiAxisB.multiply(semiAxisB));
        
        // Ritorna il risultato della funzione implicita
        return termX.add(termY).subtract(new Real(1.0));
    }
    
    // ... altri metodi geometrici ...
}
