package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;


/**
 * Rappresenta un punto geometrico nello spazio N-dimensionale.
 * Estende RealVector, aggiungendo metodi e semantica specifici della geometria.
 */
public class Point extends RealVector {

    /**
     * Costruttore per creare un punto da coordinate specifiche.
     * @param components Le coordinate del punto.
     */
    public Point(Real... components) {
        super(components);
    }

    /**
     * Costruisce un punto a 2D.
     * @param x Coordinata X.
     * @param y Coordinata Y.
     */
    public Point(Real x, Real y) {
        super(x, y);
    }

    /**
     * Costruisce un punto a 3D.
     * @param x Coordinata X.
     * @param y Coordinata Y.
     * @param z Coordinata Z.
     */
    public Point(Real x, Real y, Real z) {
        super(x, y, z);
    }

    /**
     * Calcola la distanza euclidea tra questo punto e un altro punto.
     * @param other L'altro punto.
     * @return La distanza come oggetto Real.
     */
    public Real distanceTo(Point other) {
        // La distanza è la norma (modulo) del vettore differenza
        return this.subtract(other).norm(); 
    }

    // Metodi di accesso rapido per convenienza (sebbene RealVector abbia get(index))
    public Real getX() {
        return get(0);
    }

    public Real getY() {
        if (dimension() > 1) return get(1);
        throw new IndexOutOfBoundsException("Point has no Y coordinate.");
    }

    public Real getZ() {
        if (dimension() > 2) return get(2);
        throw new IndexOutOfBoundsException("Point has no Z coordinate.");
    }

    // Potresti voler sovrascrivere toString() per una migliore leggibilità geometrica
    @Override
    public String toString() {
        // ... formato tipo "P(1.0, 2.0, 3.0)" invece di un array ...
        return "P" + super.toString();
    }
}
