package net.gommagomma.smfn.math.analysis.numerical.solvers.ode;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.metric.NormedSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentialEquationProblem;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.IntegrationParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.IntervalODEStepSolver;

/**
 * Integratore ODE Embedded Runge-Kutta di ordine 2(3) (Bogacki-Shampine),
 * con controllo adattivo del passo tramite stima dell'errore.
 */
public class EmbeddedRK23Solver<K extends ScalarElement<K>, T extends LinearElement<T, K>, S extends Field<K> & ScalarStructure<K> & NumericFactory<K>>
implements IntervalODEStepSolver<K, T, S>
{
	private static final RealField R = RealField.INSTANCE;

	// --- Coefficienti RKF2(3) (Bogacki-Shampine) ---
	private static final double A21 = 0.5;
	private static final double A32 = 0.75;
	private static final double A41 = 2.0 / 9.0;
	private static final double A42 = 1.0 / 3.0;
	private static final double A43 = 4.0 / 9.0;

	private static final double C31 = 2.0 / 9.0; // ordine 3, stima di ordine superiore
	private static final double C32 = 1.0 / 3.0;
	private static final double C33 = 4.0 / 9.0;

	private static final double C21 = 7.0 / 24.0; // ordine 2, usato per l'errore
	private static final double C22 = 1.0 / 4.0;
	private static final double C23 = 1.0 / 3.0;
	private static final double C24 = 1.0 / 8.0;

	private static final double SAFETY = 0.9;
	private static final double P_INV = 1.0 / 3.0;

	private static final class EmbeddedStep<T> {
		final T result;
		final T errorEstimate;
		EmbeddedStep(T result, T errorEstimate) { this.result = result; this.errorEstimate = errorEstimate; }
	}

	private NormedSpace<T, K, S> requireNormedSpace(Module<T, K, S> space) {
		if (!(space instanceof NormedSpace)) {
			throw new IllegalArgumentException("EmbeddedRK23Solver richiede una struttura NormedSpace, per poter stimare l'errore.");
		}
		NormedSpace<T, K, S> normed = (NormedSpace<T, K, S>) space;
		return normed;
	}

	private EmbeddedStep<T> embeddedStep(DifferentialEquationProblem<K, T> system, T currentState, Real currentTime, Real deltaTime, Module<T, K, S> space) {
		S scalars = space.getScalarStructure();
		K dt = scalars.of(deltaTime.getValue());

		T k1_rate = system.derivative(currentState, currentTime);

		Real t2 = R.add(currentTime, R.multiply(deltaTime, new Real(A21)));
		T y2 = space.add(currentState, space.scale(scalars.multiply(scalars.of(A21), dt), k1_rate));
		T k2_rate = system.derivative(y2, t2);

		Real t3 = R.add(currentTime, R.multiply(deltaTime, new Real(A32)));
		T y3 = space.add(currentState, space.scale(scalars.multiply(scalars.of(A32), dt), k2_rate));
		T k3_rate = system.derivative(y3, t3);

		Real t4 = R.add(currentTime, deltaTime);
		T term41 = space.scale(scalars.multiply(scalars.of(A41), dt), k1_rate);
		T term42 = space.scale(scalars.multiply(scalars.of(A42), dt), k2_rate);
		T term43 = space.scale(scalars.multiply(scalars.of(A43), dt), k3_rate);
		T y4 = space.add(space.add(currentState, term41), space.add(term42, term43));
		T k4_rate = system.derivative(y4, t4);

		T term31 = space.scale(scalars.multiply(scalars.of(C31), dt), k1_rate);
		T term32 = space.scale(scalars.multiply(scalars.of(C32), dt), k2_rate);
		T term33 = space.scale(scalars.multiply(scalars.of(C33), dt), k3_rate);
		T result = space.add(space.add(currentState, term31), space.add(term32, term33));

		T term21 = space.scale(scalars.multiply(scalars.of(C21), dt), k1_rate);
		T term22 = space.scale(scalars.multiply(scalars.of(C22), dt), k2_rate);
		T term23 = space.scale(scalars.multiply(scalars.of(C23), dt), k3_rate);
		T term24 = space.scale(scalars.multiply(scalars.of(C24), dt), k4_rate);
		T resultOrder2 = space.add(space.add(space.add(currentState, term21), term22), space.add(term23, term24));

		T errorEstimate = space.subtract(result, resultOrder2);

		return new EmbeddedStep<>(result, errorEstimate);
	}

	@Override
	public T step(DifferentialEquationProblem<K, T> system, T currentState, Real currentTime, Real deltaTime, Module<T, K, S> space) {
		return embeddedStep(system, currentState, currentTime, deltaTime, space).result;
	}

	@Override
	public T integrate(InitialValueProblem<K, T> problem, Real endTime, IntegrationParameters params, Module<T, K, S> space) {
		NormedSpace<T, K, S> normedSpace = requireNormedSpace(space);

		T currentState = problem.getInitialState();
		Real currentTime = problem.getStartTime();

		Real tolerance = params.tolerance;
		Real maxStepSize = params.maxStepSize;
		Real minStepSize = params.minStepSize;
		if (tolerance == null || maxStepSize == null || minStepSize == null) {
			throw new IllegalArgumentException("EmbeddedRK23Solver richiede tolerance, maxStepSize e minStepSize.");
		}

		boolean forward = currentTime.isLessThan(endTime);
		Real timeDirection = forward ? new Real(1.0) : new Real(-1.0);

		Real h_abs = (params.fixedStepSize != null) ? params.fixedStepSize : R.divide(maxStepSize, new Real(10.0));
		h_abs = new Real(Math.min(h_abs.getValue(), maxStepSize.getValue()));
		h_abs = new Real(Math.max(h_abs.getValue(), minStepSize.getValue()));

		while ((forward && currentTime.isLessThan(endTime)) || (!forward && endTime.isLessThan(currentTime))) {
			Real remainingTime = R.subtract(endTime, currentTime);
			double remaining_modulus_val = remainingTime.abs().getValue();

			double h_to_execute_modulus_val = Math.min(h_abs.getValue(), remaining_modulus_val);
			h_to_execute_modulus_val = Math.min(h_to_execute_modulus_val, maxStepSize.getValue());
			Real h_to_execute_modulus = new Real(h_to_execute_modulus_val);

			if (R.isZero(h_to_execute_modulus)) break;

			Real h_to_execute = R.multiply(h_to_execute_modulus, timeDirection);

			while (true) {
				EmbeddedStep<T> stepResult = embeddedStep(problem, currentState, currentTime, h_to_execute, space);
				T errorEstimate = stepResult.errorEstimate;

				double errorNorm = normedSpace.norm(errorEstimate).getValue();
				double stateNorm = normedSpace.norm(currentState).getValue();
				double errorRatio = errorNorm / (tolerance.getValue() * Math.max(stateNorm, 1.0));

				double scaleFactor = (errorRatio <= 1e-12) ? 5.0 : SAFETY * Math.pow(errorRatio, -P_INV);
				Real h_new_abs = new Real(h_to_execute_modulus.getValue() * scaleFactor);

				if (errorRatio <= 1.0) {
					currentState = stepResult.result;
					currentTime = R.add(currentTime, h_to_execute);

					h_abs = new Real(Math.min(h_new_abs.getValue(), maxStepSize.getValue()));
					h_abs = new Real(Math.max(h_abs.getValue(), minStepSize.getValue()));
					break;
				} else {
					h_abs = new Real(Math.min(h_new_abs.getValue(), maxStepSize.getValue()));

					if (h_abs.isLessThan(minStepSize)) {
						throw new RuntimeException("Risoluzione ODE fallita: passo adattivo (" + h_abs + ") troppo piccolo.");
					}

					h_to_execute_modulus_val = Math.min(h_abs.getValue(), remaining_modulus_val);
					h_to_execute_modulus_val = Math.min(h_to_execute_modulus_val, maxStepSize.getValue());
					h_to_execute = R.multiply(new Real(h_to_execute_modulus_val), timeDirection);
				}
			}
		}

		return currentState;
	}
}
