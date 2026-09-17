package net.gommagomma.smfn.math.linearalgebra.vectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.utils.MathConstants;

@DisplayName("InnerProductVectorSpace: prodotto interno e norma")
class InnerProductVectorSpaceTest
{
	private static final double EPSILON = MathConstants.EPSILON;
	private final RealField R = RealField.INSTANCE;
	private final ComplexField C = ComplexField.INSTANCE;

	@Test
	@DisplayName("Scalari reali: prodotto interno = prodotto scalare usuale, nessuna coniugazione")
	void realInnerProductIsPlainDotProduct() {
		InnerProductVectorSpace<Real, RealField> space = new InnerProductVectorSpace<>(R, 2);
		Vector<Real> v = space.of(new Real[] { new Real(3.0), new Real(4.0) });

		Real innerProduct = space.innerProduct(v, v);
		assertTrue(R.areEqual(innerProduct, new Real(25.0))); // 3^2 + 4^2

		Real norm = space.norm(v);
		assertTrue(Math.abs(norm.getValue() - 5.0) < EPSILON); // norma = sqrt(25) = 5
	}

	@Test
	@DisplayName("Scalari complessi: il prodotto interno e' hermitiano, <a,b> = coniugato di <b,a>")
	void complexInnerProductIsHermitian() {
		InnerProductVectorSpace<Complex, ComplexField> space = new InnerProductVectorSpace<>(C, 2);
		Vector<Complex> a = space.of(new Complex[] { new Complex(1, 1), new Complex(0, 2) });
		Vector<Complex> b = space.of(new Complex[] { new Complex(2, 0), new Complex(1, -1) });

		Complex ab = space.innerProduct(a, b);
		Complex ba = space.innerProduct(b, a);

		assertTrue(C.areEqual(ab, ba.conjugate()), "<a,b> deve essere il coniugato di <b,a>");
	}

	@Test
	@DisplayName("Scalari complessi: <v,v> e' sempre reale (parte immaginaria nulla) e non negativo")
	void complexSelfInnerProductIsRealAndNonNegative() {
		InnerProductVectorSpace<Complex, ComplexField> space = new InnerProductVectorSpace<>(C, 2);
		Vector<Complex> v = space.of(new Complex[] { new Complex(3, 4), new Complex(1, -2) });

		Complex selfProduct = space.innerProduct(v, v);
		assertTrue(Math.abs(selfProduct.getIm()) < EPSILON, "<v,v> deve avere parte immaginaria nulla");
		assertTrue(selfProduct.getRe() >= 0.0, "<v,v> deve essere non negativo");
	}
}
