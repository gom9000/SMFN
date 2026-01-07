package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.EuclideanPolynomialRing;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialDivisionResult;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialRing;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomials;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrices;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;
import net.gommagomma.smfn.math.linearalgebra.operators.CharacteristicPolynomialMorphism;

public class PolynomialDemo
{
	static RealField R = RealField.INSTANCE;
	static ComplexField C = ComplexField.INSTANCE;
	static RationalField Q = RationalField.INSTANCE;

	static Polynomial<Real> p, q, dp;
    static Polynomial<Complex> pz, qz, dpz;
	static Polynomial<SquareMatrix<Real>> pm, qm;
	static Polynomial<SquareMatrix<Complex>> pmz, qmz;

    public static void main(String[] args) {
        System.out.println("=== POLYNOMIALS DEMO ===\n");

        p = Polynomials.of(R, -1.0, 0.0, 1.0); // -1 + 0x + 1x^2
        q = Polynomials.of(R, -1.0, 1.0);      // -1 + 1x
        System.out.println("P(x) = " + p);
        System.out.println("Q(x) = " + q);

        pz = Polynomials.of(C, new Complex(0, 1),  new Complex(2, 0), new Complex(1, 1)); 
        qz = Polynomials.of(C, new Complex(1, -1), new Complex(1, 0));
        System.out.println("P(z) = " + pz);
        System.out.println("Q(z) = " + qz);

        SquareMatrixRing<Real, RealField> M2 = new SquareMatrixRing<>(R, 2);
        SquareMatrix<Real> A = SquareMatrices.of(R, R.of(1), R.of(2), R.of(3), R.of(4)); // M2.of(R.of(1), R.of(2), R.of(3), R.of(4));
        SquareMatrix<Real> I = SquareMatrices.identity(R, 2);
        pm = Polynomials.of(M2, I, A);
        qm = Polynomials.of(M2, A, I);
        System.out.println("\nP(m) = " + pm);
        System.out.println("\nQ(m) = " + qm);
        System.out.println("Grado P(m): " + pm.degree());
        System.out.println("Grado Q(m): " + qm.degree());

        SquareMatrixRing<Complex, ComplexField> M2z = new SquareMatrixRing<>(C, 2);
        SquareMatrix<Complex> B = SquareMatrices.of(C, new Complex(2, 3), new Complex(0, 1), new Complex(1, -3), new Complex(2, -1));
        SquareMatrix<Complex> Iz = SquareMatrices.identity(C, 2);
        pmz = Polynomials.of(M2z, Iz, B);
        qmz = Polynomials.of(M2z, B, Iz);
        System.out.println("\nP(mz) = " + pmz);
        System.out.println("\nQ(mz) = " + qmz);

        System.out.println("\n--- Algebraic Operations (Real) ---");
        PolynomialRing<Real, RealField> r_ring = new PolynomialRing<>(R);
        Polynomial<Real> sumr = r_ring.add(p, q);
        Polynomial<Real> prod = r_ring.multiply(p, q);
        System.out.println("\nP(x) + Q(x) = " + sumr);
        System.out.println("P(x) * Q(x) = " + prod);
        
        System.out.println("\n--- Algebraic Operations (Complex) ---");
        PolynomialRing<Complex, ComplexField> c_ring = new PolynomialRing<>(C);
        Polynomial<Complex> sumz = c_ring.add(pz, qz);
        Polynomial<Complex> prodz = c_ring.multiply(pz, qz);
        System.out.println("\nP(z) + Q(z) = " + sumz);
        System.out.println("P(z) * Q(z) = " + prodz);
        
        System.out.println("\n--- Algebraic Operations (Matrix) ---");
        PolynomialRing<SquareMatrix<Real>, SquareMatrixRing<Real, RealField>> matrixPolyRing = new PolynomialRing<>(M2);
        Polynomial<SquareMatrix<Real>> sumrm = matrixPolyRing.add(pm, qm);
        System.out.println("\nP(m) + Q(m) = " + sumrm);

        System.out.println("\n--- Algebraic Operations (Matrix<Complex>) ---");
        PolynomialRing<SquareMatrix<Complex>, SquareMatrixRing<Complex, ComplexField>> matrixPolyRingc = new PolynomialRing<>(M2z);
        Polynomial<SquareMatrix<Complex>> sum = matrixPolyRingc.add(pmz, qmz);
        System.out.println("\nP(mz) + Q(mz) = " + sum);

        System.out.println("\n--- Euclidean Division (Real) ---");
        EuclideanPolynomialRing<Real, RealField> r_ering = new EuclideanPolynomialRing<>(R);
        PolynomialDivisionResult<Real> r = r_ering.divide(p, q);
        System.out.println("P / Q :\nQuotient:  " + r.quotient());
        System.out.println("Remainder: " + r.remainder());
        
        var PR = p.getStructure();
        Polynomial<Real> check = r_ering.add( r_ering.multiply(q, r.quotient()), r.remainder());
        System.out.println("Verifica (p == q*Q + R) = " + PR.add(PR.multiply(q, r.quotient()), r.remainder()) + " : "  + p.equals(check));

        System.out.println("\n--- Euclidean Division (Complex) ---");
        EuclideanPolynomialRing<Complex, ComplexField> c_ering = new EuclideanPolynomialRing<>(C);
        PolynomialDivisionResult<Complex> rz = c_ering.divide(pz, qz);
        System.out.println("Quotient:  " + rz.quotient());
        System.out.println("Remainder: " + rz.remainder());
        
        var PZ = pz.getStructure();
        Polynomial<Complex> check2 = c_ering.add( c_ering.multiply(qz, rz.quotient()), rz.remainder());
        System.out.println("Verifica (pz == qz*Q + R) = " + PZ.add(PZ.multiply(qz, rz.quotient()), rz.remainder()) + " : "  + pz.equals(check2));

        System.out.println("\n--- Euclidean Division (SquareMatrix<Complex>) ---");
        SquareMatrixField<Complex, ComplexField> MF2z = new SquareMatrixField<>(C, 2);
        EuclideanPolynomialRing<SquareMatrix<Complex>, SquareMatrixField<Complex, ComplexField>> c_space = new EuclideanPolynomialRing<>(MF2z);
        PolynomialDivisionResult<SquareMatrix<Complex>> rmz = c_space.divide(pmz, qmz);
        System.out.println("Quotient:  " + rmz.quotient());
        System.out.println("Remainder: " + rmz.remainder());

        var PMZ = pmz.getStructure();
        Polynomial<SquareMatrix<Complex>> check3 = c_space.add( c_space.multiply(qmz, rmz.quotient()), rmz.remainder());
        System.out.println("Verifica (pmz == qmz*Q + R) = " + PMZ.add(PMZ.multiply(qmz, rmz.quotient()), rmz.remainder()) + " : "  + pmz.equals(check3));

        System.out.println("\n--- Polynomial of Polynomial (Real) ---");
    	var structureOfP = p.getStructure();
        var polyOfPoly = Polynomials.of(structureOfP, q, p); // q + p*y
        System.out.println("\nPolinomio Ricorsivo (K[x][y]):");
        System.out.println(polyOfPoly);

    	System.out.println("\n--- Polynomial of Polynomial (Complex)---");
    	var PZc = pz.getStructure();
        var polyOfPolyc = Polynomials.of(PZc, qz, pz); // q + p*y
        System.out.println("\nPolinomio Ricorsivo (K[z1][z2]):");
        System.out.println(polyOfPolyc);

        System.out.println("\n--- Matrix of Polynomial ---");
        EuclideanPolynomialRing<Rational, RationalField> pRing = new EuclideanPolynomialRing<>(Q);
        SquareMatrixRing<Polynomial<Rational>, EuclideanPolynomialRing<Rational, RationalField>> polyMatrixRing = new SquareMatrixRing<>(pRing, 2);
        // M(x) = [[x+1, 1], [x, x^2]]
        SquareMatrix<Polynomial<Rational>> D = SquareMatrices.of(pRing, Polynomials.of(Q, 1, 1), Polynomials.of(Q, 1), Polynomials.of(Q, 0, 1), Polynomials.of(Q, 0, 0, 1));
        System.out.println("M(x):\n" + D);
        Polynomial<Rational> det = polyMatrixRing.determinant(D); // Det = (x+1)(x^2) - (x)(1) = x^3 + x^2 - x
        System.out.println("Det(M(x)): " + det);
        Polynomial<Rational> expectedDet = Polynomials.of(Q, 0, -1, 1, 1); // -x + x^2 + x^3
        System.out.println("Verifica Determinante: " + (det.equals(expectedDet) ? "OK" : "ERRORE"));

        System.out.println("\n--- CAYLEY-HAMILTON test ---"); // Obiettivo: Verificare che P(M) = 0 dove P è il polinomio caratteristico di M.
        SquareMatrixField<Rational, RationalField> qMatrixField = new SquareMatrixField<>(Q, 2);
        Rational[] data_M = { Q.of(1), Q.of(2),	Q.of(3), Q.of(4) };
        SquareMatrix<Rational> M = qMatrixField.of(data_M);
        Polynomial<Rational> cp = (new CharacteristicPolynomialMorphism()).evaluate(M);
        System.out.println("Matrice M:\n" + M);
        System.out.println("Polinomio Caratteristico P(x): " + cp);
        SquareMatrixRing<Rational, RationalField> qMatrixRing = new SquareMatrixRing<>(Q, 2);
        SquareMatrix<Rational> resultCH = evaluateMatrixPolynomial(cp, M, qMatrixRing, qMatrixField);
        System.out.println("P(M) = M^2 - 5M - 2I:\n" + resultCH);
        System.out.println("Verifica Cayley-Hamilton: " + (qMatrixField.isZero(resultCH) ? "SUCCESSO" : "FALLITO"));
    }

    /**
     * Valuta un polinomio scalare in una matrice quadrata.
     * P(M) = a_n*M^n + ... + a_1*M + a_0*I
     */
    private static <K extends ScalarElement<K>> SquareMatrix<K> evaluateMatrixPolynomial(Polynomial<K> p, SquareMatrix<K> m, SquareMatrixRing<K, ?> ring, SquareMatrixField<K, ?> space) {
        
        int deg = p.degree();
        if (deg < 0) return ring.zero();

        // Algoritmo di Horner per matrici: (...(a_n*M + a_{n-1})*M + ... + a_0)*I
        SquareMatrix<K> result = space.scale(p.getCoefficient(deg), ring.one());
        
        for (int i = deg - 1; i >= 0; i--) {
            result = ring.multiply(result, m); // result * M
            SquareMatrix<K> term = space.scale(p.getCoefficient(i), ring.one()); // a_i * I
            result = space.add(result, term);
        }
        return result;
    }
}

