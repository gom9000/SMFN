package net.gommagomma.smfn.math.linearalgebra.operators;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

/**
 * Catalogo condiviso di matrici quadrate complesse per gli invarianti di HouseholderQRDecomposition
 * e HessenbergReduction, sullo stesso spirito delle altre catalog class del progetto: nessun
 * assert qui dentro, solo dati.
 * <p>
 * A differenza dei singoli casi hardcoded nei *Test esistenti (una sola matrice 3x3/4x4), qui si
 * accumula un piccolo insieme di matrici di dimensioni e strutture diverse (reali incorporate in
 * Complex, genericamente complesse, gia' upper Hessenberg, con una colonna nulla sotto la
 * sottodiagonale) su cui verificare le stesse proprieta' -- Q*R == A, Q unitaria, similitudine
 * corretta -- in modo sistematico invece che una tantum.
 */
final class OperatorTestValues
{
	private OperatorTestValues() {}

	private static final ComplexField C = ComplexField.INSTANCE;

	private static Complex c(double re, double im) {
		return new Complex(re, im);
	}

	static List<SquareMatrix<Complex>> matrices() {
		return List.of(
			// 2x2 reale incorporata in Complex (rotazione di 90 gradi)
			SquareMatrixElementFactory.of(C,
				c(0, 0), c(-1, 0),
				c(1, 0), c(0, 0)),

			// 3x3 genericamente complessa
			SquareMatrixElementFactory.of(C,
				c(2, 1), c(0, -1), c(1, 0),
				c(1, 0), c(3, 0), c(0, 2),
				c(0, 1), c(1, -1), c(4, 0)),

			// 4x4 genericamente complessa
			SquareMatrixElementFactory.of(C,
				c(4, 1), c(1, -1), c(0, 2), c(2, 0),
				c(1, 0), c(3, 2), c(1, 1), c(0, -1),
				c(2, -1), c(0, 1), c(5, 0), c(1, 0),
				c(0, 1), c(2, 0), c(1, -2), c(2, 1)),

			// 3x3 gia' upper Hessenberg
			SquareMatrixElementFactory.of(C,
				c(2, 0), c(1, 1), c(0, -1),
				c(3, 0), c(4, 0), c(1, 0),
				c(0, 0), c(2, 0), c(5, 0)),

			// 3x3 reale, simmetrica, incorporata in Complex
			SquareMatrixElementFactory.of(C,
				c(2, 0), c(1, 0), c(0, 0),
				c(1, 0), c(2, 0), c(1, 0),
				c(0, 0), c(1, 0), c(2, 0)),

			// 5x5 diagonale (caso degenere: gia' triangolare/Hessenberg, autovalori ovvi)
			SquareMatrixElementFactory.of(C,
				c(1, 0), c(0, 0), c(0, 0), c(0, 0), c(0, 0),
				c(0, 0), c(2, 0), c(0, 0), c(0, 0), c(0, 0),
				c(0, 0), c(0, 0), c(3, 0), c(0, 0), c(0, 0),
				c(0, 0), c(0, 0), c(0, 0), c(4, 0), c(0, 0),
				c(0, 0), c(0, 0), c(0, 0), c(0, 0), c(5, 0))
		);
	}
}
