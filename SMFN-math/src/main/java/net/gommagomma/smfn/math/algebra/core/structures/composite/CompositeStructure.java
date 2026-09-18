package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.CompositeElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.AlgebraicStructure;

/**
 * Rappresenta una struttura algebrica composta (come vettori o matrici), 
 * i cui elementi sono formati da collezioni di scalari gestiti da una struttura sottostante.
 *
 * @param <K> il tipo degli scalari costitutivi
 * @param <E> il tipo dell'elemento composto
 * @param <S> il tipo della struttura scalare di supporto
 */
public interface CompositeStructure<K extends ScalarElement<K>, E extends CompositeElement<K, E>, S extends ScalarStructure<K>>
extends AlgebraicStructure<E>
{
	/**
     * Restituisce la struttura scalare associata agli elementi di questa struttura composta.
     * 
     * @return la struttura scalare di riferimento
     */
	S getScalarStructure();
}
