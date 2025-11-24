// net.gommagomma.smfn.math.analysis.core.MathFunction.java (Versione Rivista)
package net.gommagomma.smfn.math.analysis.core;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

/**
 * Rappresenta una funzione matematica astratta che mappa un dominio T in un codominio R.
 * I tipi D e C sono vincolati a essere elementi della gerarchia algebrica della libreria.
 * 
 * @param <D> Il tipo di input (dominio), che estende AlgebraicElement<D>.
 * @param <C> Il tipo di output (codominio), che estende AlgebraicElement<c>.
 */
public interface MathFunction<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
{    
    /**
     * Esegue la valutazione della funzione per un dato input.
     * 
     * @param input L'elemento del dominio D.
     * @return L'elemento risultante nel codominio C.
     */
    C evaluate(D input);
}
