package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta un semianello (semiring), ovvero una struttura algebrica dotata di due leggi 
 * di composizione interna (addizione e moltiplicazione) in cui l'addizione forma un monoide 
 * commutativo e la moltiplicazione forma un monoide, con la proprietà distributiva.
 *
 * @param <E> il tipo degli elementi appartenenti alla struttura
 */
public interface Semiring<E extends AlgebraicElement<E>>
extends AdditiveMonoid<E>, MultiplicativeMonoid<E>
{}
