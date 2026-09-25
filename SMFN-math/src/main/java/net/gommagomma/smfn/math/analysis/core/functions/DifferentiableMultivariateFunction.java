package net.gommagomma.smfn.math.analysis.core.functions;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Rappresenta una funzione multivariata differenziabile f: K^n \-> K.
 * Espone l'operatore gradiente nabla_f: K^n -> K^n, che ad ogni vettore di input associa
 * il corrispondente vettore delle derivate parziali.
 *
 * @param <K> Il tipo dello scalare appartenente al dominio e al codominio della funzione
 */
public interface DifferentiableMultivariateFunction<K extends ScalarElement<K>>
extends MultivariateFunction<K>
{
	/**
     * Restituisce la funzione vettoriale che rappresenta il gradiente di questa funzione multivariata.
     *
     * @return Il mapping gradiente nabla_f che trasforma un vettore di input nel vettore delle derivate parziali
     */
	Mapping<Vector<K>, Vector<K>> getGradient();
}
