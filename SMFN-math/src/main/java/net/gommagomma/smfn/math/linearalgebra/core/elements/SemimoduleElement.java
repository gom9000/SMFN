package net.gommagomma.smfn.math.linearalgebra.core.elements;


import net.gommagomma.smfn.math.algebra.core.elements.additive.CommutativeMonoidElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;


/**
 * Rappresenta un elemento di un Semimodulo.
 * Generalizzato sul tipo di scalare K (che deve essere un SemiringElement)
 * e sul tipo del vettore V.
 */
public interface SemimoduleElement<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>>
extends SpaceElement<V>, CommutativeMonoidElement<V>
{
    int dimension(); 
    K get(int index); 
    void set(int index, K value); // Aggiungiamo il set per flessibilità
    
    /**
     * Moltiplicazione per uno scalare dal semianello K.
     */
    V multiplyByScalar(K scalar);
    
    // NOTA: Non definiamo un "dot product" qui, poiché richiede che il tipo K sia almeno un RingElement
    // e che ci sia la sottrazione nel semianello risultante, cosa non vera per i Naturali.
}