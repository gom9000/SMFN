package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Rappresenta un cerchio nel piano 2D.
 * Implementa GeometryEntity come capability Evaluable: 
 * la valutazione restituisce la distanza con segno dal bordo (f(P) = ||P - C|| - r).
 */
public final class Circle
implements GeometryEntity<Point, Real>
{
    private final Point center;
    private final Real radius;

    /**
     * Costruisce un cerchio dati centro e raggio.
     * @param center Punto 2D rappresentante il centro.
     * @param radius Raggio del cerchio.
     */
    public Circle(Point center, Real radius) {
        if (center.dimension() != 2) {
            throw new IllegalArgumentException("Circle center must be a 2D point.");
        }
        if (radius.getValue() < 0) {
            throw new IllegalArgumentException("Radius cannot be negative.");
        }
        this.center = center;
        this.radius = radius;
    }

    @Override
    public int getAmbientDimension() {
        return 2;
    }

    @Override
    public int getEntityDimension() {
        return 1;
    }

    @Override
    public boolean isOnEntity(Point point) {
        return Math.abs(evaluate(point).getValue()) < MathConstants.EPSILON;
    }

    @Override
    public Real evaluate(Point inputPoint) {
        return implicitFunctionAt(inputPoint);
    }

    /**
     * Calcola la funzione implicita: f(P) = dist(P, C) - r
     */
    @Override
    public Real implicitFunctionAt(Point point) {
        if (point.dimension() != 2) {
            throw new IllegalArgumentException("Input point must be 2D.");
        }

        RealVector displacement = point.subtract(center);
        Real distance = displacement.norm();

        return distance.subtract(radius);
    }

    // --- Getters ---
    public Point getCenter() { return center; }
    public Real getRadius() { return radius; }

    @Override
    public String toString() {
        return String.format("Circle(center=%s, radius=%s)", center, radius);
    }
}
