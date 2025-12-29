package net.gommagomma.smfn.math.linearalgebra.core.operators;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.LinearCombinable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;


/**
 * Rappresenta un operatore Hermitiano (o autoaggiunto).
 * Una proprietà matematica richiesta dagli osservabili fisici.
 */
public interface HermitianMapping<K extends FieldElement<K>, V extends LinearCombinable<K, V>, H extends HermitianMapping<K, V, H>>
extends LinearMapping<K, V, H>
{
    // L'interfaccia garantisce la proprietà hermitiana per contratto.
    // Non possiamo verificarla a livello di tipo in Java, ma è un'assunzione di design.

	/**
     * Calcola il valore di aspettazione (media).
     * Matematicamente, questo è il risultato del prodotto interno <psi|O|psi>.
     * 
     * @param state Il vettore di stato normalizzato.
     * @return Il valore di aspettazione come oggetto Real (una proprietà degli operatori hermitiani).
     */
    Real expectationValue(V state);
}
