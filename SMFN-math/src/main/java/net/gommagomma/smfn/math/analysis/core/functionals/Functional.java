package net.gommagomma.smfn.math.analysis.core.functionals;

import net.gommagomma.smfn.math.algebra.core.Mapping;

/**
 * Rappresenta un funzionale matematico che mappa una funzione e un punto del suo dominio
 * a un valore di output scalare.
 * 
 * @param <F> Il tipo della funzione (mapping) su cui agisce il funzionale
 * @param <I> Il tipo del punto di input (dominio della funzione)
 * @param <K> Il tipo dello scalare di output (codominio della funzione e risultato del funzionale)
 */
public interface Functional<F extends Mapping<I, K>, I, K>
{
	/**
     * Valuta il funzionale applicando la funzione specificata nel punto fornito.
     *
     * @param function La funzione su cui operare
     * @param point Il punto appartenente al dominio della funzione in cui calcolare il valore
     * @return Il valore scalare risultante dalla valutazione
     */
	K evaluate(F function, I point);
}
