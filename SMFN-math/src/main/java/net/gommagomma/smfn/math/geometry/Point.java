package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;
import java.util.Objects;

/**
 * Rappresenta un punto (posizione) nello spazio affine N-dimensionale.
 * A differenza di un vettore, un punto non supporta operazioni di somma tra simili.
 */
public final class Point
implements AlgebraicElement<Point>
{
    private final RealVector position;

    // --- Costruttori ---
    public Point(RealVector position) {
        this.position = Objects.requireNonNull(position, "Position vector cannot be null");
    }

    public Point(Real... components) {
        this(new RealVector(components));
    }

    public Point(Real x, Real y) {
        this(new RealVector(x, y));
    }

    public Point(Real x, Real y, Real z) {
        this(new RealVector(x, y, z));
    }

    // --- Proprietà Geometriche ---
    
    public int dimension() {
        return position.dimension();
    }

    public Real get(int index) {
        return position.get(index);
    }

    // Metodi di accesso rapido con fail-fast invece di NaN
    public Real getX() { return get(0); }
    
    public Real getY() {
        if (dimension() < 2) throw new IndexOutOfBoundsException("Point is 1D, no Y coordinate.");
        return get(1);
    }
    
    public Real getZ() {
        if (dimension() < 3) throw new IndexOutOfBoundsException("Point is less than 3D, no Z coordinate.");
        return get(2);
    }

    // --- Operazioni Affini ---

    /**
     * Calcola la distanza tra questo punto e un altro punto.
     * Matematicamente: ||P1 - P2||
     */
    public Real distanceTo(Point other) {
        return this.subtract(other).norm();
    }

    /**
     * Sottrazione di due punti: P1 - P2 = V (restituisce un vettore).
     */
    public RealVector subtract(Point other) {
        if (this.dimension() != other.dimension()) {
            throw new IllegalArgumentException("Dimension mismatch in point subtraction.");
        }
        return this.position.subtract(other.position);
    }

    /**
     * Traslazione di un punto tramite un vettore: P + V = P'
     */
    public Point translate(RealVector vector) {
        return new Point(this.position.add(vector));
    }

    // --- Implementazione AlgebraicElement ---

    @Override
    public boolean isMathematicallyEqualTo(Point other) {
        if (other == null) return false;
        if (this.dimension() != other.dimension()) return false;
        return this.position.isMathematicallyEqualTo(other.position);
    }

    @Override
    public Point copy() {
        return new Point(this.position.copy());
    }

    // --- Metodi Standard ---

    @Override
    public String toString() {
        return "P" + position.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        return isMathematicallyEqualTo((Point) o);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }
}