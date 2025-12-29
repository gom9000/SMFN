package net.gommagomma.smfn.math.algebra.polynomial;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.TreeMap;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.real.RealMatrix;
import net.gommagomma.smfn.math.linearalgebra.core.structures.specialized.RealMatrixRing;

class PolynomialTest {

    private final RealField R = RealField.getInstance();

    @Test
    void testScalarPolynomialOperations() {
        // Creiamo P(x) = x + 2
        TreeMap<Integer, Real> coeffs1 = new TreeMap<>();
        coeffs1.put(1, R.of(1.0));
        coeffs1.put(0, R.of(2.0));
        GeneralPolynomial<Real> p1 = new GeneralPolynomial<>(coeffs1, R);

        // Creiamo Q(x) = x - 2
        TreeMap<Integer, Real> coeffs2 = new TreeMap<>();
        coeffs2.put(1, R.of(1.0));
        coeffs2.put(0, R.of(-2.0));
        GeneralPolynomial<Real> p2 = new GeneralPolynomial<>(coeffs2, R);

        // Test Moltiplicazione: (x + 2)(x - 2) = x^2 - 4
        GeneralPolynomial<Real> product = p1.multiply(p2);
        
        assertEquals(2, product.degree());
        assertTrue(product.getCoefficient(2).isMathematicallyEqualTo(R.of(1.0)));
        assertTrue(product.getCoefficient(1).isMathematicallyEqualTo(R.zero()));
        assertTrue(product.getCoefficient(0).isMathematicallyEqualTo(R.of(-4.0)));

        // Test Valutazione: P(3) = 3 + 2 = 5
        Real result = p1.evaluate(R.of(3.0));
        assertTrue(result.isMathematicallyEqualTo(R.of(5.0)));
    }

    @Test
    void testMatrixPolynomialEvaluation() {
        int n = 2;
        RealMatrixRing matrixRing = new RealMatrixRing(n);
        
        // Creiamo P(x) = x^2 (Polinomio a coefficienti matriciali)
        // Per testare P(A) = A^2, i coefficienti devono essere matrici.
        // a2 = I, a1 = 0, a0 = 0
        TreeMap<Integer, RealMatrix> matrixCoeffs = new TreeMap<>();
        matrixCoeffs.put(2, matrixRing.getIdentity()); 
        
        GeneralPolynomial<RealMatrix> poly = new GeneralPolynomial<>(matrixCoeffs, matrixRing);

        // Creiamo una matrice A = [[1, 2], [3, 4]]
        double[][] data = {{1, 2}, {3, 4}};
        RealMatrix A = matrixRing.createMatrix(data);

        // Valutiamo P(A) -> dovrebbe restituire A * A
        RealMatrix result = poly.evaluate(A);
        RealMatrix expected = A.multiply(A);

        assertTrue(result.isMathematicallyEqualTo(expected), 
            "P(A) con P(x)=x^2 dovrebbe essere uguale ad A*A");
    }

    @Test
    void testZeroPolynomial() {
        GeneralPolynomial<Real> p = new GeneralPolynomial<>(new TreeMap<>(), R);
        assertTrue(p.isZero());
        assertEquals(-1, p.degree());
        assertTrue(p.evaluate(R.of(10.0)).isMathematicallyEqualTo(R.zero()));
    }
}