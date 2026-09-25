package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Estensione di ScalarRootFindingProblem per problemi scalari che forniscono la derivata analitica della funzione residuo.
 * 
 * Rappresenta un problema di ricerca degli zeri f(x) = 0 in cui è nota e calcolabile anche la derivata prima f'(x) = df/dx.
 * Nel modello Problem-Solver della libreria, questa interfaccia funge da "capability".
 * I solutori scalari basati sulle derivate verificano mediante l'operatore instanceof se il problema fornito implementa questa interfaccia:
 * - Se presente, utilizzano la derivata esatta fornita da getDerivative()
 * - Se assente, eseguono il fallback su stimatori di differenziazione numerica
 *
 * @param <T> Il tipo dello scalare appartenente al dominio e al codominio del problema
 */
public interface DifferentiableScalarProblem<T extends ScalarElement<T>>
extends ScalarRootFindingProblem<T>
{
	/**
     * Restituisce la mappa della derivata prima f'(x) per la funzione residuo del problema.
     *
     * @return Il Mapping che trasforma un punto scalare x nel valore della sua derivata f'(x)
     */
	Mapping<T, T> getDerivative();
}
