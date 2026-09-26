package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.Solver;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;

/**
 * Contratto comune a tutti i solver agli autovalori, a prescindere
 * dall'algoritmo concreto o dal tipo scalare K su cui operano.
 * <p>
 * Questo solutore non riceve un {@code MetricSpace} esterno sullo spazio di iterazione (il criterio
 * di arresto e' interno all'algoritmo, es. la norma della parte fuori diagonale per Jacobi), quindi
 * il {@link SolverResult} che restituisce non espone {@code StepDistanceAware}; non avendo
 * un'equazione F(x)=0 da annullare (e' una decomposizione spettrale, non una ricerca di zeri),
 * non espone nemmeno {@code ResidualAware}.
 */
public interface EigenvalueSolver<K extends ScalarElement<K>>
extends Solver<SquareMatrix<K>, EigenDecomposition>
{
	SolverResult<EigenDecomposition> solve(SquareMatrix<K> matrix, StoppingParameters params);
}
