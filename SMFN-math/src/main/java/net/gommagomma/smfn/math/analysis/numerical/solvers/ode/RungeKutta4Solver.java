package net.gommagomma.smfn.math.analysis.numerical.solvers.ode;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentialEquationProblem;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.IntegrationParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.IntervalODEStepSolver;

/**
 * Integratore Runge-Kutta di quarto ordine (RK4), a passo fisso.
 *
 * Generico su qualunque stato T che sia un LinearElement<T,K> con una
 * struttura Module<T,K,S> -- vedi DifferentialEquationProblem per il perche'
 * (include gli stati matriciali, non solo Vector<K>). Il tempo resta sempre
 * Real, convenzione standard per le ODE indipendente dal campo K dello stato:
 * i passi temporali vengono convertiti in K solo per scalare lo stato,
 * tramite NumericFactory<K>.
 */
public class RungeKutta4Solver<K extends ScalarElement<K>, T extends LinearElement<T, K>, S extends Ring<K> & ScalarStructure<K> & NumericFactory<K>>
implements IntervalODEStepSolver<K, T, S>
{
	private static final RealField R = RealField.INSTANCE;

	@Override
	public T step(DifferentialEquationProblem<K, T> system, T currentState, Real currentTime, Real deltaTime, Module<T, K, S> space) {
		S scalars = space.getScalarStructure();
		K dt = scalars.of(deltaTime.getValue());
		K half = scalars.of(0.5);
		K two = scalars.of(2.0);
		K oneSixth = scalars.of(1.0 / 6.0);
		Real halfDt = R.multiply(deltaTime, new Real(0.5));

		// --- K1 ---
		T k1_rate = system.derivative(currentState, currentTime);
		T k1 = space.scale(dt, k1_rate);

		// --- K2 ---
		Real time_k2 = R.add(currentTime, halfDt);
		T state_k2 = space.add(currentState, space.scale(scalars.multiply(half, dt), k1_rate));
		T k2_rate = system.derivative(state_k2, time_k2);
		T k2 = space.scale(dt, k2_rate);

		// --- K3 ---
		Real time_k3 = time_k2;
		T state_k3 = space.add(currentState, space.scale(scalars.multiply(half, dt), k2_rate));
		T k3_rate = system.derivative(state_k3, time_k3);
		T k3 = space.scale(dt, k3_rate);

		// --- K4 ---
		Real time_k4 = R.add(currentTime, deltaTime);
		T state_k4 = space.add(currentState, space.scale(dt, k3_rate));
		T k4_rate = system.derivative(state_k4, time_k4);
		T k4 = space.scale(dt, k4_rate);

		// --- nextState = currentState + 1/6 * (K1 + 2*K2 + 2*K3 + K4) ---
		T sum = space.add(space.add(k1, space.scale(two, k2)), space.add(space.scale(two, k3), k4));
		return space.add(currentState, space.scale(oneSixth, sum));
	}

	@Override
	public T integrate(InitialValueProblem<K, T> problem, Real endTime, IntegrationParameters params, Module<T, K, S> space) {
		Real fixedDeltaTime = params.fixedStepSize;
		if (fixedDeltaTime == null) {
		    throw new IllegalArgumentException("RungeKutta4Solver richiede un parametro fixedStepSize non nullo.");
		}
		Real stepMagnitude = fixedDeltaTime.abs();
		if (R.isZero(stepMagnitude)) {
		    throw new IllegalArgumentException("RungeKutta4Solver richiede un parametro fixedStepSize non zero.");
		}

		T currentState = problem.getInitialState();
		Real currentTime = problem.getStartTime();

		boolean forward = currentTime.isLessThan(endTime);
		Real effectiveDeltaTime = forward ? stepMagnitude : R.negate(stepMagnitude);

		while ((forward && currentTime.isLessThan(endTime)) || (!forward && endTime.isLessThan(currentTime))) {
			Real remainingTime = R.subtract(endTime, currentTime);
			Real stepDt = (effectiveDeltaTime.abs().getValue() > remainingTime.abs().getValue())
				? remainingTime : effectiveDeltaTime;

			currentState = step(problem, currentState, currentTime, stepDt, space);
			currentTime = R.add(currentTime, stepDt);

			if (R.isZero(stepDt)) break;
		}

		return currentState;
	}
}
