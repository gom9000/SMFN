package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Capacita' opzionale: il problema sa fornire la propria derivata analitica.
 * Verificata con instanceof dal solver (es. Newton-Raphson), non con un
 * metodo di default che lancia eccezione -- stesso pattern gia' usato per
 * Conjugable/Sqrtable/InvertibleElements altrove nella libreria.
 */
public interface DifferentiableScalarProblem<T extends ScalarElement<T>>
extends ScalarRootFindingProblem<T>
{
	Mapping<T, T> getDerivative();
}
