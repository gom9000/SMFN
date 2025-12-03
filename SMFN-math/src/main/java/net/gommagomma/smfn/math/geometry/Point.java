package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;

/**
 * Rappresenta un punto geometrico nello spazio N-dimensionale.
 */
public class Point
implements AlgebraicElement<Point>
{
    private final RealVector position;

    // --- Costruttori ---
    public Point(RealVector position) { this.position = position; }
    public Point(Real... components) { this(new RealVector(components)); }
    public Point(Real x, Real y) { this(new RealVector(x, y)); }
    public Point(Real x, Real y, Real z) { this(new RealVector(x, y, z)); }

    // --- Metodi geometrici/utilitari ---
    public int dimension() { return position.dimension(); }
    public Real get(int index) { return position.get(index); } // Necessario per VectorElement

    // Metodi di accesso rapido
    public Real getX() { return get(0); }
    public Real getY() { return dimension() > 1 ? get(1) : new Real(Double.NaN); }
    public Real getZ() { return dimension() > 2 ? get(2) : new Real(Double.NaN); }
    
    public boolean isMathematicallyEqualTo(Point other) { return this.position.isMathematicallyEqualTo(other.position); }
    public Point copy() { return new Point(this.position.copy()); }
    public Point getZero() { return new Point(this.position.getZero()); }
    public Real distanceTo(Point other) {
        RealVector diff = this.position.subtract(other.position);
        return diff.norm();
    }    

    // --- Metodi Standard Java ---
    @Override public String toString() { return "P" + position.toString(); }
    @Override public boolean equals(Object o) { return (o instanceof Point) && isMathematicallyEqualTo((Point) o); }
    @Override public int hashCode() { return position.hashCode(); }
}
