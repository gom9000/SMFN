package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.Module;
import net.gommagomma.smfn.math.linearalgebra.core.NormedVectorElement;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;

/**
 * Rappresenta un punto geometrico nello spazio N-dimensionale.
 * Implementa NormedVectorElement<Real, Point> tramite composizione di un RealVector.
 */
public class Point
implements NormedVectorElement<Real, Point>
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

    // --- Implementazioni di NormedVectorElement / VectorElement / Normable / AlgebraicElement ---

    @Override
    public Real norm() {
        return this.position.norm(); 
    }
    
    @Override
    public boolean isEqual(Point other) { return this.position.isEqual(other.position); }
    
    @Override
    public Point copy() { return new Point(this.position.copy()); }

    @Override
    public Point getZero() { return new Point(this.position.getZero()); }
    
    @Override
    public Point add(Point other) { return new Point(this.position.add(other.position)); }
    
    @Override
    public Point negate() { return new Point(this.position.negate()); }

    // I metodi 'multiplyByScalar', 'dotProduct' sono necessari per NormedVectorElement.
    @Override
    public Point multiplyByScalar(Real scalar) {
        // Assume che RealVector abbia questo metodo
        return new Point(this.position.multiplyByScalar(scalar));
    }
    
    @Override
    public Real dotProduct(Point other) {
        // Ritorna K (Real). Assume che RealVector abbia questo metodo.
        return this.position.dotProduct(other.position);
    }

    // --- Metodi Standard Java ---
    @Override public String toString() { return "P" + position.toString(); }
    @Override public boolean equals(Object o) { /* ... */ return (o instanceof Point) && isEqual((Point) o); }
    @Override public int hashCode() { return position.hashCode(); }

	@Override
	public Point createNewInstance(Real... components) {
		return new Point(components);
	}
    @Override
	public Module<Point, Real> getModule() {
		return null;
	}

}
