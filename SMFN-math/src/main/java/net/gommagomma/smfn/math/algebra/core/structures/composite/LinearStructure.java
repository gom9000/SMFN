package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Rappresenta una struttura lineare, ovvero una struttura composta che supporta 
 * l'operazione di scalatura (moltiplicazione di un vettore/elemento lineare per uno scalare).
 *
 * @param <V> il tipo dell'elemento lineare
 * @param <K> il tipo dello scalare
 * @param <S> il tipo della struttura scalare di supporto
 */
public interface LinearStructure<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends ScalarStructure<K>> 
extends CompositeStructure<K, V, S>
{
	/**
     * Moltiplica un elemento lineare per uno scalare (operazione di scalatura).
     * 
     * @param scalar lo scalare moltiplicatore
     * @param vector l'elemento lineare da scalare
     * @return il nuovo elemento lineare risultante dalla scalatura
     */
    V scale(K scalar, V vector);
}
