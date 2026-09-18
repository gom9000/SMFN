package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Rappresenta una retta geometrica bidimensionale definita da un punto di origine 
 * e da un vettore direzione dx, dy. s
 */
public final class Line
implements GeometryEntity<Point, Real>
{
	private static final RealField R = RealField.INSTANCE;

	private final Point origin;
	private final Real dx;
	private final Real dy;
	private final Real directionLength;

	/**
     * Costruisce una nuova retta passante per l'origine specificata e dotata del vettore direzione dato.
     * 
     * @param origin il punto di origine della retta (deve essere 2D)
     * @param dx la componente x del vettore direzione
     * @param dy la componente y del vettore direzione
     * @throws IllegalArgumentException se il punto di origine non è 2D o se il vettore direzione è nullo
     */
	public Line(Point origin, Real dx, Real dy) {
		if (origin.dimension() != 2) {
			throw new IllegalArgumentException("Il punto di riferimento di una retta deve essere 2D.");
		}
		double len = Math.sqrt(dx.getValue() * dx.getValue() + dy.getValue() * dy.getValue());
		if (len < MathConstants.EPSILON) {
			throw new IllegalArgumentException("Il vettore direzione non puo' essere nullo.");
		}
		this.origin = origin;
		this.dx = dx;
		this.dy = dy;
		this.directionLength = new Real(len);
	}

	/**
     * Crea e restituisce una retta passante per due punti distinti specificati.
     * 
     * @param a il primo punto
     * @param b il secondo punto
     * @return la Line passante per a e b
     */
	public static Line through(Point a, Point b) {
		Real[] direction = b.displacementTo(a); // b - a
		return new Line(a, direction[0], direction[1]);
	}

	@Override public int getAmbientDimension() { return 2; }
	@Override public int getEntityDimension() { return 1; }

	@Override
	public boolean isOnEntity(Point point) {
		return Math.abs(implicitFunctionAt(point).getValue()) < MathConstants.EPSILON;
	}

	/**
     * Valuta la funzione implicita associata alla retta nel punto P:
     * f(P) = ((dx,dy) x (P-origine)) / |(dx,dy)|, che rappresenta la distanza con segno.
     * 
     * @param point il punto 2D in cui valutare la funzione
     * @return la distanza con segno del punto dalla retta
     * @throws IllegalArgumentException se il punto non è 2D
     */
	@Override
	public Real implicitFunctionAt(Point point) {
		if (point.dimension() != 2) {
			throw new IllegalArgumentException("Il punto deve essere 2D.");
		}
		Real[] displacement = point.displacementTo(origin); // P - origine
		Real px = displacement[0];
		Real py = displacement[1];

		// Componente z del prodotto vettoriale direzione x spostamento
		Real cross = R.subtract(R.multiply(dx, py), R.multiply(dy, px));
		return R.divide(cross, directionLength);
	}

	/**
     * Calcola la distanza euclidea di un punto dalla retta.
     * 
     * @param point il punto di cui calcolare la distanza
     * @return la distanza non negativa sotto forma di Real
     */
	public Real distanceTo(Point point) {
		return implicitFunctionAt(point).abs();
	}

	/**
     * Restituisce il punto di origine della retta.
     * 
     * @return il punto di origine
     */
	public Point getOrigin() { return origin; }

	/**
     * Restituisce la componente x del vettore direzione della retta.
     * 
     * @return il valore di $\Delta x$
     */
	public Real getDirectionX() { return dx; }

	/**
     * Restituisce la componente y del vettore direzione della retta.
     * 
     * @return il valore di $\Delta y$
     */
	public Real getDirectionY() { return dy; }

	@Override
	public String toString() {
		return String.format("Line(origin=%s, direction=(%s, %s))", origin, dx, dy);
	}
}
