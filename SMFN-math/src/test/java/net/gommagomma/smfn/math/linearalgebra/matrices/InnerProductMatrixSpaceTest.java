package net.gommagomma.smfn.math.linearalgebra.matrices;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.utils.MathConstants;

@DisplayName("InnerProductMatrixSpace: prodotto interno e norma")
class InnerProductMatrixSpaceTest
{
	private static final double EPSILON = MathConstants.EPSILON;
	private final RealField R = RealField.INSTANCE;
	private final ComplexField C = ComplexField.INSTANCE;

	@Test
	@DisplayName("Scalari reali: norma di Frobenius")
	void realFrobeniusNorm() {
		InnerProductMatrixSpace<Real, RealField> space = new InnerProductMatrixSpace<>(R, 2, 2);
		Matrix<Real> m = space.of(new Real[] { new Real(3.0), new Real(0.0), new Real(0.0), new Real(4.0) });

		Real norm = space.norm(m);
		assertTrue(Math.abs(norm.getValue() - 5.0) < EPSILON); // sqrt(3^2+4^2) = 5
	}

	@Test
	@DisplayName("Scalari complessi: il prodotto interno e' hermitiano")
	void complexInnerProductIsHermitian() {
		InnerProductMatrixSpace<Complex, ComplexField> space = new InnerProductMatrixSpace<>(C, 2, 1);
		Matrix<Complex> a = space.of(new Complex[] { new Complex(1, 1), new Complex(0, 2) });
		Matrix<Complex> b = space.of(new Complex[] { new Complex(2, 0), new Complex(1, -1) });

		Complex ab = space.innerProduct(a, b);
		Complex ba = space.innerProduct(b, a);
		assertTrue(C.areEqual(ab, ba.conjugate()));
	}
}
