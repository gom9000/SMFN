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
		Hamiltonian<Complex> hamiltonian = new Hamiltonian<>(H);

		StationaryStates result = hamiltonian.findStationaryStates(params);
		List<Real> energyLevels = result.getEnergyLevels();

		assertEquals(2, energyLevels.size());
		double v0 = energyLevels.get(0).getValue();
		double v1 = energyLevels.get(1).getValue();
		assertTrue((Math.abs(v0 - 1.0) < 1e-9 && Math.abs(v1 - 4.0) < 1e-9)
			|| (Math.abs(v0 - 4.0) < 1e-9 && Math.abs(v1 - 1.0) < 1e-9));
	}

	@Test
	@DisplayName("findStationaryStates(): gli stati corrispondono in ordine ai livelli energetici, H*v = E*v")
	void stationaryStatesSatisfyEigenequation() {
		SquareMatrix<Complex> H = M2.of(new Complex[] {
			new Complex(2, 0), new Complex(1, 1),
			new Complex(1, -1), new Complex(3, 0)
		});
		Hamiltonian<Complex> hamiltonian = new Hamiltonian<>(H);

		StationaryStates result = hamiltonian.findStationaryStates(params);
		List<Real> energyLevels = result.getEnergyLevels();
		List<QuantumState> states = result.getStates();

		assertEquals(energyLevels.size(), states.size());
		for (int i = 0; i < energyLevels.size(); i++) {
			Complex lambda = new Complex(energyLevels.get(i).getValue(), 0);
			net.gommagomma.smfn.math.linearalgebra.vectors.Vector<Complex> v = states.get(i).asVector();
			net.gommagomma.smfn.math.linearalgebra.vectors.Vector<Complex> Hv = H.apply(v);
			for (int k = 0; k < 2; k++) {
				Complex expected = C.multiply(lambda, v.get(k));
				assertEquals(expected.getRe(), Hv.get(k).getRe(), 1e-9);
				assertEquals(expected.getIm(), Hv.get(k).getIm(), 1e-9);
			}
		}
	}

	@Test
	@DisplayName("Hamiltonian e' un Observable: isHermitian() e asOperator() ereditati funzionano invariati")
	void hamiltonianIsAnObservable() {
		SquareMatrix<Complex> H = M2.of(new Complex[] {
			new Complex(1, 0), new Complex(0, 0),
			new Complex(0, 0), new Complex(-1, 0)
		});
		Hamiltonian<Complex> hamiltonian = new Hamiltonian<>(H);

		Observable<Complex> asObservable = hamiltonian; // upcast implicito, nessun cast esplicito necessario
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
		Hamiltonian<Complex> hamiltonian = new Hamiltonian<>(H);

		// Non deve lanciare: il costruttore accetta Observable, Hamiltonian lo e'.
		SchrodingerEquationSystem system = new SchrodingerEquationSystem(hamiltonian);
		assertTrue(system != null);
	}

	@Test
	@DisplayName("Hamiltonian<Real>: findStationaryStates() su H=[[2,1],[1,3]], autovalori calcolati a mano")
	void findsStationaryStatesForRealHamiltonian() {
		net.gommagomma.smfn.math.algebra.structures.RealField R = net.gommagomma.smfn.math.algebra.structures.RealField.INSTANCE;
		net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix<Real> H_real =
			net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory.of(R, 2.0, 1.0, 1.0, 3.0);
		Hamiltonian<Real> hamiltonianReal = new Hamiltonian<>(H_real);

		StationaryStates result = hamiltonianReal.findStationaryStates(params);
		List<Real> energyLevels = result.getEnergyLevels();

		// tr=5, det=5 -> lambda = (5 +- sqrt(5)) / 2, calcolato a mano
		double expected1 = (5 - Math.sqrt(5)) / 2;
		double expected2 = (5 + Math.sqrt(5)) / 2;
		assertEquals(2, energyLevels.size());
		double v0 = energyLevels.get(0).getValue();
		double v1 = energyLevels.get(1).getValue();
		assertTrue((Math.abs(v0 - expected1) < 1e-9 && Math.abs(v1 - expected2) < 1e-9)
			|| (Math.abs(v0 - expected2) < 1e-9 && Math.abs(v1 - expected1) < 1e-9));
	}

	@Test
	@DisplayName("Observable.toComplex(): ponte da Observable<Real> a Observable<Complex>, parte immaginaria zero")
	void toComplexBridgesRealToComplex() {
		net.gommagomma.smfn.math.algebra.structures.RealField R = net.gommagomma.smfn.math.algebra.structures.RealField.INSTANCE;
		net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix<Real> H_real =
			net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory.of(R, 2.0, 1.0, 1.0, 3.0);
		Observable<Real> observableReal = new Observable<>(H_real);

		Observable<Complex> observableComplex = Observable.toComplex(observableReal);

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				Complex c = observableComplex.asOperator().get(i, j);
				assertEquals(H_real.get(i, j).getValue(), c.getRe(), 1e-12);
				assertEquals(0.0, c.getIm(), 1e-12);
			}
		}

		// Il risultato deve funzionare come Observable<Complex> vero, es. costruire un sistema di Schrodinger
		SchrodingerEquationSystem system = new SchrodingerEquationSystem(observableComplex);
		assertTrue(system != null);
	}
}
