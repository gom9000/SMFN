package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta un gruppo abeliano (o gruppo commutativo), ovvero un gruppo 
 * in cui l'operazione di gruppo (addizione) gode della proprietà commutativa.
 *
 * @param <E> il tipo degli elementi appartenenti alla struttura
 */
public interface AbelianGroup<E extends AlgebraicElement<E>>
extends AdditiveGroup<E>
{}
