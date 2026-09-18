package net.gommagomma.smfn.math.analysis.core.functions;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Capacita' opzionale: la funzione sa fornire il proprio gradiente
 * analitico. Verificata con instanceof da chi la consuma (es.
 * MultivariateFunctionSystemProblem, riga per riga), non con un default che
 * lancia eccezione -- stesso pattern di DifferentiableScalarProblem.
 */
public interface DifferentiableMultivariateFunction<K extends ScalarElement<K>>
extends MultivariateFunction<K>
{
	Mapping<Vector<K>, Vector<K>> getGradient();
}
