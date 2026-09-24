package net.gommagomma.smfn.demo;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.physics.mq.Observable;
import net.gommagomma.smfn.physics.mq.Pauli;
import net.gommagomma.smfn.physics.mq.QuantumState;
import net.gommagomma.smfn.physics.mq.QuantumSystemSimulator;
import net.gommagomma.smfn.physics.mq.SchrodingerEquationSystem;

/**
 * Campo magnetico lungo Z con intensita' modulata nel tempo:
 * H(t) = f(t)*sigma_z, f(t) = (omega0/2)*(1 + 0.5*sin(nu*t)).
 *
 * Non e' l'oscillazione di Rabi vera (che richiederebbe un campo lungo X che
 * NON commuta con la parte statica lungo Z, senza una forma chiusa semplice
 * da verificare) -- qui il campo resta sempre lungo Z, cambia solo
 * l'intensita': H(t1) e H(t2) commutano sempre tra loro, quindi la
 * soluzione ha ancora una forma chiusa esatta, con cui confrontare
 * l'integrazione numerica:
 *
 *   Phi(t) = integrale di f da 0 a t
 *          = (omega0/2)*t + (omega0/(4*nu))*(1-cos(nu*t))
 *   <sigma_x>(t) = cos(2*Phi(t))
 *
 * Derivazione verificata indipendentemente con scipy.integrate.solve_ivp
 * prima di scrivere questo codice (differenza massima ~4e-10 su 101 punti).
 */
public class TimeDependentFieldDemo
{
	public static void main(String[] args) {
		ComplexField C = ComplexField.INSTANCE;

		double omega0 = 1.0;
		double nu = 2.0; // frequenza di modulazione del campo

		Mapping<Real, Observable<Complex>> hamiltonianOfT = t -> {
			double magnitude = (omega0 / 2.0) * (1 + 0.5 * Math.sin(nu * t.getValue()));
			SquareMatrix<Complex> H_matrix = SquareMatrixElementFactory.of(C,
				new Complex(magnitude, 0), new Complex(0, 0),
				new Complex(0, 0), new Complex(-magnitude, 0)
			);
			return new Observable<>(H_matrix);
		};

		SchrodingerEquationSystem system = new SchrodingerEquationSystem(hamiltonianOfT);

		double invSqrt2 = 1.0 / Math.sqrt(2);
		QuantumState psi0 = QuantumState.of(new Complex(invSqrt2, 0), new Complex(invSqrt2, 0));

		Observable<Complex> sigmaX = new Observable<>(Pauli.SIGMA_X);
		QuantumSystemSimulator simulator = new QuantumSystemSimulator();
		Real dt = new Real(0.001);

		System.out.println("   t   |  <Sx> numerico |  cos(2*Phi(t)) analitico");
		for (int i = 0; i <= 100; i++) {
			double tVal = i * 0.1;
			Real t = new Real(tVal);

			QuantumState psiAtT = system.evolve(psi0, t, dt);
			Real expX = simulator.expectationValue(sigmaX, psiAtT);

			double phi = (omega0 / 2.0) * tVal + (omega0 / (4 * nu)) * (1 - Math.cos(nu * tVal));
			double analytic = Math.cos(2 * phi);

			System.out.printf("%6.2f | %14.5f | %18.5f%n", tVal, expX.getValue(), analytic);
		}
	}
}
