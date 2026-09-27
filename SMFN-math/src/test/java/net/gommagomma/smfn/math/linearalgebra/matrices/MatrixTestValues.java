package net.gommagomma.smfn.math.linearalgebra.matrices;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Catalogo condiviso di valori {@link Matrix} (rettangolari, non quadrate), sullo stesso
 * schema delle catalog class del resto del progetto: nessun assert qui dentro, solo dati.
 * <p>
 * Tutte le matrici sono {@link #ROWS} x {@link #COLS}, dimensione fissa (add()/innerProduct()
 * richiedono dimensioni compatibili). I componenti sono {@link Real}: come per Vector e
 * SquareMatrix, la virgola mobile non lancia mai per overflow.
 */
final class MatrixTestValues
{
	private MatrixTestValues() {}

	static final int ROWS = 2;
	static final int COLS = 3;
	static final RealField R = RealField.INSTANCE;
	static final InnerProductMatrixSpace<Real, RealField> SPACE = new InnerProductMatrixSpace<>(R, ROWS, COLS);

	private static Matrix<Real> m(double... values) {
		return MatrixElementFactory.of(SPACE, values);
	}

	static List<Matrix<Real>> standardValues() {
		return List.of(
			SPACE.zero(),
			m(1, 0, 0, 0, 1, 0),          // proiezione canonica, rango pieno (2)
			m(1, 2, 3, 4, 5, 6),           // generica, rango pieno (2)
			m(1, 2, 3, 2, 4, 6),            // riga2 = 2*riga1: rango carente (1)
			m(-1, -2, -3, 4, 5, 6)
		);
	}

	static List<Matrix<Real>> extremeValues() {
		return List.of(
			m(1e308, 0, 0, 0, 1, 0),
			m(1e-308, 0, 0, 0, 1, 0),
			m(Double.MAX_VALUE, 0, 0, 0, 1, 0)
		);
	}

	static List<Matrix<Real>> allValues() {
		List<Matrix<Real>> all = new ArrayList<>(standardValues());
		all.addAll(extremeValues());
		return all;
	}
}
