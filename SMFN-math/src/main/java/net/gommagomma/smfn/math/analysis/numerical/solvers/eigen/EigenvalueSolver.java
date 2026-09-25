package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.Solver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;

/**
 * Contratto comune a tutti i solver agli autovalori, a prescindere
 * dall'algoritmo concreto o dal tipo scalare K su cui operano.
 */
public interface EigenvalueSolver<K extends ScalarElement<K>>
extends Solver<SquareMatrix<K>, EigenDecomposition>
{
	EigenDecomposition solve(SquareMatrix<K> matrix, ConvergenceParameters params);
}
