package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta un monoide moltiplicativo commutativo, in cui l'operazione 
 * di moltiplicazione è sia associativa che commutativa.
 *
 * @param <E> il tipo degli elementi appartenenti alla struttura
 */
public interface CommutativeMultiplicativeMonoid<E extends AlgebraicElement<E>>
extends MultiplicativeMonoid<E>
{}
