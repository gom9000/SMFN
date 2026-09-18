package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta un anello commutativo, ovvero un anello in cui l'operazione di moltiplicazione 
 * gode della proprietà commutativa.
 *
 * @param <E> il tipo degli elementi appartenenti alla struttura
 */
public interface CommutativeRing<E extends AlgebraicElement<E>>
extends Ring<E>, CommutativeMultiplicativeMonoid<E>
{}
