package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

@DisplayName("CentralDifferenceJacobianEstimator: stima della Jacobiana intera per differenze centrali")
class CentralDifferenceJacobianEstimatorTest
{
	private static final RealField R = RealField.INSTANCE;
	private final VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(R, 2);
	private final CentralDifferenceJacobianEstimator<Real, RealField> estimator =
		new CentralDifferenceJacobianEstimator<>(R, new Real(1e-6));

	private Vector<Real> point(double x, double y) {
		return V2.of(new Real[] { new Real(x), new Real(y) });
	}

	@Test
	@DisplayName("F(x,y) = (x^2+y^2, x*y): Jacobiana attesa (2x,2y; y,x), calcolata a mano")
	void estimatesKnownNonlinearJacobian() {
		Mapping<Vector<Real>, Vector<Real>> F = v -> {
			Real x = v.get(0), y = v.get(1);
			Real f1 = R.add(R.multiply(x, x), R.multiply(y, y));
			Real f2 = R.multiply(x, y);
			return V2.of(new Real[] { f1, f2 });
		};

		SquareMatrix<Real> jacobian = estimator.estimateAt(F, point(3.0, 4.0));

		// Jacobiana vera in (3,4): [[6,8],[4,3]]
		assertEquals(6.0, jacobian.get(0, 0).getValue(), 1e-4);
		assertEquals(8.0, jacobian.get(0, 1).getValue(), 1e-4);
		assertEquals(4.0, jacobian.get(1, 0).getValue(), 1e-4);
		assertEquals(3.0, jacobian.get(1, 1).getValue(), 1e-4);
	}

	@Test
	@DisplayName("F lineare: la differenza centrale non ha errore di troncamento, la stima e' quasi esatta")
	void estimatesLinearJacobianNearlyExactly() {
		// F(x,y) = (2x+3y, x-y) -- Jacobiana costante [[2,3],[1,-1]] ovunque
		Mapping<Vector<Real>, Vector<Real>> F = v -> {
			Real x = v.get(0), y = v.get(1);
			Real f1 = R.add(R.multiply(new Real(2.0), x), R.multiply(new Real(3.0), y));
			Real f2 = R.subtract(x, y);
			return V2.of(new Real[] { f1, f2 });
		};

		SquareMatrix<Real> jacobian = estimator.estimateAt(F, point(10.0, -5.0));

		assertEquals(2.0, jacobian.get(0, 0).getValue(), 1e-7);
		assertEquals(3.0, jacobian.get(0, 1).getValue(), 1e-7);
		assertEquals(1.0, jacobian.get(1, 0).getValue(), 1e-7);
		assertEquals(-1.0, jacobian.get(1, 1).getValue(), 1e-7);
	}

	@Test
	@DisplayName("Il costruttore rifiuta un passo h nullo o zero")
	void constructorRejectsZeroOrNullStep() {
		assertThrows(IllegalArgumentException.class, () -> new CentralDifferenceJacobianEstimator<>(R, new Real(0.0)));
		assertThrows(IllegalArgumentException.class, () -> new CentralDifferenceJacobianEstimator<Real, RealField>(R, null));
	}
}
