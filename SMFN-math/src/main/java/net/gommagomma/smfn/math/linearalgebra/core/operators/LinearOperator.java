package net.gommagomma.smfn.math.linearalgebra.core.operators;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.LinearCombinable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement; 


/**
 * Un Operatore Lineare è un Morfismo che mappa un vettore in un altro vettore
 * dello stesso spazio (Endomorfismo), preservando le operazioni di somma e prodotto per scalare.
 */
public interface LinearOperator<K extends FieldElement<K>, V extends LinearCombinable<K, V>, O extends LinearOperator<K, V, O>> 
extends LinearMapping<K, V, O>, LinearCombinable<K, O> 
{
	@Override
	default V evaluate(V vector) {
		return apply(vector);
	}
}
