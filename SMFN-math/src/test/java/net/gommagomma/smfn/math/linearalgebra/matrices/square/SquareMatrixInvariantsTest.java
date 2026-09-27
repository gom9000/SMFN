package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Invarianti algebrici di SquareMatrix/SquareMatrixAlgebra sul catalogo di
 * SquareMatrixTestValues, sullo stesso spirito delle *InvariantsTest di
 * numerics/polynomial/vectors: le stesse leggi generali (anello, algebra lineare,
 * determinante, trasposta) verificate sull'intero catalogo invece che su una singola
 * terna fissa (quella resta compito delle classi *AxiomContract esistenti).
 */
@DisplayName("SquareMatrix<Real>: invarianti algebrici sul catalogo di valori (standard + estremi)")
class SquareMatrixInvariantsTest
{
	private static final double EPSILON = 1e-9;

	private final SquareMatrixAlgebra<Real, RealField> M = SquareMatrixTestValues.RING;
	private final RealField R = RealField.INSTANCE;
	private final VectorSpace<Real, RealField> VS = new VectorSpace<>(RealField.INSTANCE, SquareMatrixTestValues.N);

	private static Vector<Real> v(double... values) {
		return VectorElementFactory.of(new VectorSpace<>(RealField.INSTANCE, SquareMatrixTestValues.N), values);
	}

	private boolean approxEqual(double a, double b) {
		return Math.abs(a - b) < EPSILON;
	}

	/** Confronto elemento per elemento a tolleranza esplicita, per risultati passati da eliminazione gaussiana (arrotondamento un po' piu' ampio dell'epsilon di default di RealField). */
	private boolean matricesApproxEqual(SquareMatrix<Real> a, SquareMatrix<Real> b, double tolerance) {
		int n = a.getN();
		if (b.getN() != n) return false;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (!approxEqualWithTolerance(a.get(i, j).getValue(), b.get(i, j).getValue(), tolerance)) return false;
			}
		}
		return true;
	}

	private boolean approxEqualWithTolerance(double x, double y, double tolerance) {
		return Math.abs(x - y) < tolerance;
	}

	@Test
	@DisplayName("Addizione commutativa: A+B == B+A, su tutte le coppie di valori standard")
	void additionIsCommutative() {
		List<SquareMatrix<Real>> values = SquareMatrixTestValues.standardValues();
		for (SquareMatrix<Real> a : values) {
			for (SquareMatrix<Real> b : values) {
				assertTrue(M.areEqual(M.add(a, b), M.add(b, a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Addizione associativa: (A+B)+C == A+(B+C), su terne di valori standard")
	void additionIsAssociative() {
		List<SquareMatrix<Real>> values = SquareMatrixTestValues.standardValues();
		for (SquareMatrix<Real> a : values) {
			for (SquareMatrix<Real> b : values) {
				for (SquareMatrix<Real> c : values) {
					assertTrue(M.areEqual(M.add(M.add(a, b), c), M.add(a, M.add(b, c))), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Zero e' identita' additiva su tutto il catalogo, estremi inclusi")
	void zeroIsAdditiveIdentity() {
		for (SquareMatrix<Real> a : SquareMatrixTestValues.allValues()) {
			assertTrue(M.areEqual(a, M.add(a, M.zero())), "a=" + a);
		}
	}

	@Test
	@DisplayName("Inverso additivo: A + (-A) == 0, su tutto il catalogo, estremi inclusi")
	void additiveInverseReturnsZero() {
		for (SquareMatrix<Real> a : SquareMatrixTestValues.allValues()) {
			assertTrue(M.areEqual(M.zero(), M.add(a, M.negate(a))), "a=" + a);
		}
	}

	@Test
	@DisplayName("Moltiplicazione associativa: (A*B)*C == A*(B*C), su terne di valori standard")
	void multiplicationIsAssociative() {
		List<SquareMatrix<Real>> values = SquareMatrixTestValues.standardValues();
		for (SquareMatrix<Real> a : values) {
			for (SquareMatrix<Real> b : values) {
				for (SquareMatrix<Real> c : values) {
					SquareMatrix<Real> left = M.multiply(M.multiply(a, b), c);
					SquareMatrix<Real> right = M.multiply(a, M.multiply(b, c));
					assertTrue(M.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva a sinistra: A*(B+C) == A*B + A*C, su terne di valori standard")
	void multiplicationDistributesOverAdditionLeft() {
		List<SquareMatrix<Real>> values = SquareMatrixTestValues.standardValues();
		for (SquareMatrix<Real> a : values) {
			for (SquareMatrix<Real> b : values) {
				for (SquareMatrix<Real> c : values) {
					SquareMatrix<Real> left = M.multiply(a, M.add(b, c));
					SquareMatrix<Real> right = M.add(M.multiply(a, b), M.multiply(a, c));
					assertTrue(M.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva a destra: (A+B)*C == A*C + B*C, su terne di valori standard")
	void multiplicationDistributesOverAdditionRight() {
		List<SquareMatrix<Real>> values = SquareMatrixTestValues.standardValues();
		for (SquareMatrix<Real> a : values) {
			for (SquareMatrix<Real> b : values) {
				for (SquareMatrix<Real> c : values) {
					SquareMatrix<Real> left = M.multiply(M.add(a, b), c);
					SquareMatrix<Real> right = M.add(M.multiply(a, c), M.multiply(b, c));
					assertTrue(M.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("L'identita' e' neutra per la moltiplicazione: I*A == A == A*I, su tutto il catalogo, estremi inclusi")
	void identityIsMultiplicativeIdentity() {
		for (SquareMatrix<Real> a : SquareMatrixTestValues.allValues()) {
			assertTrue(M.areEqual(a, M.multiply(M.one(), a)), "a=" + a);
			assertTrue(M.areEqual(a, M.multiply(a, M.one())), "a=" + a);
		}
	}

	@Test
	@DisplayName("La moltiplicazione tra matrici non e' commutativa in generale (verifica strutturale, non un difetto)")
	void multiplicationIsNotCommutativeInGeneral() {
		SquareMatrix<Real> a = SquareMatrixTestValues.standardValues().get(3); // generica invertibile
		SquareMatrix<Real> b = SquareMatrixTestValues.standardValues().get(5); // simmetrica
		assertFalse(M.areEqual(M.multiply(a, b), M.multiply(b, a)), "a=" + a + " b=" + b + " -- atteso A*B != B*A");
	}

	@Test
	@DisplayName("Il determinante e' moltiplicativo: det(A*B) == det(A)*det(B), a tolleranza, su coppie di valori standard")
	void determinantIsMultiplicative() {
		List<SquareMatrix<Real>> values = SquareMatrixTestValues.standardValues();
		for (SquareMatrix<Real> a : values) {
			for (SquareMatrix<Real> b : values) {
				double left = M.determinant(M.multiply(a, b)).getValue();
				double right = R.multiply(M.determinant(a), M.determinant(b)).getValue();
				assertTrue(approxEqual(left, right), "a=" + a + " b=" + b + " det(a*b)=" + left + " det(a)*det(b)=" + right);
			}
		}
	}

	@Test
	@DisplayName("det(I) == 1 e det(0) == 0")
	void determinantBaseCasesHold() {
		assertEquals(1.0, M.determinant(M.one()).getValue(), EPSILON);
		assertEquals(0.0, M.determinant(M.zero()).getValue(), EPSILON);
	}

	@Test
	@DisplayName("La matrice classicamente singolare del catalogo ha determinante nullo, e non e' invertibile")
	void singularMatrixHasZeroDeterminant() {
		SquareMatrix<Real> singular = SquareMatrixTestValues.standardValues().get(4);
		assertTrue(approxEqual(M.determinant(singular).getValue(), 0.0), "det=" + M.determinant(singular));
		assertFalse(M.isInvertible(singular));
		assertThrows(ArithmeticException.class, () -> M.inverse(singular));
	}

	@Test
	@DisplayName("Trasposta involutiva: (A^T)^T == A, su tutto il catalogo, estremi inclusi")
	void transposeIsInvolution() {
		for (SquareMatrix<Real> a : SquareMatrixTestValues.allValues()) {
			assertTrue(M.areEqual(a, a.conjugateTranspose().conjugateTranspose()), "a=" + a);
		}
	}

	@Test
	@DisplayName("Trasposta del prodotto inverte l'ordine: (A*B)^T == B^T * A^T, su coppie di valori standard")
	void transposeOfProductReversesOrder() {
		List<SquareMatrix<Real>> values = SquareMatrixTestValues.standardValues();
		for (SquareMatrix<Real> a : values) {
			for (SquareMatrix<Real> b : values) {
				SquareMatrix<Real> left = M.multiply(a, b).conjugateTranspose();
				SquareMatrix<Real> right = M.multiply(b.conjugateTranspose(), a.conjugateTranspose());
				assertTrue(M.areEqual(left, right), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("La traccia e' invariante per trasposizione: trace(A) == trace(A^T), su tutto il catalogo, estremi inclusi")
	void traceIsInvariantUnderTranspose() {
		for (SquareMatrix<Real> a : SquareMatrixTestValues.allValues()) {
			assertTrue(R.areEqual(a.trace(), a.conjugateTranspose().trace()), "a=" + a);
		}
	}

	@Test
	@DisplayName("La traccia e' additiva: trace(A+B) == trace(A) + trace(B), su coppie di valori standard")
	void traceIsAdditive() {
		List<SquareMatrix<Real>> values = SquareMatrixTestValues.standardValues();
		for (SquareMatrix<Real> a : values) {
			for (SquareMatrix<Real> b : values) {
				Real left = M.add(a, b).trace();
				Real right = R.add(a.trace(), b.trace());
				assertTrue(R.areEqual(left, right), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Per ogni matrice invertibile del catalogo: A * inverse(A) ~= I ~= inverse(A) * A")
	void inverseSatisfiesDefinitionForInvertibleMatrices() {
		for (SquareMatrix<Real> a : SquareMatrixTestValues.invertibleStandardValues()) {
			SquareMatrix<Real> inv = M.inverse(a);
			assertTrue(matricesApproxEqual(M.multiply(a, inv), M.one(), EPSILON), "a=" + a + " a*inverse(a)=" + M.multiply(a, inv));
			assertTrue(matricesApproxEqual(M.multiply(inv, a), M.one(), EPSILON), "a=" + a + " inverse(a)*a=" + M.multiply(inv, a));
			assertTrue(M.isInvertible(a), "a=" + a);
		}
	}

	@Test
	@DisplayName("apply() e' lineare: A(v1+v2) == A(v1)+A(v2) e A(k*v) == k*A(v), su una matrice generica del catalogo")
	void applyIsLinear() {
		SquareMatrix<Real> a = SquareMatrixTestValues.standardValues().get(3); // generica invertibile
		Vector<Real> v1 = v(1, 2, 3);
		Vector<Real> v2 = v(-1, 4, 0.5);
		Real k = new Real(2.5);

		Vector<Real> left = a.apply(VS.add(v1, v2));
		Vector<Real> right = VS.add(a.apply(v1), a.apply(v2));
		assertTrue(VS.areEqual(left, right), "additivita' violata");

		Vector<Real> leftScaled = a.apply(VS.scale(k, v1));
		Vector<Real> rightScaled = VS.scale(k, a.apply(v1));
		assertTrue(VS.areEqual(leftScaled, rightScaled), "omogeneita' violata");
	}

	@Test
	@DisplayName("L'identita' agisce come identita' su ogni vettore, su tutto il catalogo dei vettori standard")
	void identityActsAsIdentityOnVectors() {
		Vector<Real> v1 = v(1, 2, 3);
		Vector<Real> v2 = v(-5, 0, 7);
		assertTrue(VS.areEqual(v1, M.one().apply(v1)));
		assertTrue(VS.areEqual(v2, M.one().apply(v2)));
	}

	@Test
	@DisplayName("La matrice simmetrica del catalogo e' effettivamente hermitiana/simmetrica, quella generica no")
	void isHermitianDetectsSymmetryCorrectly() {
		SquareMatrix<Real> symmetric = SquareMatrixTestValues.standardValues().get(5);
		SquareMatrix<Real> generic = SquareMatrixTestValues.standardValues().get(3);
		assertTrue(symmetric.isHermitian(), "attesa simmetrica: " + symmetric);
		assertTrue(symmetric.isSymmetric(), "attesa simmetrica: " + symmetric);
		assertFalse(generic.isHermitian(), "attesa non simmetrica: " + generic);
	}

	@Test
	@DisplayName("La matrice di permutazione del catalogo e' unitaria/ortogonale, quella generica no")
	void isUnitaryDetectsOrthogonalityCorrectly() {
		SquareMatrix<Real> permutation = SquareMatrixTestValues.standardValues().get(6);
		SquareMatrix<Real> generic = SquareMatrixTestValues.standardValues().get(3);
		assertTrue(permutation.isUnitary(), "attesa unitaria: " + permutation);
		assertFalse(generic.isUnitary(), "attesa non unitaria: " + generic);
	}

	@Test
	@DisplayName("Moltiplicazione/apply con dimensioni incompatibili lanciano IllegalArgumentException")
	void operationsRejectMismatchedDimensions() {
		SquareMatrix<Real> a2 = SquareMatrixElementFactory.of(R, 1.0, 0.0, 0.0, 1.0); // 2x2
		SquareMatrix<Real> a3 = SquareMatrixTestValues.standardValues().get(1); // 3x3
		assertThrows(IllegalArgumentException.class, () -> SquareMatrixTestValues.RING.multiply(a2, a3));

		Vector<Real> wrongSizeVector = VectorElementFactory.of(new VectorSpace<>(RealField.INSTANCE, 2), 1.0, 2.0);
		assertThrows(IllegalArgumentException.class, () -> a3.apply(wrongSizeVector));
	}

	@Test
	@DisplayName("copy() restituisce una matrice uguale all'originale, su tutto il catalogo")
	void copyReturnsEquivalentMatrix() {
		for (SquareMatrix<Real> a : SquareMatrixTestValues.allValues()) {
			assertEquals(a, a.copy(), "a=" + a);
		}
	}
}
