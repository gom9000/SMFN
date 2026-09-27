package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Catalogo condiviso di valori {@link SquareMatrix}, sullo stesso schema delle catalog class
 * di {@code numerics}/{@code polynomial}/{@code vectors}: nessun assert qui dentro, solo dati.
 * <p>
 * Tutte le matrici hanno la stessa dimensione ({@link #N}), dato che add()/multiply()
 * richiedono dimensioni compatibili. I componenti sono {@link Real}: come per Vector, la
 * virgola mobile non lancia mai per overflow, quindi non serve isolare un sottoinsieme
 * "sicuro per combinazione" come per Rational -- basta la consueta tolleranza.
 */
final class SquareMatrixTestValues
{
	private SquareMatrixTestValues() {}

	static final int N = 3;
	static final RealField R = RealField.INSTANCE;
	static final SquareMatrixAlgebra<Real, RealField> RING = new SquareMatrixAlgebra<>(R, N);

	private static SquareMatrix<Real> m(double... values) {
		return SquareMatrixElementFactory.of(R, values);
	}

	static List<SquareMatrix<Real>> standardValues() {
		return List.of(
			RING.zero(),
			RING.one(),
			m(2, 0, 0, 0, 3, 0, 0, 0, 4),              // diagonale, invertibile, det=24
			m(1, 2, 3, 0, 1, 4, 5, 6, 0),                 // generica invertibile, det=1
			m(1, 2, 3, 4, 5, 6, 7, 8, 9),                   // classica singolare, det=0
			m(2, 1, 0, 1, 2, 1, 0, 1, 2),                     // simmetrica, invertibile, det=4
			m(0, 1, 0, 1, 0, 0, 0, 0, 1),                      // matrice di permutazione (ortogonale), det=-1
			m(-1, 2, -3, 4, -5, 6, -7, 8, -9)                   // valori negativi generici
		);
	}

	static List<SquareMatrix<Real>> extremeValues() {
		return List.of(
			m(1e308, 0, 0, 0, 1, 0, 0, 0, 1),
			m(1e-308, 0, 0, 0, 1, 0, 0, 0, 1),
			m(Double.MAX_VALUE, 0, 0, 0, 1, 0, 0, 0, 1),
			m(-1e308, 0, 0, 0, -1e308, 0, 0, 0, 1)
		);
	}

	static List<SquareMatrix<Real>> allValues() {
		List<SquareMatrix<Real>> all = new ArrayList<>(standardValues());
		all.addAll(extremeValues());
		return all;
	}

	/** Sottoinsieme di standardValues() con determinante non nullo (utile per test su inverse()/isInvertible()). */
	static List<SquareMatrix<Real>> invertibleStandardValues() {
		List<SquareMatrix<Real>> result = new ArrayList<>();
		for (SquareMatrix<Real> mat : standardValues()) {
			if (!R.isZero(RING.determinant(mat))) {
				result.add(mat);
			}
		}
		return result;
	}
}
