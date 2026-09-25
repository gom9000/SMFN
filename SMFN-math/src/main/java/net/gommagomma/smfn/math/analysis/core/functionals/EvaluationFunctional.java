package net.gommagomma.smfn.math.analysis.core.functionals;

import net.gommagomma.smfn.math.algebra.core.Mapping;

/**
 * Specializzazione di Functional per strategie di valutazione diretta di una funzione in un punto.
 * Questa interfaccia caratterizza i funzionali il cui scopo primario è il calcolo del valore
 * assunto da una mappa algebrica o analitica (come polinomi o serie) in uno specifico elemento
 * del dominio, consentendo di astrarre ed intercambiare gli algoritmi di valutazione usati.
 *
 * @param <F> Il tipo della funzione (mapping) su cui agisce la valutazione
 * @param <I> Il tipo del punto di input (dominio della funzione)
 * @param <K> Il tipo dello scalare risultante (codominio della funzione)
 */
public interface EvaluationFunctional<F extends Mapping<I, K>, I, K> 
extends Functional<F, I, K>
{}
