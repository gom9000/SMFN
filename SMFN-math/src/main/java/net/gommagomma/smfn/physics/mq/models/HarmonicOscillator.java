package net.gommagomma.smfn.physics.mq.models;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.physics.mq.Hamiltonian;

/**
 * Oscillatore armonico quantistico, 1D: V(x) = (1/2)*m*omega^2*x^2, su un
 * dominio finito [-L,L] con condizioni al contorno di Dirichlet psi=0 ai
 * due estremi.
 *
 * Discretizzazione su N punti di griglia (inclusi gli estremi): x_i = -L + i*dx,
 * dx = 2L/(N-1). T = -(hbar^2/2m) d^2/dx^2 tridiagonale (differenze finite
 * centrate 2 ordine), V(x) aggiunto sulla diagonale.
 */
public final class HarmonicOscillator
{
	private HarmonicOscillator() {}

	/**
	 * Unita' naturali: m = 1.0, hbar = 1.0.
	 */
	public static Hamiltonian<Real> hamiltonian(int N, double L, double omega) {
		return hamiltonian(N, L, omega, 1.0, 1.0);
	}

	/**
	 * Costruisce l'Hamiltoniana consentendo di specificare massa e costante di Planck ridotta.
	 */
	public static Hamiltonian<Real> hamiltonian(int N, double L, double omega, double m, double hbar) {
		if (N <= 1) {
			throw new IllegalArgumentException("Il numero di punti di griglia N deve essere > 1");
		}
		if (L <= 0.0) {
			throw new IllegalArgumentException("La semi-ampiezza del dominio L deve essere > 0");
		}

		RealField R = RealField.INSTANCE;
		double dx = 2.0 * L / (N - 1);
		double factor = (hbar * hbar) / (2.0 * m * dx * dx);

		Real zero = R.zero();
		Real offDiag = new Real(-factor);

		Real[] data = new Real[N * N];
		for (int i = 0; i < N; i++) {
			int rowOffset = i * N;
			double x = -L + i * dx;
			double potential = 0.5 * m * omega * omega * x * x;
			for (int j = 0; j < N; j++) {
				if (i == j) {
					data[rowOffset + j] = new Real(2.0 * factor + potential);
				} else if (Math.abs(i - j) == 1) {
					data[rowOffset + j] = offDiag;
				} else {
					data[rowOffset + j] = zero;
				}
			}
		}

		SquareMatrix<Real> H = SquareMatrixElementFactory.of(R, List.of(data));
		return new Hamiltonian<>(H);
	}
}
