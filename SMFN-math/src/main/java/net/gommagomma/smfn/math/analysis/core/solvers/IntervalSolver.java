package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;

/**
 * Contratto per solutori di problemi ai valori iniziali (IPV), rquazioni differenziali, su un intervallo temporale continuo [t_0, t_f].
 * 
 * Riceve in ingresso lo stato iniziale e il tempo di partenza t_0 racchiusi nel problema di Cauchy, ed esegue
 * l'integrazione numerica fino al raggiungimento del tempo finale t_f prescritto.
 * L'evoluzione dello stato V(t) viene calcolata integrando la dinamica dV/dt = F(t, V) definita dal problema,
 * applicando le strategie di suddivisione dell'intervallo e di controllo del passo specificate
 * mediante i parametri di integrazione.
 *
 * @param <K> Il tipo dello scalare appartenente all'anello sottostante
 * @param <V> Il tipo dello stato del sistema (elemento lineare vettoriale o matriciale)
 * @param <S> La struttura algebrica di anello e struttura scalare associata a K
 */
public interface IntervalSolver<K extends ScalarElement<K>, V extends LinearElement<V, K>, S extends Ring<K> & ScalarStructure<K>>
extends Solver<InitialValueProblem<K, V>, V>
{
	/**
     * Esegue l'integrazione numerica del problema di Cauchy dall'istante iniziale t_0 espresso
     * nel problema fino all'istante finale specificato endTime.
     *
     * @param problem Il problema ai valori iniziali (IVP) contenente la dinamica F(t, V), lo stato iniziale V_0 e il tempo t_0
     * @param endTime L'istante di tempo finale t_f fino al quale integrare il sistema
     * @param params I parametri di integrazione (es. ampiezza del passo dt, tolleranze di errore o strategie adattive)
     * @param space Il modulo algebrico che fornisce le operazioni di combinazione lineare sullo spazio degli stati V
     * @return Lo stato finale del sistema V(t_f) calcolato al tempo endTime
     */
    V integrate(InitialValueProblem<K, V> problem, Real endTime, IntegrationParameters params, Module<V, K, S> space);
}
