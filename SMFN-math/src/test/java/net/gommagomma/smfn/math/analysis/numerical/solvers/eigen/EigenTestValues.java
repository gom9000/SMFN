package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

/**
 * Catalogo condiviso di matrici quadrate per gli invarianti di GeneralEigenvalueSolver
 * (che instrada a JacobiEigenvalueSolver/HermitianEigenvalueSolver per matrici
 * simmetriche/hermitiane, e a RealQREigenvalueSolver/ComplexQREigenvalueSolver per il caso
 * generico), sullo stesso schema delle catalog class del resto del progetto: nessun assert
 * qui dentro, solo dati.
 * <p>
 * A differenza dei cataloghi di root-finding, qui non serve conoscere gli autovalori esatti a
 * mano per ogni caso: gli invarianti "traccia == somma degli autovalori" e "determinante ==
 * prodotto degli autovalori" sono verificabili confrontando col determinante/traccia calcolati
 * indipendentemente da SquareMatrix/SquareMatrixAlgebra, quindi il catalogo include anche
 * matrici il cui spettro non e' banale da calcolare a mano (es. non simmetriche con autovalori
 * complessi coniugati).
 */
final class EigenTestValues
{
	private EigenTestValues() {}

	private static final RealField R = RealField.INSTANCE;
	private static final ComplexField C = ComplexField.INSTANCE;

	/** Matrici reali simmetriche: autovalori garantiti reali, autovettori ortogonali. */
	static List<SquareMatrix<Real>> realSymmetricMatrices() {
		return List.of(
			SquareMatrixElementFactory.of(R, 2.0, 1.0, 1.0, 2.0),
			SquareMatrixElementFactory.of(R, 5.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 9.0),
			SquareMatrixElementFactory.of(R, 2.0, 1.0, 0.0, 1.0, 2.0, 1.0, 0.0, 1.0, 2.0),
			SquareMatrixElementFactory.of(R, 4.0, -2.0, -2.0, 4.0),
			SquareMatrixElementFactory.of(R, 0.0, 0.0, 0.0, 0.0) // caso degenere: tutti gli autovalori nulli
		);
	}

	/** Matrici reali NON simmetriche: caso generico, spettro anche complesso (coppie coniugate). */
	static List<SquareMatrix<Real>> realGeneralMatrices() {
		return List.of(
			SquareMatrixElementFactory.of(R, 1.0, 2.0, 3.0, 4.0),      // autovalori reali distinti: (5+-sqrt(33))/2
			SquareMatrixElementFactory.of(R, 0.0, -1.0, 1.0, 0.0),      // rotazione di 90 gradi: autovalori +-i
			SquareMatrixElementFactory.of(R, 2.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 4.0), // triangolare/diagonale: gia' generico ma con autovalori ovvi
			SquareMatrixElementFactory.of(R, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0)   // triangolare superiore: autovalore triplo 1 (difettivo)
		);
	}

	/** Matrici complesse hermitiane: autovalori garantiti reali. */
	static List<SquareMatrix<Complex>> complexHermitianMatrices() {
		return List.of(
			SquareMatrixElementFactory.of(C,
				new Complex(2, 0), new Complex(1, 1),
				new Complex(1, -1), new Complex(3, 0)),
			SquareMatrixElementFactory.of(C,
				new Complex(1, 0), new Complex(0, 0),
				new Complex(0, 0), new Complex(1, 0)) // identita' complessa: autovalori 1,1
		);
	}

	/** Matrici complesse NON hermitiane: caso generico via ComplexQREigenvalueSolver. */
	static List<SquareMatrix<Complex>> complexGeneralMatrices() {
		return List.of(
			SquareMatrixElementFactory.of(C,
				new Complex(1, 0), new Complex(2, 3),
				new Complex(0, 0), new Complex(4, 0)) // triangolare superiore: autovalori esatti 1 e 4
		);
	}
}
