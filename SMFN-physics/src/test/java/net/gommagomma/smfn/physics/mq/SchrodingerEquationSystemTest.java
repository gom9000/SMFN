package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.IntegrationParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.ode.RungeKutta4Solver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

@DisplayName("SchrodingerEquationSystem: d|psi>/dt = -i*H*|psi>")
class SchrodingerEquationSystemTest
{
	private final ComplexField C = ComplexField.INSTANCE;
	private final SquareMatrixRing<Complex, ComplexField> M2 = new SquareMatrixRing<>(C, 2);
	private final VectorSpace<Complex, ComplexField> V2 = new VectorSpace<>(C, 2);

	private final SquareMatrix<Complex> pauliX = M2.of(new Complex[] {
		new Complex(0, 0), new Complex(1, 0),
		new Complex(1, 0), new Complex(0, 0)
	});

	@Test
	@DisplayName("derivative() calcola -i*H*psi correttamente")
	void derivativeIsMinusIHPsi() {
		Observable<Complex> H = new Observable<>(pauliX);
		SchrodingerEquationSystem system = new SchrodingerEquationSystem(H);

		Vector<Complex> psi = V2.of(new Complex[] { new Complex(1, 0), new Complex(0, 0) });
		Vector<Complex> derivative = system.derivative(psi, new Real(0.0));

		// H*psi = (0,1) ; -i*(0,1) = (0,-i)
		assertTrue(C.areEqual(derivative.get(0), new Complex(0, 0)));
		assertTrue(C.areEqual(derivative.get(1), new Complex(0, -1)));
	}

	@Test
	@DisplayName("Integrazione RK4 coincide con la soluzione analitica esatta: psi(t) = (cos t, -i sin t)")
	void integrationMatchesAnalyticSolution() {
		Observable<Complex> H = new Observable<>(pauliX);
		SchrodingerEquationSystem system = new SchrodingerEquationSystem(H);

		Vector<Complex> psi0 = V2.of(new Complex[] { new Complex(1, 0), new Complex(0, 0) });
		InitialValueProblem<Complex, Vector<Complex>> problem = new InitialValueProblem<>() {
			@Override public Vector<Complex> derivative(Vector<Complex> s, Real t) { return system.derivative(s, t); }
			@Override public Vector<Complex> getInitialState() { return psi0; }
			@Override public Real getStartTime() { return new Real(0.0); }
		};

		RungeKutta4Solver<Complex, Vector<Complex>, ComplexField> solver = new RungeKutta4Solver<>();
		IntegrationParameters params = new IntegrationParameters(new Real(0.001));

		double t = Math.PI / 2.0;
		Vector<Complex> psiT = solver.integrate(problem, new Real(t), params, V2);

		// Soluzione esatta: psi(pi/2) = (cos(pi/2), -i*sin(pi/2)) = (0, -i)
		double tolerance = 1e-6; // tolleranza numerica di RK4 a passo 0.001, non l'epsilon di ComplexField
		assertTrue(Math.abs(psiT.get(0).getRe()) < tolerance);
		assertTrue(Math.abs(psiT.get(0).getIm()) < tolerance);
		assertTrue(Math.abs(psiT.get(1).getRe()) < tolerance);
		assertTrue(Math.abs(psiT.get(1).getIm() - (-1.0)) < tolerance);
	}

	@Test
	@DisplayName("evolve() da' lo stesso risultato dell'integrazione manuale via InitialValueProblem")
	void evolveMatchesManualIntegration() {
		Observable<Complex> H = new Observable<>(pauliX);
		SchrodingerEquationSystem system = new SchrodingerEquationSystem(H);

		QuantumState psi0 = QuantumState.of(new Complex(1, 0), new Complex(0, 0));
		QuantumState psiT = system.evolve(psi0, new Real(Math.PI / 2.0), new Real(0.001));

		// Stessa soluzione esatta del test precedente: psi(pi/2) = (0, -i)
		double tolerance = 1e-6;
		Vector<Complex> v = psiT.asVector();
		assertTrue(Math.abs(v.get(0).getRe()) < tolerance);
		assertTrue(Math.abs(v.get(0).getIm()) < tolerance);
		assertTrue(Math.abs(v.get(1).getRe()) < tolerance);
		assertTrue(Math.abs(v.get(1).getIm() - (-1.0)) < tolerance);
	}

	@Test
	@DisplayName("Hamiltoniana dipendente dal tempo (campo Z modulato): coincide con la soluzione analitica esatta")
	void timeDependentHamiltonianMatchesAnalyticSolution() {
		// H(t) = f(t)*sigma_z, f(t) = (omega0/2)*(1+0.5*sin(nu*t)) -- il campo resta
		// sempre lungo Z, quindi H(t1) e H(t2) commutano sempre e la soluzione ha
		// ancora forma chiusa: <sigma_x>(t) = cos(2*Phi(t)), Phi(t) = integrale di f.
		// Derivazione verificata indipendentemente con scipy prima di scrivere questo test.
		double omega0 = 1.0;
		double nu = 2.0;

		net.gommagomma.smfn.math.algebra.core.Mapping<Real, Observable<Complex>> hamiltonianOfT = t -> {
			double magnitude = (omega0 / 2.0) * (1 + 0.5 * Math.sin(nu * t.getValue()));
			SquareMatrix<Complex> H_t = M2.of(new Complex[] {
				new Complex(magnitude, 0), new Complex(0, 0),
				new Complex(0, 0), new Complex(-magnitude, 0)
			});
			return new Observable<>(H_t);
		};

		SchrodingerEquationSystem system = new SchrodingerEquationSystem(hamiltonianOfT);
		QuantumState psi0 = QuantumState.of(new Complex(1 / Math.sqrt(2), 0), new Complex(1 / Math.sqrt(2), 0));
		Observable<Complex> sigmaX = new Observable<>(pauliX);
		QuantumSystemSimulator simulator = new QuantumSystemSimulator();

		double tVal = 3.7; // un istante qualunque, non uno dei punti "facili" come 0 o pi
		QuantumState psiAtT = system.evolve(psi0, new Real(tVal), new Real(0.001));
		Real expX = simulator.expectationValue(sigmaX, psiAtT);

		double phi = (omega0 / 2.0) * tVal + (omega0 / (4 * nu)) * (1 - Math.cos(nu * tVal));
		double analytic = Math.cos(2 * phi);

		assertTrue(Math.abs(expX.getValue() - analytic) < 1e-4);
	}
}
