package net.gommagomma.smfn.math.linearalgebra.core;


import net.gommagomma.smfn.math.algebra.core.elements.additive.AbelianGroupElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;


public interface ModuleElement<K extends RingElement<K>, V extends ModuleElement<K, V>> 
extends SpaceElement<V>, AbelianGroupElement<V>
{
	// Aggiungiamo il riferimento alla struttura ospite (il Modulo o lo Spazio Vettoriale)
//    Module<K, V> getModule(); 

    int dimension();
    K get(int index);
    V multiplyByScalar(K scalar);
    V createNewInstance(@SuppressWarnings("unchecked") K... components); 

//    default K getScalarZero() {
//        return getModule().getScalarRing().additiveIdentity();
//    }
//    default K getScalarOne() {
//        return getModule().getScalarRing().multiplicativeIdentity();
//    }
}