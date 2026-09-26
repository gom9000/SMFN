package net.gommagomma.smfn.math.analysis.core.problems;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Modellizza un sistema lineare Ax = b: una matrice dei coefficienti quadrata A e un vettore
 * dei termini noti b, per cui si cerca il vettore x che soddisfa l'equazione.
 *
 * @param <K> Il tipo dello scalare che compone la matrice dei coefficienti e i vettori
 */
public final class LinearSystemProblem<K extends ScalarElement<K>>
implements AnalysisProblem<Vector<K>>
{
	private final SquareMatrix<K> matrix;
	private final Vector<K> rhs;

	/**
     * Costruisce un problema di sistema lineare immutabile.
     *
     * @param matrix La matrice dei coefficienti quadrata A
     * @param rhs Il vettore dei termini noti b
     * @throws NullPointerException Se matrix o rhs sono nulli
     */
	public LinearSystemProblem(SquareMatrix<K> matrix, Vector<K> rhs) {
		this.matrix = Objects.requireNonNull(matrix, "matrix must not be null.");
		this.rhs = Objects.requireNonNull(rhs, "rhs must not be null.");
	}

	/**
     * Restituisce la matrice dei coefficienti A.
     *
     * @return La matrice quadrata A del sistema Ax = b
     */
	public SquareMatrix<K> getMatrix() {
		return matrix;
	}

	/**
     * Restituisce il vettore dei termini noti b.
     *
     * @return Il vettore b del sistema Ax = b
     */
	public Vector<K> getRhs() {
		return rhs;
	}
}
