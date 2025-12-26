package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Rappresenta un'ellisse nel piano 2D.
 * Implementa GeometryEntity dove il dominio è un Point (spazio affine) 
 * e il codominio è un Real (potenziale della funzione implicita).
 * * Equazione canonica: (x/a)^2 + (y/b)^2 - 1 = 0
 */
public final class Ellipse
implements GeometryEntity<Point, Real>
{
    private final Point center;
    private final Real semiAxisA; // Semiasse maggiore/minore lungo X
    private final Real semiAxisB; // Semiasse maggiore/minore lungo Y
    
    // Cache dei quadrati per ottimizzare la valutazione (eval)
    private final Real aSquared;
    private final Real bSquared;

    /**
     * Costruisce un'ellisse dati il centro e i semiassi.
     * @param center    Punto 2D rappresentante il centro dell'ellisse.
     * @param semiAxisA Lunghezza del semiasse orizzontale.
     * @param semiAxisB Lunghezza del semiasse verticale.
     */
    public Ellipse(Point center, Real semiAxisA, Real semiAxisB) {
        if (center.dimension() != 2) {
            throw new IllegalArgumentException("Ellipse center must be a 2D point.");
        }
        if (semiAxisA.getValue() <= 0 || semiAxisB.getValue() <= 0) {
            throw new IllegalArgumentException("Semi-axes must be positive values.");
        }

        this.center = center;
        this.semiAxisA = semiAxisA;
        this.semiAxisB = semiAxisB;
        
        // Pre-calcolo dei quadrati per evitare moltiplicazioni ripetute in implicitFunctionAt
        this.aSquared = semiAxisA.multiply(semiAxisA);
        this.bSquared = semiAxisB.multiply(semiAxisB);
    }

    @Override
    public int getAmbientDimension() {
        return 2;
    }

    @Override
    public int getEntityDimension() {
        return 1;
    }

    /**
     * Verifica se un punto appartiene al bordo dell'ellisse.
     * Utilizza una tolleranza epsilon per compensare l'approssimazione floating-point.
     */
    @Override
    public boolean isOnEntity(Point point) {
        return Math.abs(evaluate(point).getValue()) < MathConstants.EPSILON;
    }

    /**
     * Implementazione della capability Evaluable.
     */
    @Override
    public Real evaluate(Point inputPoint) {
        return implicitFunctionAt(inputPoint);
    }

    /**
     * Valuta la funzione implicita dell'ellisse: f(P) = (x-cx)^2/a^2 + (y-cy)^2/b^2 - 1
     * * @param point Il punto P(x,y) da testare.
     * @return Valore del potenziale: 0 sul bordo, negativo all'interno, positivo all'esterno.
     */
    @Override
    public Real implicitFunctionAt(Point point) {
        if (point.dimension() != 2) {
            throw new IllegalArgumentException("Input point must be a 2D point.");
        }

        // Punto (P) - Punto (Centro) = Vettore (Spostamento relativo)
        RealVector relativePos = point.subtract(center);
        
        Real x = relativePos.get(0);
        Real y = relativePos.get(1);

        // Calcolo dei termini (x^2 / a^2) e (y^2 / b^2)
        Real termX = x.multiply(x).divide(aSquared);
        Real termY = y.multiply(y).divide(bSquared);

        // Somma dei termini meno l'unità
        return termX.add(termY).subtract(new Real(1.0));
    }

    // --- Getters ---
    public Point getCenter() { return center; }
    public Real getSemiAxisA() { return semiAxisA; }
    public Real getSemiAxisB() { return semiAxisB; }

    @Override
    public String toString() {
        return String.format("Ellipse[center=%s, a=%s, b=%s]", center, semiAxisA, semiAxisB);
    }
}