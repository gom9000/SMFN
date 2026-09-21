package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentialEquationProblem;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.IntegrationParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.ode.RungeKutta4Solver;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Equazione di Schrodinger dipendente dal tempo: d|psi>/dt = (-i/hbar) H|psi>
 * (assumendo hbar = 1 in unita' naturali).
 *
 * Implementa l'interfaccia DifferentialEquationProblem per stati vettoriali a valori complessi.
 */
public final class SchrodingerEquationSystem
implements DifferentialEquationProblem<Complex, Vector<Complex>>
{
	private static final Complex MINUS_I = new Complex(0.0, -1.0);

	private final Observable hamiltonian;
	private final VectorSpace<Complex, ComplexField> space;

	public SchrodingerEquationSystem(Observable hamiltonian) {
		this.hamiltonian = hamiltonian;
		this.space = new VectorSpace<>(ComplexField.INSTANCE, hamiltonian.asOperator().getN());
	}

	@Override
	public Vector<Complex> derivative(Vector<Complex> state, Real time) {
		Vector<Complex> H_psi = hamiltonian.asOperator().apply(state);
		return space.scale(MINUS_I, H_psi);
	}

	/**
	 * Evolve initialState da t=0 a endTime, integrando con RungeKutta4Solver
	 * a passo fisso dt -- nasconde la costruzione manuale di
	 * InitialValueProblem (una classe anonima ripetuta identica in ogni
	 * demo finora) e la scelta del solver.
	 */
	public QuantumState evolve(QuantumState initialState, Real endTime, Real dt) {
		InitialValueProblem<Complex, Vector<Complex>> ivp = new InitialValueProblem<>() {
			@Override public Vector<Complex> derivative(Vector<Complex> state, Real time) {
				return SchrodingerEquationSystem.this.derivative(state, time);
			}
			@Override public Vector<Complex> getInitialState() { return initialState.asVector(); }
			@Override public Real getStartTime() { return new Real(0.0); }
		};

		RungeKutta4Solver<Complex, Vector<Complex>, ComplexField> solver = new RungeKutta4Solver<>();
		Vector<Complex> finalVector = solver.integrate(ivp, endTime, new IntegrationParameters(dt), space);
		return QuantumState.from(finalVector);
	}
}
