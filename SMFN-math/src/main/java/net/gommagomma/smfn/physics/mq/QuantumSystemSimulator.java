package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Simulatore per sistemi quantistici per misurare un osservabile, 
 * calcolando il valore di aspettazione (valore medio) su un dato stato quantistico: 
 * <psi|H|psi>.
 */
public final class QuantumSystemSimulator
{
	/**
     * Calcola il valore di aspettazione di un osservabile per uno stato quantistico specificato, 
     * sfruttando il prodotto interno hermitiano dello spazio vettoriale sottostante.
     * 
     * @param space lo spazio vettoriale con prodotto interno che ospita lo stato
     * @param observable l'osservabile hermitiano da misurare (H)
     * @param state il vettore di stato quantistico normalizzato o non normalizzato (|psi>)
     * @return il valore di aspettazione sotto forma di Real (garantito reale per operatori hermitiani)
     */
	public Real measure(InnerProductVectorSpace<Complex, ?> space, Observable observable, Vector<Complex> state) {
		Vector<Complex> H_psi = observable.asOperator().apply(state);
		Complex expectation = space.innerProduct(state, H_psi);
		// <psi|H|psi> e' garantito reale per un Observable hermitiano.
		return new Real(expectation.getRe());
	}
}
