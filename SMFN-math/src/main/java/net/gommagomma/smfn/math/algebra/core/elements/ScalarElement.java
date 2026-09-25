package net.gommagomma.smfn.math.algebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Rappresenta un elemento scalare (appartenente a un anello, campo o struttura scalare).
 * Costituisce il tipo base per i coefficienti della libreria, coprendo sia tipi atomici
 * sia strutture composte utilizzate come scalari in strutture di livello superiore.
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
