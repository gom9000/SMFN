package net.gommagomma.smfn.math.algebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Rappresenta un elemento scalare (appartenente a un anello, campo o struttura scalare).
 * Costituisce la base per i numeri o i coefficienti atomici della libreria.
 *
 * @param <K> il tipo concreto dello scalare
 */
public interface ScalarElement<K extends ScalarElement<K>>
extends AlgebraicElement<K>
{
	/**
     * Restituisce la struttura algebrica scalare di riferimento a cui appartiene l'elemento.
     * 
     * @return la struttura scalare associata
     */
	ScalarStructure<K> getStructure();
}
