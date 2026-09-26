package net.gommagomma.smfn.demo.physics;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.physics.mq.Hamiltonian;
import net.gommagomma.smfn.physics.mq.Observable;
import net.gommagomma.smfn.physics.mq.Pauli;
import net.gommagomma.smfn.physics.mq.QuantumState;
import net.gommagomma.smfn.physics.mq.QuantumSystemSimulator;
import net.gommagomma.smfn.physics.mq.SchrodingerEquationSystem;

/**
 * Precessione dello Spin in Campo Magnetico (Qubit / Sistema a due livelli).
 *
 * Un sistema a due livelli (spin 1/2) immerso in un campo magnetico uniforme
 * diretto lungo l'asse Z (B = B0*z_hat). L'Hamiltoniana e' proporzionale a
 * sigma_z: H = (omega0/2)*sigma_z. Lo stato iniziale e' preparato lungo
 * l'asse X, sovrapposizione equa di |0> e |1>.
 *
 * Facendo evolvere il sistema con SchrodingerEquationSystem.evolve() e
 * misurando sigma_x a vari istanti, il valore di aspettazione oscilla
 * sinusoidalmente: <sigma_x>(t) = cos(omega0*t) -- la classica precessione
 * di Larmor. Soluzione analitica verificata indipendentemente con numpy
 * prima di scrivere questo codice (per omega0=1, coincide con cos(t)).
 */
public class SpinPrecessionDemo
{
	public static void main(String[] args) {
		ComplexField C = ComplexField.INSTANCE;
		SquareMatrixAlgebra<Complex, ComplexField> matrixAlgebra = new SquareMatrixAlgebra<>(C, 2);

		// 1. Costruzione del modello: H = (omega0/2) * sigma_z
		double omega0 = 1.0;
		SquareMatrix<Complex> H_matrix = matrixAlgebra.scale(new Complex(omega0 / 2.0, 0), Pauli.SIGMA_Z);
		Hamiltonian<Complex> H = new Hamiltonian<>(H_matrix);
		SchrodingerEquationSystem system = new SchrodingerEquationSystem(H);

		// 2. Stato iniziale |psi(0)> = (|0> + |1>) / sqrt(2), lungo l'asse X
		double invSqrt2 = 1.0 / Math.sqrt(2);
		QuantumState psi0 = QuantumState.of(new Complex(invSqrt2, 0), new Complex(invSqrt2, 0));

		// 3. Evoluzione temporale via evolve() e misura di sigma_x a vari istanti
		Observable<Complex> sigmaX = new Observable<>(Pauli.SIGMA_X);
		QuantumSystemSimulator simulator = new QuantumSystemSimulator();
		Real dt = new Real(0.1);

		System.out.println("   t   |  <Sx> numerico |  cos(omega0*t) analitico");
		for (int i = 0; i <= 100; i++) {
			double tVal = i * 0.1;
			Real t = new Real(tVal);

			QuantumState psiAtT = system.evolve(psi0, t, dt);
			Real expX = simulator.expectationValue(sigmaX, psiAtT);

			System.out.printf("%6.2f | %14.4f | %18.4f%n", tVal, expX.getValue(), Math.cos(omega0 * tVal));
		}
	}
}
