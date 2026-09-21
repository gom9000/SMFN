package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;

/**
 * Contratto comune a tutti i solver agli autovalori, a prescindere
 * dall'algoritmo concreto o dal tipo scalare K su cui operano.
 *
 * Ogni solver resta utilizzabile da solo (JacobiEigenSolver, in futuro
 * HermitianEigenSolver e i solver per matrici generiche) -- questa
 * interfaccia e' solo il contratto comune, non l'unico modo di accedervi.
 * Vedi EigenSolvers per un punto di ingresso che sceglie il solver giusto
 * automaticamente.
 */
public interface EigenvalueSolver<K extends ScalarElement<K>>
{
	EigenDecomposition solve(SquareMatrix<K> matrix, ConvergenceParameters params);
}
