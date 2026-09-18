package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Misura un osservabile su uno stato: valore di aspettazione <psi|H|psi>.
 *
 * E' letteralmente il prodotto interno hermitiano gia' costruito e
 * verificato in InnerProductVectorSpace -- nessun HilbertSpace a parte
 * serve, era il pacchetto "spaces" fantasma gia' scartato.
 */
public final class QuantumSystemSimulator
{
	public Real measure(InnerProductVectorSpace<Complex, ?> space, Observable observable, Vector<Complex> state) {
		Vector<Complex> H_psi = observable.asOperator().apply(state);
		Complex expectation = space.innerProduct(state, H_psi);
		// <psi|H|psi> e' garantito reale per un Observable hermitiano.
		return new Real(expectation.getRe());
	}
}
