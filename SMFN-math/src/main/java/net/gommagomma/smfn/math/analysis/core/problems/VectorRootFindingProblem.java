package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;
import net.gommagomma.smfn.math.linearalgebra.core.operators.LinearOperator;

public interface VectorRootFindingProblem<K extends FieldElement<K>, V extends VectorElement<K, V>> 
extends RootFindingProblem<V>
{
	@Override
	Morphism<V, V> getFunction();

	// Lo Jacobiano restituisce una Matrice (LinearOperator) per ogni punto V
	default Mapping<V, LinearOperator<K, V, ?>> getJacobian() {
		throw new UnsupportedOperationException();
	}
}