package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Capacita' opzionale: il problema sa fornire la propria Jacobiana. A
 * differenza della derivata scalare (un'altra funzione scalare), qui il
 * risultato in ogni punto e' un operatore (una SquareMatrix, che e' gia'
 * un LinearOperator<Vector<K>> come stabilito in linearalgebra) -- forma
 * diversa dalla derivata scalare, non solo tipo diverso: per questo resta
 * una capacita' separata da DifferentiableScalarProblem, non unificata.
 */
public interface DifferentiableVectorProblem<K extends ScalarElement<K>>
extends VectorRootFindingProblem<K>
{
	Mapping<Vector<K>, SquareMatrix<K>> getJacobian();
}
