package net.gommagomma.smfn.math.linearalgebra.core;


import net.gommagomma.smfn.math.algebra.core.elements.additive.AbelianGroupElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;


public interface VectorElement<K extends RingElement<K>, V extends VectorElement<K, V>> 
extends SpaceElement<V>, AbelianGroupElement<V>
{
	// Aggiungiamo il riferimento alla struttura ospite (il Modulo o lo Spazio Vettoriale)
    Module<V, K> getModule(); 

    int dimension();
    K get(int index);

    V createNewInstance(@SuppressWarnings("unchecked") K... components); 

    // I metodi add, subtract, negate sono ereditati da AbelianGroupElement<E>

    // Metodi specifici per l'algebra lineare/moduli:
    V multiplyByScalar(K scalar);

    // Prodotto scalare (dot product) - Nota: questo non è sempre definito su un modulo generale!
    // Per ora lo lasciamo, assumendo che i moduli che usi lo supportino (es. Z^n)
    K dotProduct(V other);

    default K getScalarZero() {
        return getModule().getScalarRing().additiveIdentity();
    }
    default K getScalarOne() {
        return getModule().getScalarRing().multiplicativeIdentity();
    }
}