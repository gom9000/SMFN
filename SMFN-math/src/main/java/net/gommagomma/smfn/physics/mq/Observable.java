package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;

/**
 * Rappresenta un osservabile fisico in meccanica quantistica, modellato matematicamente 
 * come un operatore hermitiano ($M = M^\dagger$) caratterizzato da autovalori reali.
 */
public final class Observable
{
	private final SquareMatrix<Complex> operator;

	/**
     * Costruisce un nuovo osservabile a partire dalla matrice quadrata complessa specificata,
     * verificando che l'operatore soddisfi la proprietà di hermiticità.
     * 
     * @param operator la matrice quadrata complessa che rappresenta l'operatore
     * @throws IllegalArgumentException se la matrice non è hermitiana
     */
	public Observable(SquareMatrix<Complex> operator) {
		if (!operator.isHermitian()) {
			throw new IllegalArgumentException("Un Observable deve essere rappresentato da un operatore hermitiano (M = M^dagger).");
		}
		this.operator = operator;
	}

	/**
     * Restituisce la matrice quadrata complessa associata all'operatore sottostante dell'osservabile.
     * 
     * @return la matrice operatore
     */
	public SquareMatrix<Complex> asOperator() {
		return operator;
	}
}
