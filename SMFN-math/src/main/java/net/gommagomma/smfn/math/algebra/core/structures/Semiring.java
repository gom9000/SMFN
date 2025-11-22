package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.AlgebraicStructure;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;


public interface Semiring<E extends SemiringElement<E>>
extends AlgebraicStructure<E>
{
	/**
	 * Restituisce l'elemento neutro additivo (lo zero).
	 */
	E additiveIdentity();


	/**
	 * Restituisce l'elemento neutro moltiplicativo (l'uno).
	 */
	E multiplicativeIdentity();
}
