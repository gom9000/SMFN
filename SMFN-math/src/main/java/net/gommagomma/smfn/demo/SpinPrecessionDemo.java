package net.gommagomma.smfn.demo;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.numerical.solvers.ode.RungeKutta4Solver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;
import net.gommagomma.smfn.physics.mq.Hamiltonian;
import net.gommagomma.smfn.physics.mq.Observable;
import net.gommagomma.smfn.physics.mq.Pauli;
import net.gommagomma.smfn.physics.mq.QuantumState;
import net.gommagomma.smfn.physics.mq.QuantumSystemSimulator;
import net.gommagomma.smfn.physics.mq.SchrodingerEquationSystem;

/**
 * Precessione di spin: spin 1/2 in un campo magnetico lungo Z, H = 0.5*sigma_Z.
 * Partendo da |+x> = (|0>+|1>)/sqrt(2), <sigma_x>(t) precede con soluzione
 * analitica chiusa nota: <sigma_x>(t) = cos(t) -- verificata indipendentemente
 * con numpy prima di scrivere questo codice.
 */
public class SpinPrecessionDemo
{
	public static void main(String[] args) {
		ComplexField C = ComplexField.INSTANCE;
		RealField R = RealField.INSTANCE;
		// RungeKutta4Solver.step() lavora ancora sul Module grezzo (serve per le sue
		// stesse combinazioni lineari interne, non e' un dettaglio che QuantumState
		// possa nascondere qui, a differenza di evolve() che integra fino a un tempo finale) --
		// vSpace resta quindi esplicito solo per questo scopo.
		VectorSpace<Complex, ComplexField> vSpace = new VectorSpace<>(C, 2);
		SquareMatrixAlgebra<Complex, ComplexField> matrixAlgebra = new SquareMatrixAlgebra<>(C, 2);

		// 1. Setup dell'Hamiltoniana (Spin 1/2 in campo B lungo Z, E_0 = 0.5)
		SquareMatrix<Complex> H_matrix = matrixAlgebra.scale(new Complex(0.5, 0), Pauli.SIGMA_Z);
		Hamiltonian H = new Hamiltonian(H_matrix);
		SchrodingerEquationSystem sys = new SchrodingerEquationSystem(H);

		// 2. Stato iniziale |+x> = (|0> + |1>) / sqrt(2)
		double invSqrt2 = 1.0 / Math.sqrt(2);
		QuantumState psi0 = QuantumState.of(new Complex(invSqrt2, 0), new Complex(invSqrt2, 0));

		// 3. Integrazione nel tempo ed estrazione dei valori di aspettazione
		Observable sigmaX = new Observable(Pauli.SIGMA_X);
		QuantumSystemSimulator simulator = new QuantumSystemSimulator();
		RungeKutta4Solver<Complex, Vector<Complex>, ComplexField> solver = new RungeKutta4Solver<>();

		Real dt = new Real(0.1);
		QuantumState psi = psi0;
		Real t = new Real(0.0);

		System.out.println("   t   |  <Sx> numerico |  cos(t) analitico");
		while (t.getValue() <= 10.0) {
			Real expX = simulator.measure(sigmaX, psi);
			System.out.printf("%6.2f | %14.4f | %18.4f%n", t.getValue(), expX.getValue(), Math.cos(t.getValue()));

			Vector<Complex> nextVector = solver.step(sys, psi.asVector(), t, dt, vSpace);
			psi = QuantumState.from(nextVector);
			t = R.add(t, dt);
		}
	}
}
