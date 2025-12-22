package net.gommagomma.smfn.math.algebra.polynomial;

import net.gommagomma.smfn.math.algebra.core.algorithms.AlgebraicAlgorithms;
import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;

public class PolynomialGcdTest {
    public static void main(String[] args) {
        // 1. Setup del campo dei coefficienti (Razionali)
        RationalField q = RationalField.getInstance();

        // 2. Creazione di A(x) = 1*x^2 + 0*x - 1
        // Passiamo i coefficienti dal grado 0 in su: [-1, 0, 1]
        EuclideanPolynomial<Rational> polyA = Polynomials.euclidean(q, q.of(-1), q.zero(), q.one());

        // 3. Creazione di B(x) = 1*x^2 - 2*x + 1
        // Coefficienti: [1, -2, 1]
        EuclideanPolynomial<Rational> polyB = Polynomials.euclidean(q, q.of(1), q.of(-2), q.one());

        System.out.println("A(x) = " + polyA);
        System.out.println("B(x) = " + polyB);

        // 4. Esecuzione dell'algoritmo universale
        EuclideanPolynomial<Rational> gcd = AlgebraicAlgorithms.gcd(polyA, polyB);

        // 5. Normalizzazione (opzionale, per avere il coefficiente di testa = 1)
        EuclideanPolynomial<Rational> normalizedGcd = gcd.normalize();

        System.out.println("GCD(A, B) = " + normalizedGcd); 
        // Dovrebbe stampare qualcosa di simile a: -1 + 1x^1
    }
}