package net.gommagomma.smfn.client;

import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.polynomial.EuclideanPolynomialRing;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomials;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixRing;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSpace;

/**
 * Test finale di stress algebrico: Ricorsività tra Polinomi e Matrici.
 */
public class RecursiveAlgebraTest {

    public static void main(String[] args) {
        RationalField q = RationalField.INSTANCE;
        int n = 2; // Dimensione delle matrici

        System.out.println("=== 1. TEST CAYLEY-HAMILTON (Polinomio di Matrice) ===");
        // Obiettivo: Verificare che P(A) = 0 dove P è il polinomio caratteristico di A.
        
        // A = [[1, 2], [3, 4]]
        MatrixSpace<Rational, RationalField> qSpace = new MatrixSpace<>(q, n, n);
        MatrixRing<Rational, RationalField> qRing = new MatrixRing<>(q, n);
        
        Matrix<Rational> A = new Matrix<>(n, n, q);
        A.set(0, 0, q.of(1)); A.set(0, 1, q.of(2));
        A.set(1, 0, q.of(3)); A.set(1, 1, q.of(4));

        // P(x) = x^2 - 5x - 2
        Polynomial<Rational> cp = Polynomials.of(q, -2.0, -5.0, 1.0);
        System.out.println("Matrice A:\n" + A);
        System.out.println("Polinomio Caratteristico P(x): " + cp);

        // Valutazione P(A) usando l'algoritmo di Horner per matrici
        Matrix<Rational> resultCH = evaluateMatrixPolynomial(cp, A, qRing, qSpace);
        System.out.println("P(A) = A^2 - 5A - 2I:\n" + resultCH);
        System.out.println("Verifica Cayley-Hamilton: " + (qSpace.isZero(resultCH) ? "SUCCESSO" : "FALLITO"));


        System.out.println("\n=== 2. TEST MATRICE DI POLINOMI (Determinante Simbolico) ===");
        // Obiettivo: Calcolare il determinante di una matrice dove ogni cella è un polinomio.
        
        EuclideanPolynomialRing<Rational, RationalField> pRing = (EuclideanPolynomialRing<Rational, RationalField>) Polynomials.getStructureFor(q);
        // Usiamo MatrixRing perché il determinante ora è lì (senza obbligo di Field)
        MatrixRing<Polynomial<Rational>, EuclideanPolynomialRing<Rational, RationalField>> polyMatrixRing = new MatrixRing<Polynomial<Rational>, EuclideanPolynomialRing<Rational, RationalField>>(pRing, n);

        // M(x) = [[x+1, 1], [x, x^2]]
        Matrix<Polynomial<Rational>> M = new Matrix<>(n, n, pRing);
        M.set(0, 0, Polynomials.of(q, 1, 1));    // 1 + x
        M.set(0, 1, Polynomials.of(q, 1));       // 1
        M.set(1, 0, Polynomials.of(q, 0, 1));    // x
        M.set(1, 1, Polynomials.of(q, 0, 0, 1)); // x^2

        System.out.println("M(x):\n" + M);

        // Calcolo determinante (Laplace in MatrixRing)
        // Det = (x+1)(x^2) - (x)(1) = x^3 + x^2 - x
        Polynomial<Rational> det = polyMatrixRing.determinant(M);
        System.out.println("Det(M(x)): " + det);
        
        // Verifica simbolica
        Polynomial<Rational> expectedDet = Polynomials.of(q, 0, -1, 1, 1); // -x + x^2 + x^3
        System.out.println("Verifica Determinante: " + (det.equals(expectedDet) ? "OK" : "ERRORE"));


        System.out.println("\n=== 3. TEST POLINOMIO A COEFFICIENTI MATRICIALI (Analisi Simbolica) ===");
        // Obiettivo: Creare un polinomio P(y) dove i coefficienti sono matrici e derivarlo.
        
        // P(y) = I + 0*y + A*y^2  (coeff: Matrix<Rational>)
        // Usiamo MatrixRing come ScalarStructure per il Polinomio
        Polynomial<Matrix<Rational>> matrixPoly = new Polynomial<Matrix<Rational>>(qRing, List.of(qRing.one(), qRing.zero(), A));


        System.out.println("P(y) = I + A*y^2:\n" + matrixPoly);

        // Derivata simbolica: P'(y) = 2*A*y
        Polynomial<Matrix<Rational>> deriv = matrixPoly.derive();
        System.out.println("Derivata P'(y) = 2*A*y:\n" + deriv);
        
        // Verifica il coefficiente del termine di primo grado della derivata
        Matrix<Rational> leadingDerivCoeff = deriv.getCoefficient(1);
        Matrix<Rational> expectedCoeff = qSpace.scale(q.of(2), A); // 2 * A
        System.out.println("Verifica coefficiente derivata: " + (leadingDerivCoeff.equals(expectedCoeff) ? "OK" : "ERRORE"));
    }

    /**
     * Valuta un polinomio scalare in una matrice quadrata.
     * P(M) = a_n*M^n + ... + a_1*M + a_0*I
     */
    private static <K extends ScalarElement<K>> Matrix<K> evaluateMatrixPolynomial(
            Polynomial<K> p, Matrix<K> m, MatrixRing<K, ?> ring, MatrixSpace<K, ?> space) {
        
        int deg = p.degree();
        if (deg < 0) return ring.zero();

        // Algoritmo di Horner per matrici: (...(a_n*M + a_{n-1})*M + ... + a_0)*I
        Matrix<K> result = space.scale(p.getCoefficient(deg), ring.one());
        
        for (int i = deg - 1; i >= 0; i--) {
            result = ring.multiply(result, m); // result * M
            Matrix<K> term = space.scale(p.getCoefficient(i), ring.one()); // a_i * I
            result = space.add(result, term);
        }
        return result;
    }
}