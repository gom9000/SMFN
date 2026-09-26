package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Interfaccia radice marker per la rappresentazione formale dei problemi di analisi numerica.
 * Nell'architettura Problem-Solver della libreria, una classe che implementa AnalysisProblem definisce
 * la formulazione matematica di un problema (ad esempio la ricerca di zeri, il calcolo di punti fissi o l'integrazione
 * di equazioni differenziali) esplicitando la nozione di soluzione, senza vincolarsi ad uno specifico
 * algoritmo di risoluzione.
 * Il disaccoppiamento garantito da questa astrazione separa nettamente la modellazione del dominio analitico
 * dall'esecuzione iterativa gestita dai Solver.
 *
 * @param <P> Il tipo dell'elemento algebrico che definisce lo spazio della soluzione del problema
 */
public interface AnalysisProblem<P extends AlgebraicElement<P>>
{}
