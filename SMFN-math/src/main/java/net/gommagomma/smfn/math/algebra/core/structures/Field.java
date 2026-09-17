package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.InvertibleElements;

/**
 * Un campo garantisce l'inverso per ogni elemento diverso da zero: e' quindi
 * un caso particolare (totale) di InvertibleElements, dove l'unica eccezione
 * nota e' proprio lo zero. inverse(E) e' gia' dichiarato da MultiplicativeGroup
 * con la stessa firma richiesta da InvertibleElements: nessun metodo nuovo da
 * implementare, solo un isInvertible() di default per dirlo esplicitamente.
 */
public interface Field<E extends AlgebraicElement<E>>
extends CommutativeRing<E>, MultiplicativeGroup<E>, InvertibleElements<E>
{
	@Override
	default boolean isInvertible(E e) {
		return !isZero(e);
	}
}
