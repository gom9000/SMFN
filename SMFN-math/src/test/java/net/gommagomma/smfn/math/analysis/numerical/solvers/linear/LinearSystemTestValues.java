package net.gommagomma.smfn.math.analysis.numerical.solvers.linear;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Catalogo condiviso di sistemi lineari Ax = b con soluzione nota, sullo stesso spirito delle
 * catalog class del resto del progetto: nessun assert qui dentro, solo dati.
 * <p>
 * Ogni {@link WellConditionedCase} porta la matrice A, il termine noto b e la soluzione attesa
 * x* (verificata a mano/per sostituzione, indipendentemente dal codice). Le {@link #singularCases()}
 * sono invece matrici singolari per cui GaussianEliminationSolver deve rifiutarsi di produrre una
 * soluzione instabile, sollevando ArithmeticException.
 */
final class LinearSystemTestValues
{
	private LinearSystemTestValues() {}

	private static final RealField R = RealField.INSTANCE;

	static final class WellConditionedCase
	{
		final String name;
		final SquareMatrix<Real> a;
		final Vector<Real> b;
		final Vector<Real> expectedSolution;

		WellConditionedCase(String name, SquareMatrix<Real> a, Vector<Real> b, Vector<Real> expectedSolution) {
			this.name = name;
			this.a = a;
			this.b = b;
			this.expectedSolution = expectedSolution;
		}

		@Override
		public String toString() { return name; }
	}

	private static Vector<Real> v(double... values) {
		return VectorElementFactory.of(new VectorSpace<>(R, values.length), values);
	}

	static List<WellConditionedCase> wellConditionedCases() {
		return List.of(
			new WellConditionedCase("2x2: x+y=3, x-y=1 -> (2,1)",
				SquareMatrixElementFactory.of(R, 1.0, 1.0, 1.0, -1.0),
				v(3.0, 1.0),
				v(2.0, 1.0)),

			new WellConditionedCase("2x2: identita' -> b stesso",
				SquareMatrixElementFactory.of(R, 1.0, 0.0, 0.0, 1.0),
				v(5.0, -3.0),
				v(5.0, -3.0)),

			new WellConditionedCase("3x3 generico: soluzione (1,-2,-2) verificata per sostituzione",
				SquareMatrixElementFactory.of(R,
					3.0,  2.0, -1.0,
					2.0, -2.0,  4.0,
				   -1.0,  0.5, -1.0),
				v(1.0, -2.0, 0.0),
				v(1.0, -2.0, -2.0)),

			new WellConditionedCase("3x3 diagonale: soluzione banale x_i = b_i/a_ii",
				SquareMatrixElementFactory.of(R, 2.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0, 0.0, -5.0),
				v(4.0, 8.0, -10.0),
				v(2.0, 2.0, 2.0)),

			new WellConditionedCase("3x3 con pivot nullo in prima posizione: richiede scambio di righe (partial pivoting)",
				SquareMatrixElementFactory.of(R,
					0.0, 1.0, 1.0,
					2.0, 3.0, 1.0,
					1.0, 1.0, 2.0),
				// det = -4 (non singolare); b costruito a partire dalla soluzione nota x=1,y=2,z=3
				v(5.0, 11.0, 9.0),
				v(1.0, 2.0, 3.0)),

			new WellConditionedCase("4x4 generico (tridiagonale), soluzione intera nota",
				SquareMatrixElementFactory.of(R,
					2.0, 1.0, 0.0, 0.0,
					1.0, 3.0, 1.0, 0.0,
					0.0, 1.0, 4.0, 1.0,
					0.0, 0.0, 1.0, 2.0),
				// b = A*(1,1,1,1), costruito per moltiplicazione diretta (non per ipotesi): niente
				// da "risolvere a mano", la soluzione attesa e' garantita per costruzione.
				v(3.0, 5.0, 6.0, 3.0),
				v(1.0, 1.0, 1.0, 1.0)),

			new WellConditionedCase("Vettore b nullo: unica soluzione e' x=0 (sistema non singolare)",
				SquareMatrixElementFactory.of(R, 2.0, 1.0, 1.0, 3.0),
				v(0.0, 0.0),
				v(0.0, 0.0))
		);
	}

	static List<SquareMatrix<Real>> singularCases() {
		return List.of(
			// Riga3 = combinazione lineare di riga1 e riga2.
			SquareMatrixElementFactory.of(R,
				1.0, 2.0, 3.0,
				2.0, 4.0, 6.0,
				1.0, 0.0, 1.0),
			// Matrice nulla.
			SquareMatrixElementFactory.of(R, 0.0, 0.0, 0.0, 0.0),
			// Due righe identiche.
			SquareMatrixElementFactory.of(R,
				1.0, 1.0, 1.0,
				2.0, 2.0, 2.0,
				3.0, 1.0, 0.0)
		);
	}
}
