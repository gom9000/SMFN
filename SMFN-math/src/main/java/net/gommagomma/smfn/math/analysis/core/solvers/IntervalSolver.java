package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;

/**
 * Integra un'equazione differenziale su [t0, endTime].
 *
 * Generico su V (LinearElement<V,K>), non fissato a Vector<K> -- vedi
 * DifferentialEquationProblem per il perche'. La struttura Module<V,K,S>
 * viene passata esplicitamente, come gia' fa IterativeSolver con MetricSpace:
 * il solver non deve indovinare come si sommano/scalano gli stati, gliela
 * fornisce chi lo chiama.
 */
public interface IntervalSolver<K extends ScalarElement<K>, V extends LinearElement<V, K>, S extends Ring<K> & ScalarStructure<K>>
extends Solver<InitialValueProblem<K, V>, V>
{
    V integrate(InitialValueProblem<K, V> problem, Real endTime, IntegrationParameters params, Module<V, K, S> space);
}
