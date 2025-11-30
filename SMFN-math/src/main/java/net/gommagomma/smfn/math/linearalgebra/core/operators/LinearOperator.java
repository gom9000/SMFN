package net.gommagomma.smfn.math.linearalgebra.core.operators;


import net.gommagomma.smfn.math.algebra.core.MathFunction;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement; 


/**
 * Rappresenta un operatore lineare che agisce su elementi di uno spazio vettoriale.
 * Un operatore lineare mappa un vettore V a un altro vettore V.
 * 
 * @param <K> Il campo scalare.
 * @param <V> L'elemento dello spazio vettoriale.
 * @param <O> L'operatore stesso (vincolo ricorsivo).
 */
public interface LinearOperator<K extends FieldElement<K>, V extends VectorElement<K, V>, O extends LinearOperator<K, V, O>> 
extends MathFunction<V, V>
{
    /**
     * Applica l'operatore a un dato vettore (l'azione dell'operatore).
     * @param vector Il vettore di input.
     * @return Il vettore di output.
     */
    @Override
    V evaluate(V vector);
}
