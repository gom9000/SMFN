// net.gommagomma.smfn.math.analysis.core.MathFunction.java (Versione Rivista)
package net.gommagomma.smfn.math.analysis.core;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

/**
 * Rappresenta una funzione matematica astratta che mappa un dominio T in un codominio R.
 * I tipi T e R sono vincolati a essere elementi della gerarchia algebrica della libreria.
 * 
 * @param <T> Il tipo di input (dominio), che estende AlgebraicElement<T>.
 * @param <R> Il tipo di output (codominio), che estende AlgebraicElement<R>.
 */
public interface MathFunction<T extends AlgebraicElement<T>, R extends AlgebraicElement<R>>
{    
    /**
     * Esegue la valutazione della funzione per un dato input.
     * 
     * @param input L'elemento del dominio T.
     * @return L'elemento risultante nel codominio R.
     */
    R evaluate(T input);
}
