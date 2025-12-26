package net.gommagomma.smfn.math.linearalgebra.core.operators;


import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement; 


/**
 * Un Operatore Lineare è un Morfismo che mappa un vettore in un altro vettore
 * dello stesso spazio (Endomorfismo), preservando le operazioni di somma e prodotto per scalare.
 */
public interface LinearOperator<K extends FieldElement<K>, V extends VectorElement<K, V>, O extends LinearOperator<K, V, O>> 
extends Morphism<V, V>
{
    /**
     * Valuta l'operatore sul vettore
     */
	default V transform(V vector) {
        return apply(vector);
    }
}
