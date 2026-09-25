package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta la struttura astratta di base per problemi di analisi numerica risolvibili tramite sequenze iterative.
 * 
 * Un IterationProblem definisce una trasformazione F: P -> P all'interno dello stesso spazio algebrico.
 * Questa interfaccia funge da fondamento strutturale per tutte le formulazioni in cui l'iterato successivo
 * o il residuo viene calcolato mappando un elemento del dominio in un altro elemento del medesimo tipo,
 * consentendo ai solutori iterativi di valutare la funzione o il passo di aggiornamento P_{k+1} = F(P_k)
 * tramite il metodo Mapping.apply.
 *
 * @param <P> Il tipo dell'elemento algebrico appartenente sia al dominio sia al codominio della mappa
 */
public interface IterationProblem<P extends AlgebraicElement<P>>
extends AnalysisProblem<P>, Mapping<P, P>
{}
