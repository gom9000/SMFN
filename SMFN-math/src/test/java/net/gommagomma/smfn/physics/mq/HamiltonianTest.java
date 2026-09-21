package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;

@DisplayName("Hamiltonian: Observable specializzato per l'energia, con stati stazionari")
class HamiltonianTest
{
	private static final ComplexField C = ComplexField.INSTANCE;
	private final SquareMatrixRing<Complex, ComplexField> M2 = new SquareMatrixRing<>(C, 2);
	private final ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 100);

	@Test
	@DisplayName("findStationaryStates(): H=[[2,1+i],[1-i,3]], autovalori 1 e 4 (verificati a mano e con numpy)")
	void findsKnownStationaryStates() {
		SquareMatrix<Complex> H = M2.of(new Complex[] {
			new Complex(2, 0), new Complex(1, 1),
			new Complex(1, -1), new Complex(3, 0)
		});
		Hamiltonian hamiltonian = new Hamiltonian(H);

		EigenDecomposition result = hamiltonian.findStationaryStates(params);
		List<Complex> eigenvalues = result.getEigenvalues();

		assertEquals(2, eigenvalues.size());
		double v0 = eigenvalues.get(0).getRe();
		double v1 = eigenvalues.get(1).getRe();
		assertTrue((Math.abs(v0 - 1.0) < 1e-9 && Math.abs(v1 - 4.0) < 1e-9)
			|| (Math.abs(v0 - 4.0) < 1e-9 && Math.abs(v1 - 1.0) < 1e-9));
	}

	@Test
	@DisplayName("Hamiltonian e' un Observable: isHermitian() e asOperator() ereditati funzionano invariati")
	void hamiltonianIsAnObservable() {
		SquareMatrix<Complex> H = M2.of(new Complex[] {
			new Complex(1, 0), new Complex(0, 0),
			new Complex(0, 0), new Complex(-1, 0)
		});
		Hamiltonian hamiltonian = new Hamiltonian(H);

		Observable asObservable = hamiltonian; // upcast implicito, nessun cast esplicito necessario
		assertTrue(asObservable.asOperator().isHermitian());
		assertEquals(H, asObservable.asOperator());
	}

	@Test
	@DisplayName("Hamiltonian e' sostituibile ovunque un Observable sia richiesto: SchrodingerEquationSystem")
	void hamiltonianUsableWhereObservableIsExpected() {
		SquareMatrix<Complex> H = M2.of(new Complex[] {
			new Complex(0, 0), new Complex(1, 0),
			new Complex(1, 0), new Complex(0, 0)
		});
		Hamiltonian hamiltonian = new Hamiltonian(H);

		// Non deve lanciare: il costruttore accetta Observable, Hamiltonian lo e'.
		SchrodingerEquationSystem system = new SchrodingerEquationSystem(hamiltonian);
		assertTrue(system != null);
	}
}
