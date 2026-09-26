package net.gommagomma.smfn.math.analysis.numerical.solvers.linear;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.analysis.core.problems.LinearSystemProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.Solver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Contratto comune ai solutori diretti di sistemi lineari Ax = b, a prescindere
 * dall'algoritmo concreto o dal tipo scalare K su cui operano.
 *
 * @param <K> Il tipo dello scalare che compone la matrice dei coefficienti e i vettori
 */
public interface LinearSystemSolver<K extends ScalarElement<K>>
extends Solver<LinearSystemProblem<K>, Vector<K>>
{
	/**
     * Risolve il sistema lineare Ax = b descritto dal problema fornito.
     *
     * @param problem Il sistema lineare da risolvere
     * @return Il vettore soluzione x
     */
	Vector<K> solve(LinearSystemProblem<K> problem);

	/**
     * Risolve il sistema lineare Ax = b, con matrice dei coefficienti e termine noto forniti separatamente.
     *
     * @param matrix La matrice dei coefficienti quadrata A
     * @param rhs Il vettore dei termini noti b
     * @return Il vettore soluzione x
     */
	default Vector<K> solve(SquareMatrix<K> matrix, Vector<K> rhs) {
		return solve(new LinearSystemProblem<>(matrix, rhs));
	}
}
