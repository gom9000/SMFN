package net.gommagomma.smfn.physics.mq.models;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.physics.mq.Hamiltonian;

/**
 * Particella in una scatola 1D (buca di potenziale infinita):
 * V(x) = 0 per x in (0, L), V(x) = +infinito altrove.
 *
 * Condizioni al contorno di Dirichlet: psi(0) = psi(L) = 0.
 * Discretizzazione su N punti interni: x_i = i * dx, dx = L / (N + 1).
 *
 * T = - (hbar^2 / 2m) d^2/dx^2 tridiagonale (differenze finite centrate 2 ordine).
 * Autovalori analitici: E_n = n^2 * pi^2 * hbar^2 / (2 * m * L^2), n = 1, 2, ...
 * ma SOLO per i primi n (tipicamente fino a circa N/4) -- l'errore di
 * discretizzazione cresce visibilmente con n, come per HarmonicOscillator.
 */
public final class ParticleInABox
{
	private ParticleInABox() {}

	/**
	 * Unita' naturali: m = 1.0, hbar = 1.0.
	 */
	public static Hamiltonian<Real> hamiltonian(int N, double L) {
		return hamiltonian(N, L, 1.0, 1.0);
	}

	/**
	 * Costruisce l'Hamiltoniana consentendo di specificare massa e costante di Planck ridotta.
	 */
	public static Hamiltonian<Real> hamiltonian(int N, double L, double m, double hbar) {
		if (N <= 0) {
			throw new IllegalArgumentException("Il numero di punti di griglia N deve essere > 0");
		}
		if (L <= 0.0) {
			throw new IllegalArgumentException("La larghezza della buca L deve essere > 0");
		}

		RealField R = RealField.INSTANCE;
		double dx = L / (N + 1);
		double factor = (hbar * hbar) / (2.0 * m * dx * dx);

		Real zero = R.zero();
		Real diag = new Real(2.0 * factor);
		Real offDiag = new Real(-factor);

		Real[] data = new Real[N * N];
		for (int i = 0; i < N; i++) {
			int rowOffset = i * N;
			for (int j = 0; j < N; j++) {
				if (i == j) {
					data[rowOffset + j] = diag;
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
