package net.gommagomma.smfn.math.analysis.models;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;


//T è vincolato a essere un VectorElement operante su un campo K (es. Real, Complex)
public interface DynamicSystem<K extends FieldElement<K>, T extends VectorElement<K, T>> {
	/**
	 * Calcola la derivata (dX/dt = f(X, t)) dato lo stato e il tempo.
	 * La funzione ritorna un rate di cambiamento, che è dello stesso tipo di T.
	 */
	T derivative(T state, Real time);
}
