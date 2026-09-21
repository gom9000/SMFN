package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.Solver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;

/**
 * Contratto comune a tutti i solver agli autovalori, a prescindere
 * dall'algoritmo concreto o dal tipo scalare K su cui operano.
 *
 * Ogni solver resta utilizzabile da solo (JacobiEigenvalueSolver,
 * HermitianEigenvalueSolver, e in futuro QREigenvalueSolver per il caso
 * generale) -- questa interfaccia e' solo il contratto comune, non l'unico
 * modo di accedervi. Vedi GeneralEigenvalueSolver per un punto di ingresso
 * che sceglie il solver giusto automaticamente.
 */
public interface EigenvalueSolver<K extends ScalarElement<K>>
extends Solver<SquareMatrix<K>, EigenDecomposition>
{
	EigenDecomposition solve(SquareMatrix<K> matrix, ConvergenceParameters params);
}
