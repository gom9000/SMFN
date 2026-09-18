package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

@DisplayName("SquareMatrix<K>: comportamento dell'elemento, incluso LinearOperator<Vector<K>>")
class SquareMatrixTest
{
	private final ComplexField C = ComplexField.INSTANCE;
	private final SquareMatrixRing<Complex, ComplexField> M2 = new SquareMatrixRing<>(C, 2);
	private final VectorSemimodule<Complex, ComplexField> V2 = new VectorSemimodule<>(C, 2);

	// Rotazione di 90 gradi: [[0,-1],[1,0]]
	private final SquareMatrix<Complex> rotation90 = M2.of(new Complex[] {
		new Complex(0, 0), new Complex(-1, 0),
		new Complex(1, 0), new Complex(0, 0)
	});

	private Vector<Complex> v(double re0, double im0, double re1, double im1) {
		return V2.of(new Complex[] { new Complex(re0, im0), new Complex(re1, im1) });
	}

	@Test
	@DisplayName("TensorElement: rank 2, shape = [n,n]")
	void tensorProperties() {
		assertEquals(2, rotation90.rank());
		assertArrayEquals(new int[] { 2, 2 }, rotation90.getShape());
	}

	@Test
	@DisplayName("trace()")
	void trace() {
		SquareMatrix<Complex> m = M2.of(new Complex[] {
			new Complex(1, 0), new Complex(2, 0),
			new Complex(3, 0), new Complex(4, 0)
		});
		assertTrue(C.areEqual(m.trace(), new Complex(5, 0)));
	}

	@Test
	@DisplayName("apply(): prodotto matrice-vettore")
	void applyRotatesVector() {
		Vector<Complex> unitX = v(1, 0, 0, 0);
		Vector<Complex> rotated = rotation90.apply(unitX);
		assertTrue(C.areEqual(rotated.get(0), new Complex(0, 0)));
		assertTrue(C.areEqual(rotated.get(1), new Complex(1, 0)));
	}

	@Test
	@DisplayName("compose() (ereditato da Mapping) coincide con il prodotto tra matrici")
	void composeMatchesMatrixProduct() {
		Vector<Complex> x = v(1, 0, 0, 0);
		Vector<Complex> viaCompose = rotation90.compose(rotation90).apply(x);
		Vector<Complex> viaProduct = M2.multiply(rotation90, rotation90).apply(x);
		assertTrue(viaCompose.equals(viaProduct));
	}

	@Test
	@DisplayName("power(4) (ereditato da Operator): quattro rotazioni di 90 tornano all'identita'")
	void powerOfFourReturnsToStart() {
		Vector<Complex> x = v(1, 0, 0, 0);
		Vector<Complex> afterFour = rotation90.power(4).apply(x);
		assertTrue(C.areEqual(afterFour.get(0), new Complex(1, 0)));
		assertTrue(C.areEqual(afterFour.get(1), new Complex(0, 0)));
	}

	@Test
	@DisplayName("copy()/equals()/hashCode()")
	void copyAndEquality() {
		SquareMatrix<Complex> copy = rotation90.copy();
		assertEquals(rotation90, copy);
		assertEquals(rotation90.hashCode(), copy.hashCode());
	}

	@Test
	@DisplayName("conjugateTranspose(): scambia indici e coniuga ogni elemento")
	void conjugateTransposeSwapsAndConjugates() {
		SquareMatrix<Complex> m = M2.of(new Complex[] {
			new Complex(1, 1), new Complex(2, -2),
			new Complex(3, 0), new Complex(4, 5)
		});
		SquareMatrix<Complex> mDagger = m.conjugateTranspose();

		// M^dagger[i][j] = conj(M[j][i])
		assertTrue(C.areEqual(mDagger.get(0, 0), new Complex(1, -1)));
		assertTrue(C.areEqual(mDagger.get(0, 1), new Complex(3, 0)));
		assertTrue(C.areEqual(mDagger.get(1, 0), new Complex(2, 2)));
		assertTrue(C.areEqual(mDagger.get(1, 1), new Complex(4, -5)));
	}

	@Test
	@DisplayName("isHermitian(): vero per Pauli-X, falso per una matrice asimmetrica")
	void isHermitianDistinguishesCorrectly() {
		SquareMatrix<Complex> pauliX = M2.of(new Complex[] {
			new Complex(0, 0), new Complex(1, 0),
			new Complex(1, 0), new Complex(0, 0)
		});
		SquareMatrix<Complex> notHermitian = M2.of(new Complex[] {
			new Complex(1, 0), new Complex(1, 0),
			new Complex(0, 0), new Complex(1, 0)
		});

		assertTrue(pauliX.isHermitian());
		assertTrue(!notHermitian.isHermitian());
	}

	@Test
	@DisplayName("isHermitian(): una matrice reale simmetrica e' hermitiana (coniugazione e' l'identita' su Real)")
	void realSymmetricMatrixIsHermitian() {
		SquareMatrix<Complex> realSymmetric = M2.of(new Complex[] {
			new Complex(2, 0), new Complex(7, 0),
			new Complex(7, 0), new Complex(-1, 0)
		});
		assertTrue(realSymmetric.isHermitian());
	}
}
