package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomials.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomials.PolynomialDivisionResult;
import net.gommagomma.smfn.math.algebra.polynomials.Polynomials;
import net.gommagomma.smfn.math.algebra.structures.RealField;

public class PolynomialTest {

    public static void main(String[] args) {
        System.out.println("=== TEST SISTEMA POLINOMI ===\n");

        // 1. Setup Struttura (RealField implementa Field, ScalarStructure e NumericFactory)
        RealField rf = RealField.INSTANCE;

        // 2. Creazione Polinomi: P(x) = x^2 - 1  e  Q(x) = x - 1
        // Usiamo la factory con i double (comodissimo)
        // I coefficienti sono in ordine crescente: a0, a1, a2...
        Polynomial<Real> p = Polynomials.of(rf, -1.0, 0.0, 1.0); // -1 + 0x + 1x^2
        Polynomial<Real> q = Polynomials.of(rf, -1.0, 1.0);      // -1 + 1x

        System.out.println("P(x) = " + p);
        System.out.println("Q(x) = " + q);

        // 3. Operazioni Algebriche
        Polynomial<Real> sum = Polynomials.add(p, q);
        Polynomial<Real> prod = Polynomials.multiply(p, q);

        System.out.println("\nP(x) + Q(x) = " + sum);
        System.out.println("P(x) * Q(x) = " + prod);

        // 4. Divisione Euclidea (Richiede Field)
        // Sappiamo che (x^2 - 1) / (x - 1) = (x + 1) con resto 0
        System.out.println("\n--- Divisione Euclidea ---");
        PolynomialDivisionResult<Real> res = Polynomials.divide(p, q);
        System.out.println("Quoziente: " + res.quotient());
        System.out.println("Resto:     " + res.remainder());

        // 5. Analisi (Derivata e Integrale)
        System.out.println("\n--- Analisi ---");
        Polynomial<Real> dp = Polynomials.derivative(p); // Derivata di x^2 - 1 è 2x
        System.out.println("P'(x) = " + dp);

        // Integrale di 2x con costante C = 5 -> x^2 + 5
        Real constantC = rf.of(5.0);
        Polynomial<Real> intDp = Polynomials.integrate(dp, constantC);
        System.out.println("Integrale di P'(x) con C=5: " + intDp);

        // 6. Valutazione (Morphism)
        // Valutiamo P(3) -> 3^2 - 1 = 8
        Real input = rf.of(3.0);
        Real evaluation = p.apply(input);
        System.out.println("\nValutazione P(3) = " + evaluation);
        
        // 7. Test Ricorsività (Polinomio di Polinomi)
        // Creiamo un polinomio dove i coefficienti sono altri polinomi!
        var structureOfP = p.getStructure(); // ScalarStructure<Polynomial<Real>>
        var polyOfPoly = Polynomials.of(structureOfP, q, p); // q + p*y
        System.out.println("\nPolinomio Ricorsivo (K[x][y]):");
        System.out.println(polyOfPoly);
    }
}
