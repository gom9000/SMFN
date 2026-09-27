package net.gommagomma.smfn.math.linearalgebra.vectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Invarianti algebrici di Vector/VectorSpace/InnerProductVectorSpace sul catalogo di
 * VectorTestValues, sullo stesso spirito delle *InvariantsTest di numerics/polynomial.
 * <p>
 * I componenti sono Real/Complex (virgola mobile): niente eccezioni di overflow da isolare
 * come per Rational, ma le disuguaglianze (Cauchy-Schwarz, triangolare) e l'omogeneita' della
 * norma restano verificate a tolleranza, e solo sul catalogo a magnitudine standard, per non
 * confondere un vero limite di cancellazione catastrofica (gia' documentato in
 * RealInvariantsTest) con un difetto di Vector.
 */
@DisplayName("Vector: invarianti algebrici sul catalogo di valori (standard + estremi)")
class VectorInvariantsTest
{
	private static final double EPSILON = 1e-9;

	private final InnerProductVectorSpace<Real, RealField> S = VectorTestValues.SPACE;
	private final InnerProductVectorSpace<Complex, ComplexField> SC = VectorTestValues.COMPLEX_SPACE;
	private final RealField R = RealField.INSTANCE;
	private final ComplexField C = ComplexField.INSTANCE;

	private static final List<Real> SCALARS = List.of(new Real(0), new Real(1), new Real(-1), new Real(2), new Real(-2), new Real(0.5), new Real(-3));

	@Test
	@DisplayName("Addizione commutativa: a+b == b+a, su tutte le coppie di valori standard")
	void additionIsCommutative() {
		List<Vector<Real>> values = VectorTestValues.standardValues();
		for (Vector<Real> a : values) {
			for (Vector<Real> b : values) {
				assertTrue(S.areEqual(S.add(a, b), S.add(b, a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Addizione associativa: (a+b)+c == a+(b+c), su terne di valori standard")
	void additionIsAssociative() {
		List<Vector<Real>> values = VectorTestValues.standardValues();
		for (Vector<Real> a : values) {
			for (Vector<Real> b : values) {
				for (Vector<Real> c : values) {
					Vector<Real> left = S.add(S.add(a, b), c);
					Vector<Real> right = S.add(a, S.add(b, c));
					assertTrue(S.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Zero e' identita' additiva su tutto il catalogo, estremi inclusi")
	void zeroIsAdditiveIdentity() {
		for (Vector<Real> v : VectorTestValues.allValues()) {
			assertTrue(S.areEqual(v, S.add(v, S.zero())), "v=" + v);
		}
	}

	@Test
	@DisplayName("Inverso additivo: v + (-v) == 0, esattamente (cancellazione IEEE754 esatta), su tutto il catalogo, estremi inclusi")
	void additiveInverseReturnsZero() {
		for (Vector<Real> v : VectorTestValues.allValues()) {
			assertTrue(S.areEqual(S.zero(), S.add(v, S.negate(v))), "v=" + v);
		}
	}

	@Test
	@DisplayName("Doppia negazione: -(-v) == v, su tutto il catalogo, estremi inclusi")
	void doubleNegationReturnsOriginal() {
		for (Vector<Real> v : VectorTestValues.allValues()) {
			assertTrue(S.areEqual(v, S.negate(S.negate(v))), "v=" + v);
		}
	}

	@Test
	@DisplayName("1*v == v, su tutto il catalogo, estremi inclusi")
	void scalingByOneIsIdentity() {
		for (Vector<Real> v : VectorTestValues.allValues()) {
			assertTrue(S.areEqual(v, S.scale(R.one(), v)), "v=" + v);
		}
	}

	@Test
	@DisplayName("0*v == vettore nullo, su tutto il catalogo, estremi inclusi")
	void scalingByZeroGivesZeroVector() {
		for (Vector<Real> v : VectorTestValues.allValues()) {
			assertTrue(S.areEqual(S.zero(), S.scale(R.zero(), v)), "v=" + v);
		}
	}

	@Test
	@DisplayName("Distributiva rispetto alla somma di vettori: k*(a+b) == k*a + k*b, su valori standard")
	void scaleDistributesOverVectorAddition() {
		List<Vector<Real>> values = VectorTestValues.standardValues();
		for (Real k : SCALARS) {
			for (Vector<Real> a : values) {
				for (Vector<Real> b : values) {
					Vector<Real> left = S.scale(k, S.add(a, b));
					Vector<Real> right = S.add(S.scale(k, a), S.scale(k, b));
					assertTrue(S.areEqual(left, right), "k=" + k + " a=" + a + " b=" + b);
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva rispetto alla somma di scalari: (k1+k2)*v == k1*v + k2*v, su valori standard")
	void scaleDistributesOverScalarAddition() {
		List<Vector<Real>> values = VectorTestValues.standardValues();
		for (Real k1 : SCALARS) {
			for (Real k2 : SCALARS) {
				for (Vector<Real> v : values) {
					Vector<Real> left = S.scale(R.add(k1, k2), v);
					Vector<Real> right = S.add(S.scale(k1, v), S.scale(k2, v));
					assertTrue(S.areEqual(left, right), "k1=" + k1 + " k2=" + k2 + " v=" + v);
				}
			}
		}
	}

	@Test
	@DisplayName("Associativita' dello scaling: k1*(k2*v) == (k1*k2)*v, su valori standard")
	void scaleIsAssociativeWithScalarMultiplication() {
		List<Vector<Real>> values = VectorTestValues.standardValues();
		for (Real k1 : SCALARS) {
			for (Real k2 : SCALARS) {
				for (Vector<Real> v : values) {
					Vector<Real> left = S.scale(k1, S.scale(k2, v));
					Vector<Real> right = S.scale(R.multiply(k1, k2), v);
					assertTrue(S.areEqual(left, right), "k1=" + k1 + " k2=" + k2 + " v=" + v);
				}
			}
		}
	}

	@Test
	@DisplayName("Prodotto interno reale simmetrico: <a,b> == <b,a> (nessuna coniugazione per Real), su tutto il catalogo, estremi inclusi")
	void realInnerProductIsSymmetric() {
		List<Vector<Real>> values = VectorTestValues.allValues();
		for (Vector<Real> a : values) {
			for (Vector<Real> b : values) {
				assertTrue(R.areEqual(S.innerProduct(a, b), S.innerProduct(b, a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Prodotto interno lineare nel secondo argomento: <a,b+c> == <a,b>+<a,c>, su terne di valori standard")
	void innerProductIsLinearInSecondArgument() {
		List<Vector<Real>> values = VectorTestValues.standardValues();
		for (Vector<Real> a : values) {
			for (Vector<Real> b : values) {
				for (Vector<Real> c : values) {
					Real left = S.innerProduct(a, S.add(b, c));
					Real right = R.add(S.innerProduct(a, b), S.innerProduct(a, c));
					assertTrue(R.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Prodotto interno con uno scalare: <a, k*b> == k*<a,b>, su valori standard")
	void innerProductIsHomogeneousInSecondArgument() {
		List<Vector<Real>> values = VectorTestValues.standardValues();
		for (Real k : SCALARS) {
			for (Vector<Real> a : values) {
				for (Vector<Real> b : values) {
					Real left = S.innerProduct(a, S.scale(k, b));
					Real right = R.multiply(k, S.innerProduct(a, b));
					assertTrue(R.areEqual(left, right), "k=" + k + " a=" + a + " b=" + b);
				}
			}
		}
	}

	@Test
	@DisplayName("<v,v> non e' mai negativo, su tutto il catalogo, estremi inclusi")
	void selfInnerProductIsNonNegative() {
		for (Vector<Real> v : VectorTestValues.allValues()) {
			assertTrue(S.innerProduct(v, v).getValue() >= 0.0, "v=" + v);
		}
	}

	@Test
	@DisplayName("norm() non e' mai negativa, su tutto il catalogo, estremi inclusi")
	void normIsNeverNegative() {
		for (Vector<Real> v : VectorTestValues.allValues()) {
			assertTrue(S.norm(v).getValue() >= 0.0, "v=" + v);
		}
	}

	@Test
	@DisplayName("norm(0) == 0")
	void normOfZeroVectorIsZero() {
		assertEquals(0.0, S.norm(S.zero()).getValue(), 0.0);
	}

	@Test
	@DisplayName("Omogeneita' della norma: norm(k*v) == |k|*norm(v), a tolleranza, su valori standard")
	void normIsHomogeneous() {
		List<Vector<Real>> values = VectorTestValues.standardValues();
		for (Real k : SCALARS) {
			for (Vector<Real> v : values) {
				double left = S.norm(S.scale(k, v)).getValue();
				double right = Math.abs(k.getValue()) * S.norm(v).getValue();
				assertTrue(Math.abs(left - right) < EPSILON, "k=" + k + " v=" + v + " norm(k*v)=" + left + " |k|*norm(v)=" + right);
			}
		}
	}

	@Test
	@DisplayName("Disuguaglianza di Cauchy-Schwarz: |<a,b>| <= norm(a)*norm(b), a tolleranza, su coppie di valori standard")
	void cauchySchwarzInequalityHolds() {
		List<Vector<Real>> values = VectorTestValues.standardValues();
		for (Vector<Real> a : values) {
			for (Vector<Real> b : values) {
				double lhs = Math.abs(S.innerProduct(a, b).getValue());
				double rhs = S.norm(a).getValue() * S.norm(b).getValue();
				assertTrue(lhs <= rhs + EPSILON, "a=" + a + " b=" + b + " |<a,b>|=" + lhs + " norm(a)*norm(b)=" + rhs);
			}
		}
	}

	@Test
	@DisplayName("Disuguaglianza triangolare: norm(a+b) <= norm(a)+norm(b), a tolleranza, su coppie di valori standard")
	void triangleInequalityHolds() {
		List<Vector<Real>> values = VectorTestValues.standardValues();
		for (Vector<Real> a : values) {
			for (Vector<Real> b : values) {
				double lhs = S.norm(S.add(a, b)).getValue();
				double rhs = S.norm(a).getValue() + S.norm(b).getValue();
				assertTrue(lhs <= rhs + EPSILON, "a=" + a + " b=" + b + " norm(a+b)=" + lhs + " norm(a)+norm(b)=" + rhs);
			}
		}
	}

	@Test
	@DisplayName("Prodotto interno complesso: e' hermitiano, <a,b> == conj(<b,a>), su tutte le coppie di valori standard complessi")
	void complexInnerProductIsHermitian() {
		List<Vector<Complex>> values = VectorTestValues.complexStandardValues();
		for (Vector<Complex> a : values) {
			for (Vector<Complex> b : values) {
				Complex ab = SC.innerProduct(a, b);
				Complex ba = SC.innerProduct(b, a);
				assertTrue(C.areEqual(ab, ba.conjugate()), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Prodotto interno complesso con se stesso: <v,v> e' reale (parte immaginaria nulla) e non negativo, su tutto il catalogo complesso")
	void complexSelfInnerProductIsRealAndNonNegative() {
		for (Vector<Complex> v : VectorTestValues.complexStandardValues()) {
			Complex selfProduct = SC.innerProduct(v, v);
			assertTrue(Math.abs(selfProduct.getIm()) < 1e-9, "v=" + v + " <v,v>=" + selfProduct);
			assertTrue(selfProduct.getRe() >= -1e-9, "v=" + v + " <v,v>=" + selfProduct);
		}
	}

	@Test
	@DisplayName("Prodotto interno complesso lineare nel secondo argomento: <a,k*b> == k*<a,b>, su valori standard complessi")
	void complexInnerProductIsLinearInSecondArgument() {
		List<Vector<Complex>> values = VectorTestValues.complexStandardValues();
		Complex k = new Complex(2, -1);
		for (Vector<Complex> a : values) {
			for (Vector<Complex> b : values) {
				Complex left = SC.innerProduct(a, SC.scale(k, b));
				Complex right = C.multiply(k, SC.innerProduct(a, b));
				assertTrue(C.areEqual(left, right), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Prodotto interno complesso coniugato-lineare nel primo argomento: <k*a,b> == conj(k)*<a,b>, su valori standard complessi")
	void complexInnerProductIsConjugateLinearInFirstArgument() {
		List<Vector<Complex>> values = VectorTestValues.complexStandardValues();
		Complex k = new Complex(2, -1);
		for (Vector<Complex> a : values) {
			for (Vector<Complex> b : values) {
				Complex left = SC.innerProduct(SC.scale(k, a), b);
				Complex right = C.multiply(k.conjugate(), SC.innerProduct(a, b));
				assertTrue(C.areEqual(left, right), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Operazioni su vettori di dimensione diversa lanciano IllegalArgumentException")
	void operationsRejectMismatchedDimensions() {
		Vector<Real> v2 = VectorElementFactory.of(new InnerProductVectorSpace<>(RealField.INSTANCE, 2), 1.0, 2.0);
		Vector<Real> v3 = VectorTestValues.standardValues().get(0);
		assertThrows(IllegalArgumentException.class, () -> VectorTestValues.SPACE.add(v2, v3));
	}

	@Test
	@DisplayName("get() fuori range lancia IndexOutOfBoundsException, su tutto il catalogo")
	void getOutOfBoundsThrows() {
		for (Vector<Real> v : VectorTestValues.allValues()) {
			assertThrows(IndexOutOfBoundsException.class, () -> v.get(-1));
			assertThrows(IndexOutOfBoundsException.class, () -> v.get((int) v.size()));
		}
	}

	@Test
	@DisplayName("copy() restituisce un vettore uguale all'originale, su tutto il catalogo")
	void copyReturnsEquivalentVector() {
		for (Vector<Real> v : VectorTestValues.allValues()) {
			assertEquals(v, v.copy(), "v=" + v);
		}
	}

	@Test
	@DisplayName("rank()==1 e getShape() coincide con la dimensione, su tutto il catalogo")
	void shapeAndRankAreConsistent() {
		for (Vector<Real> v : VectorTestValues.allValues()) {
			assertEquals(1, v.rank(), "v=" + v);
			assertEquals(VectorTestValues.DIMENSION, v.getShape()[0], "v=" + v);
			assertEquals((long) VectorTestValues.DIMENSION, v.size(), "v=" + v);
		}
	}
}
