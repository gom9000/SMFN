package net.gommagomma.smfn.physics.mq;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

/**
 * Catalogo condiviso di osservabili e stati quantistici per gli invarianti del pacchetto
 * physics.mq, sullo stesso spirito delle altre catalog class del progetto: nessun assert qui
 * dentro, solo dati.
 * <p>
 * Le dimensioni sono tenute separate (2 e 3) perche' l'equazione agli autovalori e il valore di
 * aspettazione richiedono che osservabile e stato condividano la stessa dimensione dello spazio
 * di Hilbert.
 */
final class PhysicsTestValues
{
	private PhysicsTestValues() {}

	private static final ComplexField C = ComplexField.INSTANCE;
	private static final double INV_SQRT2 = 1.0 / Math.sqrt(2.0);

	/** Osservabili 2x2 hermitiani: le matrici di Pauli piu' una combinazione lineare (ancora hermitiana). */
	static List<Observable<Complex>> twoDimObservables() {
		return List.of(
			new Observable<>(Pauli.IDENTITY),
			new Observable<>(Pauli.SIGMA_X),
			new Observable<>(Pauli.SIGMA_Y),
			new Observable<>(Pauli.SIGMA_Z),
			new Observable<>(SquareMatrixElementFactory.of(C, // (X+Z)/1, ancora hermitiana
				new Complex(1, 0), new Complex(1, 0),
				new Complex(1, 0), new Complex(-1, 0)))
		);
	}

	/** Stati 2-dimensionali: base computazionale, autostati di X e Y, e uno stato non normalizzato. */
	static List<QuantumState> twoDimStates() {
		return List.of(
			QuantumState.of(new Complex(1, 0), new Complex(0, 0)),           // |0>
			QuantumState.of(new Complex(0, 0), new Complex(1, 0)),           // |1>
			QuantumState.of(new Complex(INV_SQRT2, 0), new Complex(INV_SQRT2, 0)),   // |+>
			QuantumState.of(new Complex(INV_SQRT2, 0), new Complex(-INV_SQRT2, 0)),  // |->
			QuantumState.of(new Complex(INV_SQRT2, 0), new Complex(0, INV_SQRT2)),   // |i> = (|0>+i|1>)/sqrt2
			QuantumState.of(new Complex(2, 0), new Complex(0, 0))                    // non normalizzato
		);
	}

	/** Un osservabile 3x3 hermitiano generico (non diagonale, con componenti complesse). */
	static Observable<Complex> threeDimObservable() {
		return new Observable<>(SquareMatrixElementFactory.of(C,
			new Complex(2, 0), new Complex(1, 1), new Complex(0, 0),
			new Complex(1, -1), new Complex(3, 0), new Complex(1, 0),
			new Complex(0, 0), new Complex(1, 0), new Complex(1, 0)));
	}

	/** Stati 3-dimensionali: base computazionale, sovrapposizione uniforme, e uno stato complesso. */
	static List<QuantumState> threeDimStates() {
		double invSqrt3 = 1.0 / Math.sqrt(3.0);
		return List.of(
			QuantumState.of(new Complex(1, 0), new Complex(0, 0), new Complex(0, 0)),
			QuantumState.of(new Complex(0, 0), new Complex(1, 0), new Complex(0, 0)),
			QuantumState.of(new Complex(0, 0), new Complex(0, 0), new Complex(1, 0)),
			QuantumState.of(new Complex(invSqrt3, 0), new Complex(invSqrt3, 0), new Complex(invSqrt3, 0)),
			QuantumState.of(new Complex(INV_SQRT2, 0), new Complex(0, INV_SQRT2), new Complex(0, 0))
		);
	}
}
