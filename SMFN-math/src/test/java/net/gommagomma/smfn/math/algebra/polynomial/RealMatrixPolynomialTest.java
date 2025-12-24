package net.gommagomma.smfn.math.algebra.polynomial;

import net.gommagomma.smfn.math.linearalgebra.core.structures.specialized.RealMatrixRing;
import net.gommagomma.smfn.math.linearalgebra.real.RealMatrix;

public class RealMatrixPolynomialTest {

    public static void main(String[] args) {
        // 1. Creiamo il Ring delle matrici 2x2
        RealMatrixRing ring2x2 = new RealMatrixRing(2);

        // 2. Definiamo le matrici coefficienti
        // M0 = Identità (grado 0)
        RealMatrix m0 = ring2x2.getIdentity(); 
        
        // M1 = Una matrice specifica (grado 1)
        // Esempio: [[0, 1], [0, 0]] - Matrice nilpotente (N^2 = 0)
        RealMatrix m1 = ring2x2.createMatrix(new double[][]{{0, 1}, {0, 0}});

        // 3. Creiamo il polinomio P(x) = M1*x + M0
        // Usiamo la factory .ring() perché RealMatrix implementa RingElement
        GeneralPolynomial<RealMatrix> polyP = Polynomials.ring(ring2x2, m0, m1);

        System.out.println("Polinomio matriciale P(x):");
        System.out.println(polyP);

        // 4. Calcoliamo P(x)^2 = (M1*x + M0) * (M1*x + M0)
        // Matematicamente: M1^2*x^2 + (M1*M0 + M0*M1)*x + M0^2
        // Dato che M1 è nilpotente, M1^2 sarà la matrice nulla.
        GeneralPolynomial<RealMatrix> polySquared = polyP.multiply(polyP);

        System.out.println("\nQuadrato P(x)^2:");
        System.out.println(polySquared);

        // Verifica del grado: se la moltiplicazione gestisce bene gli zeri, 
        // il grado deve essere sceso a 1 perché il termine x^2 ha coefficiente ZeroMatrix.
        System.out.println("\nGrado di P(x)^2: " + polySquared.degree());
        
        // 5. Valutazione in un punto reale (es. x = 3.0)
        //Real xVal = ring2x2.getScalarStructure().of(3.0);
        RealMatrix xVal = ring2x2.of(3.0);
        RealMatrix result = polyP.eval(xVal);
        
        System.out.println("\nValutazione P(3.0):");
        System.out.println(result);
    }
}