package net.gommagomma.smfn.math.analysis.core.solvers;

/**
 * Contratto radice per tutti i solutori dell'architettura di analisi numerica della library.
 * 
 * Questa interfaccia definisce il livello piu' astratto del pattern Problem-Solver,
 * separando nettamente la formulazione matematica di un problema (P) dall'algoritmo
 * preposto alla sua risoluzione e al calcolo del risultato (R).
 * 
 * Le sotto-interfacce e le implementazioni concrete specializzano questo contratto
 * per specifiche famiglie di problemi (es. solutori di equazioni differenziali,
 * solutori di punto fisso o solutori per la ricerca degli zeri), definendo i metodi
 * di esecuzione appropriati e la gestione dei parametri di convergenza o integrazione.
 * </p>
 *
 * @param <P> Il tipo del problema di analisi numerica da risolvere
 * @param <R> Il tipo del risultato o della soluzione prodotta dal solutore
 */
public interface Solver<P, R>
{}

