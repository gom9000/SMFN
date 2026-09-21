package net.gommagomma.smfn.demo;

import java.util.Random;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.IntegrationParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.analysis.numerical.solvers.ode.RungeKutta4Solver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;
import net.gommagomma.smfn.physics.mq.Hamiltonian;
import net.gommagomma.smfn.physics.mq.MeasurementOutcome;
import net.gommagomma.smfn.physics.mq.Observable;
import net.gommagomma.smfn.physics.mq.QuantumSystemSimulator;
import net.gommagomma.smfn.physics.mq.SchrodingerEquationSystem;

/**
 * Tutto quello che il modulo physics.mq puo' fare oggi, su un qubit:
 *  1. Observable e valore di aspettazione (measure)
 *  2. Misura quantistica vera, con collasso (performMeasurement)
 *  3. Hamiltonian e stati stazionari (findStationaryStates)
 *  4. Evoluzione temporale (SchrodingerEquationSystem + RungeKutta4Solver),
 *     confrontata con la soluzione analitica nota
 *  5. isUnitary() -- proprieta' generale, non specifica di mq
 */
public class MqDemo
{
	public static void main(String[] args) {
		ComplexField C = ComplexField.INSTANCE;
		InnerProductVectorSpace<Complex, ComplexField> space = new InnerProductVectorSpace<>(C, 2);
		ConvergenceParameters eigenParams = new ConvergenceParameters(new Real(1e-12), 100);
		QuantumSystemSimulator sim = new QuantumSystemSimulator();

		// Pauli Z = [[1,0],[0,-1]] -- autovalori +1,-1, autostati |0>=(1,0), |1>=(0,1)
		SquareMatrix<Complex> Z = SquareMatrixElementFactory.of(C,
			new Complex(1, 0), new Complex(0, 0),
			new Complex(0, 0), new Complex(-1, 0)
		);
		Observable pauliZ = new Observable(Z);

		System.out.println("=== 1. Valore di aspettazione (measure) ===");
		Vector<Complex> zero = VectorElementFactory.of(space, new Complex(1, 0), new Complex(0, 0));
		double s = 1.0 / Math.sqrt(2);
		Vector<Complex> superposition = VectorElementFactory.of(space, new Complex(s, 0), new Complex(s, 0));
		System.out.println("<Z> su |0>            = " + sim.measure(space, pauliZ, zero) + " (atteso: 1)");
		System.out.println("<Z> su (|0>+|1>)/sqrt2 = " + sim.measure(space, pauliZ, superposition) + " (atteso: 0)");

		System.out.println("\n=== 2. Misura quantistica vera (performMeasurement), con collasso ===");
		Random random = new Random(7);
		MeasurementOutcome outcome = sim.performMeasurement(space, pauliZ, superposition, eigenParams, random);
		System.out.println("esito singolo su sovrapposizione: valore=" + outcome.getValue()
			+ " stato collassato=" + outcome.getCollapsedState());
		int plus = 0, trials = 10000;
		for (int i = 0; i < trials; i++) {
			if (sim.performMeasurement(space, pauliZ, superposition, eigenParams, random).getValue().getValue() > 0) plus++;
		}
		System.out.println("su " + trials + " misure: +1 nel " + (100.0 * plus / trials) + "% dei casi (atteso: ~50%)");

		System.out.println("\n=== 3. Hamiltonian e stati stazionari ===");
		// H = Pauli X = [[0,1],[1,0]] -- autovalori +-1, autostati (1,1)/sqrt2, (1,-1)/sqrt2
		SquareMatrix<Complex> X = SquareMatrixElementFactory.of(C,
			new Complex(0, 0), new Complex(1, 0),
			new Complex(1, 0), new Complex(0, 0)
		);
		Hamiltonian hamiltonian = new Hamiltonian(X);
		EigenDecomposition stationary = hamiltonian.findStationaryStates(eigenParams);
		System.out.println("livelli energetici = " + stationary.getEigenvalues() + " (atteso: -1 e 1)");

		System.out.println("\n=== 4. Evoluzione temporale, confrontata con la soluzione analitica ===");
		// Sotto H=X, a partire da |0>=(1,0): psi(t) = (cos t, -i sin t) -- soluzione nota in forma chiusa.
		// A t = pi/2: psi = (0, -i) -- trasferimento completo di popolazione a |1>.
		VectorSpace<Complex, ComplexField> vectorSpace = new VectorSpace<>(C, 2);
		SchrodingerEquationSystem schrodinger = new SchrodingerEquationSystem(hamiltonian); // Hamiltonian e' un Observable
		Real endTime = new Real(Math.PI / 2);

		InitialValueProblem<Complex, Vector<Complex>> ivp = new InitialValueProblem<>() {
			@Override public Vector<Complex> derivative(Vector<Complex> state, Real time) {
				return schrodinger.derivative(state, time);
			}
			@Override public Vector<Complex> getInitialState() { return zero; }
			@Override public Real getStartTime() { return new Real(0.0); }
		};

		RungeKutta4Solver<Complex, Vector<Complex>, ComplexField> rk4 = new RungeKutta4Solver<>();
		IntegrationParameters params = new IntegrationParameters(new Real(0.001));
		Vector<Complex> finalState = rk4.integrate(ivp, endTime, params, vectorSpace);

		System.out.println("psi(pi/2) numerico  = " + finalState);
		System.out.println("psi(pi/2) analitico = [0, -i]  (soluzione chiusa per H=Pauli X)");

		System.out.println("\n=== 5. isUnitary() -- proprieta' generale, non di mq ===");
		System.out.println("Pauli X isUnitary() = " + X.isUnitary() + " (atteso: true)");
	}
}
