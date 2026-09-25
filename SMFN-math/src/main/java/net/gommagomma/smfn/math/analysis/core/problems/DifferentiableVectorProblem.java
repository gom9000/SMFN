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

/**
 * Estensione VectorRootFindingProblem per problemi di ricerca degli zeri multivariati
 * che forniscono la matrice Jacobiana analitica del sistema residuo.
 * 
 * Per un sistema di n equazioni non lineari in n incognite F(x) = 0, la matrice Jacobiana J in K^nxn
 * raccoglie le derivate parziali di ciascun residuo rispetto a ogni variabile d'ingresso:
 * J_ij(x) = dF_i / dx_j (x)
 * 
 * Nel modello Problem-Solver della library, questa interfaccia funge da "capability" opzionale.
 * I solutori vettoriali controllano mediante instanceof se il problema implementa questo contratto:
 * - Se presente, utilizzano la matrice Jacobiana esatta fornita da getJacobian()
 * - Se assente, eseguono il fallback su uno stimatore numerico
 *
 * @param <K> Il tipo dello scalare che compone i vettori e le matrici dello spazio vettoriale $n$-dimensionale
 */
public interface DifferentiableVectorProblem<K extends ScalarElement<K>>
extends VectorRootFindingProblem<K>
{
	/**
     * Restituisce il mapping che calcola la matrice Jacobiana J(x) per un dato vettore di stato x.
     *
     * @return La mappa
     */
	Mapping<Vector<K>, SquareMatrix<K>> getJacobian();
}
