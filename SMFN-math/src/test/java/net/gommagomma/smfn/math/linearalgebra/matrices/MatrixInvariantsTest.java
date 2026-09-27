package net.gommagomma.smfn.math.linearalgebra.matrices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Invarianti algebrici di Matrix/MatrixSpace/InnerProductMatrixSpace (matrici rettangolari,
 * non quadrate) sul catalogo di MatrixTestValues, sullo stesso spirito delle *InvariantsTest
 * di SquareMatrix/Vector. Qui non ci sono determinante/inversa (non hanno senso per una
 * matrice non quadrata), ma restano le leggi di modulo/spazio, il prodotto interno di
 * Frobenius, il rango e la linearita' di apply().
 */
@DisplayName("Matrix<Real> (rettangolare): invarianti algebrici sul catalogo di valori (standard + estremi)")
class MatrixInvariantsTest
{
	private static final double EPSILON = 1e-9;

	private final InnerProductMatrixSpace<Real, RealField> S = MatrixTestValues.SPACE;
	private final RealField R = RealField.INSTANCE;

	private static final List<Real> SCALARS = List.of(new Real(0), new Real(1), new Real(-1), new Real(2), new Real(0.5));

	private static Vector<Real> domainVector(double... values) {
		return VectorElementFactory.of(new VectorSpace<>(RealField.INSTANCE, MatrixTestValues.COLS), values);
	}

	@Test
	@DisplayName("Addizione commutativa: A+B == B+A, su tutte le coppie di valori standard")
	void additionIsCommutative() {
		List<Matrix<Real>> values = MatrixTestValues.standardValues();
		for (Matrix<Real> a : values) {
			for (Matrix<Real> b : values) {
				assertTrue(S.areEqual(S.add(a, b), S.add(b, a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Addizione associativa: (A+B)+C == A+(B+C), su terne di valori standard")
	void additionIsAssociative() {
		List<Matrix<Real>> values = MatrixTestValues.standardValues();
		for (Matrix<Real> a : values) {
			for (Matrix<Real> b : values) {
				for (Matrix<Real> c : values) {
					assertTrue(S.areEqual(S.add(S.add(a, b), c), S.add(a, S.add(b, c))), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Zero e' identita' additiva su tutto il catalogo, estremi inclusi")
	void zeroIsAdditiveIdentity() {
		for (Matrix<Real> a : MatrixTestValues.allValues()) {
			assertTrue(S.areEqual(a, S.add(a, S.zero())), "a=" + a);
		}
	}

	@Test
	@DisplayName("Inverso additivo: A + (-A) == 0, su tutto il catalogo, estremi inclusi")
	void additiveInverseReturnsZero() {
		for (Matrix<Real> a : MatrixTestValues.allValues()) {
			assertTrue(S.areEqual(S.zero(), S.add(a, S.negate(a))), "a=" + a);
		}
	}

	@Test
	@DisplayName("1*A == A e 0*A == 0, su tutto il catalogo, estremi inclusi")
	void scalingByOneAndZero() {
		for (Matrix<Real> a : MatrixTestValues.allValues()) {
			assertTrue(S.areEqual(a, S.scale(R.one(), a)), "a=" + a);
			assertTrue(S.areEqual(S.zero(), S.scale(R.zero(), a)), "a=" + a);
		}
	}

	@Test
	@DisplayName("Distributiva rispetto alla somma di matrici: k*(A+B) == k*A + k*B, su valori standard")
	void scaleDistributesOverMatrixAddition() {
		List<Matrix<Real>> values = MatrixTestValues.standardValues();
		for (Real k : SCALARS) {
			for (Matrix<Real> a : values) {
				for (Matrix<Real> b : values) {
					assertTrue(S.areEqual(S.scale(k, S.add(a, b)), S.add(S.scale(k, a), S.scale(k, b))), "k=" + k + " a=" + a + " b=" + b);
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva rispetto alla somma di scalari: (k1+k2)*A == k1*A + k2*A, su valori standard")
	void scaleDistributesOverScalarAddition() {
		List<Matrix<Real>> values = MatrixTestValues.standardValues();
		for (Real k1 : SCALARS) {
			for (Real k2 : SCALARS) {
				for (Matrix<Real> a : values) {
					assertTrue(S.areEqual(S.scale(R.add(k1, k2), a), S.add(S.scale(k1, a), S.scale(k2, a))), "k1=" + k1 + " k2=" + k2 + " a=" + a);
				}
			}
		}
	}

	@Test
	@DisplayName("Prodotto interno di Frobenius simmetrico per Real: <A,B> == <B,A>, su tutto il catalogo, estremi inclusi")
	void innerProductIsSymmetricForReal() {
		List<Matrix<Real>> values = MatrixTestValues.allValues();
		for (Matrix<Real> a : values) {
			for (Matrix<Real> b : values) {
				assertTrue(R.areEqual(S.innerProduct(a, b), S.innerProduct(b, a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Prodotto interno lineare nel secondo argomento: <A,B+C> == <A,B>+<A,C>, su terne di valori standard")
	void innerProductIsLinearInSecondArgument() {
		List<Matrix<Real>> values = MatrixTestValues.standardValues();
		for (Matrix<Real> a : values) {
			for (Matrix<Real> b : values) {
				for (Matrix<Real> c : values) {
					Real left = S.innerProduct(a, S.add(b, c));
					Real right = R.add(S.innerProduct(a, b), S.innerProduct(a, c));
					assertTrue(R.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("<A,A> non e' mai negativo, su tutto il catalogo, estremi inclusi")
	void selfInnerProductIsNonNegative() {
		for (Matrix<Real> a : MatrixTestValues.allValues()) {
			assertTrue(S.innerProduct(a, a).getValue() >= 0.0, "a=" + a);
		}
	}

	@Test
	@DisplayName("norm() non e' mai negativa e norm(0) == 0")
	void normIsNeverNegativeAndZeroAtZero() {
		for (Matrix<Real> a : MatrixTestValues.allValues()) {
			assertTrue(S.norm(a).getValue() >= 0.0, "a=" + a);
		}
		assertEquals(0.0, S.norm(S.zero()).getValue(), 0.0);
	}

	@Test
	@DisplayName("Omogeneita' della norma: norm(k*A) == |k|*norm(A), a tolleranza, su valori standard")
	void normIsHomogeneous() {
		for (Real k : SCALARS) {
			for (Matrix<Real> a : MatrixTestValues.standardValues()) {
				double left = S.norm(S.scale(k, a)).getValue();
				double right = Math.abs(k.getValue()) * S.norm(a).getValue();
				assertTrue(Math.abs(left - right) < EPSILON, "k=" + k + " a=" + a);
			}
		}
	}

	@Test
	@DisplayName("Disuguaglianza triangolare: norm(A+B) <= norm(A)+norm(B), a tolleranza, su coppie di valori standard")
	void triangleInequalityHolds() {
		List<Matrix<Real>> values = MatrixTestValues.standardValues();
		for (Matrix<Real> a : values) {
			for (Matrix<Real> b : values) {
				double lhs = S.norm(S.add(a, b)).getValue();
				double rhs = S.norm(a).getValue() + S.norm(b).getValue();
				assertTrue(lhs <= rhs + EPSILON, "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("apply() e' lineare: A(v1+v2) == A(v1)+A(v2) e A(k*v) == k*A(v), su una matrice generica del catalogo")
	void applyIsLinear() {
		Matrix<Real> a = MatrixTestValues.standardValues().get(2); // generica, rango pieno
		Vector<Real> v1 = domainVector(1, 2, 3);
		Vector<Real> v2 = domainVector(-1, 0.5, 4);
		Real k = new Real(3);

		VectorSpace<Real, RealField> domain = new VectorSpace<>(RealField.INSTANCE, MatrixTestValues.COLS);
		VectorSpace<Real, RealField> codomain = new VectorSpace<>(RealField.INSTANCE, MatrixTestValues.ROWS);

		assertTrue(codomain.areEqual(a.apply(domain.add(v1, v2)), codomain.add(a.apply(v1), a.apply(v2))), "additivita' violata");
		assertTrue(codomain.areEqual(a.apply(domain.scale(k, v1)), codomain.scale(k, a.apply(v1))), "omogeneita' violata");
	}

	@Test
	@DisplayName("rank(): rango pieno per una matrice generica, rango carente per righe dipendenti, zero per la matrice nulla")
	void rankReflectsLinearDependence() {
		Matrix<Real> zero = MatrixTestValues.standardValues().get(0);
		Matrix<Real> fullRank = MatrixTestValues.standardValues().get(2);   // [[1,2,3],[4,5,6]]
		Matrix<Real> deficient = MatrixTestValues.standardValues().get(3);   // riga2 = 2*riga1

		assertEquals(0, S.rank(zero), "rango della matrice nulla");
		assertEquals(2, S.rank(fullRank), "rango di una matrice 2x3 con righe indipendenti");
		assertEquals(1, S.rank(deficient), "rango di una matrice con una riga multipla dell'altra");
	}

	@Test
	@DisplayName("Operazioni con dimensioni incompatibili lanciano IllegalArgumentException")
	void operationsRejectMismatchedDimensions() {
		InnerProductMatrixSpace<Real, RealField> wrongShape = new InnerProductMatrixSpace<>(R, MatrixTestValues.ROWS + 1, MatrixTestValues.COLS);
		Matrix<Real> wrongShapedMatrix = MatrixElementFactory.of(wrongShape, new double[(MatrixTestValues.ROWS + 1) * MatrixTestValues.COLS]);
		Matrix<Real> a = MatrixTestValues.standardValues().get(2);
		assertThrows(IllegalArgumentException.class, () -> MatrixTestValues.SPACE.add(a, wrongShapedMatrix));

		// Costruito in un proprio spazio di dimensione 2 (valido di per se'), ma incompatibile
		// con una matrice 2x3 che si aspetta un vettore di dominio di dimensione COLS=3.
		Vector<Real> wrongSizeVector = VectorElementFactory.of(new VectorSpace<>(RealField.INSTANCE, 2), 1.0, 2.0);
		assertThrows(IllegalArgumentException.class, () -> a.apply(wrongSizeVector));
	}

	@Test
	@DisplayName("copy() restituisce una matrice uguale all'originale, su tutto il catalogo")
	void copyReturnsEquivalentMatrix() {
		for (Matrix<Real> a : MatrixTestValues.allValues()) {
			assertEquals(a, a.copy(), "a=" + a);
		}
	}
}
