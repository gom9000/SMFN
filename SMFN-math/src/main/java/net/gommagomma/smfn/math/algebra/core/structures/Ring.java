package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta un anello (ring), una struttura algebrica in cui l'addizione forma 
 * un gruppo abeliano e la moltiplicazione forma un monoide, raccordate dalla proprietà distributiva.
 *
 * @param <E> il tipo degli elementi appartenenti alla struttura
 */
public interface Ring<E extends AlgebraicElement<E>>
extends Semiring<E>, AbelianGroup<E>
{}
