package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialDivisionResult;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomials;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;

public class PolynomialDemo
{
	static RealField R = RealField.INSTANCE;
	static ComplexField C = ComplexField.INSTANCE;

	static Polynomial<Real> p, q, dp;
    static Polynomial<Complex> pz, qz, dpz;
	static Polynomial<Matrix<Real>> pm, qm;
	static Polynomial<Matrix<Complex>> pmz, qmz;

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

        SquareMatrixRing<Real, RealField> M = new SquareMatrixRing<>(R, 2);
        Matrix<Real> A = new Matrix<>(2, 2, R);
        A.set(0, 0, R.of(1)); A.set(0, 1, R.of(2));
        A.set(1, 0, R.of(3)); A.set(1, 1, R.of(4));
        Matrix<Real> I = M.one();
        pm = Polynomials.of(M, I, A);
        qm = Polynomials.of(M, A, I);
        System.out.println("\nP(m) = " + pm);
        System.out.println("\nQ(m) = " + qm);

        SquareMatrixRing<Complex, ComplexField> Mz = new SquareMatrixRing<>(C, 2);
        Matrix<Complex> B = new Matrix<>(2, 2, C);
        B.set(0, 0, new Complex(2, 3)); B.set(0, 1, new Complex(0, 1));
        B.set(1, 0, new Complex(1, -3)); B.set(1, 1, new Complex(2, -1));
        Matrix<Complex> Iz = Mz.one();
        pmz = Polynomials.of(Mz, Iz, B);
        qmz = Polynomials.of(Mz, B, Iz);
        System.out.println("\nP(mz) = " + pmz);
        System.out.println("\nQ(mz) = " + qmz);

        algebraicOperationsReal();
        algebraicOperationsComplex();
        algebraicOperationsMatrix();
        algebraicOperationsMatrixComplex();
        euclideanDivisionReal();
        euclideanDivisionComplex();
        euclideanDivisionMatrixComplex();
        derivateReal();
        derivateComplex();
        integral();
        evaluation();
        evaluationComplex();
        recursiveReal();
        recursiveComplex();
    }

    static void algebraicOperationsReal() {
        System.out.println("\n--- Algebraic Operations (Real) ---");
        Polynomial<Real> sum = Polynomials.add(p, q);
        Polynomial<Real> prod = Polynomials.multiply(p, q);
        System.out.println("\nP(x) + Q(x) = " + sum);
        System.out.println("P(x) * Q(x) = " + prod);
        
    }
    static void algebraicOperationsComplex() {
        System.out.println("\n--- Algebraic Operations (Complex) ---");
        Polynomial<Complex> sumz = Polynomials.add(pz, qz);
        Polynomial<Complex> prodz = Polynomials.multiply(pz, qz);
        System.out.println("\nP(z) + Q(z) = " + sumz);
        System.out.println("P(z) * Q(z) = " + prodz);
        
    }
    static void euclideanDivisionReal() {
        System.out.println("\n--- Euclidean Division (Real) ---");
        PolynomialDivisionResult<Real> r = Polynomials.divide(p, q);
        System.out.println("Quotient:  " + r.quotient());
        System.out.println("Remainder: " + r.remainder());
        
        var PR = p.getStructure();
        Polynomial<Real> check = Polynomials.add( Polynomials.multiply(q, r.quotient()), r.remainder());
        System.out.println("Verifica (p == q*Q + R) = " + PR.add(PR.multiply(q, r.quotient()), r.remainder()) + " : "  + p.equals(check));
    }
    static void euclideanDivisionComplex() {
        System.out.println("\n--- Euclidean Division (Complex) ---");
        PolynomialDivisionResult<Complex> rz = Polynomials.divide(pz, qz);
        System.out.println("Quotient:  " + rz.quotient());
        System.out.println("Remainder: " + rz.remainder());
        
        var PZ = pz.getStructure();
        Polynomial<Complex> check = Polynomials.add( Polynomials.multiply(qz, rz.quotient()), rz.remainder());
        System.out.println("Verifica (pz == qz*Q + R) = " + PZ.add(PZ.multiply(qz, rz.quotient()), rz.remainder()) + " : "  + pz.equals(check));
    }
    static void euclideanDivisionMatrixComplex() {
        System.out.println("\n--- Euclidean Division (Matrix<Complex>) ---");
        PolynomialDivisionResult<Matrix<Complex>> rmz = Polynomials.divide(pmz, qmz);
        System.out.println("Quotient:  " + rmz.quotient());
        System.out.println("Remainder: " + rmz.remainder());
        
        var PMZ = pmz.getStructure();
        Polynomial<Matrix<Complex>> check = Polynomials.add( Polynomials.multiply(qmz, rmz.quotient()), rmz.remainder());
        System.out.println("Verifica (pmz == qmz*Q + R) = " + PMZ.add(PMZ.multiply(qmz, rmz.quotient()), rmz.remainder()) + " : "  + pmz.equals(check));
    }
    static void derivateReal() {
    	System.out.println("\n--- Derivative (Real) ---");
        dp = Polynomials.derivative(p);
        System.out.println("P'(x) = " + dp);
    }
    static void derivateComplex() {
    	System.out.println("\n--- Derivative (Complex>) ---");
        dpz = Polynomials.derivative(pz);
        System.out.println("P'(z) = " + dpz);
    }
    static void integral() {
    	System.out.println("\n--- Integral ---");
    	Real constantC = R.of(5.0);
        Polynomial<Real> intDp = Polynomials.integrate(dp, constantC); // C = 5 -> x^2 + 5
        System.out.println("Integrale di P'(x) con C=5: " + intDp);
    }
    static void evaluation() {
    	System.out.println("\n--- Evaluation ---");
    	Real input = R.of(3.0);
        Real evaluation = p.apply(input);
        System.out.println("\nValutazione P(3) = " + evaluation);
    }
    static void evaluationComplex() {
    	System.out.println("\n--- Evaluation (Complex) ---");
    	Complex input = C.of(3);
        Complex evaluation = pz.apply(input);
        System.out.println("\nValutazione P(3) = " + evaluation);
    }
    static void recursiveReal() {
    	System.out.println("\n--- Polynomial of Polynomial (Real) ---");
    	var structureOfP = p.getStructure();
        var polyOfPoly = Polynomials.of(structureOfP, q, p); // q + p*y
        System.out.println("\nPolinomio Ricorsivo (K[x][y]):");
        System.out.println(polyOfPoly);
    }
    static void recursiveComplex() {
    	System.out.println("\n--- Polynomial of Polynomial (Complex)---");
    	var PZ = pz.getStructure();
        var polyOfPoly = Polynomials.of(PZ, qz, pz); // q + p*y
        System.out.println("\nPolinomio Ricorsivo (K[z1][z2]):");
        System.out.println(polyOfPoly);
    }
    static void algebraicOperationsMatrix() {
        System.out.println("\n--- Algebraic Operations (Matrix) ---");
        Polynomial<Matrix<Real>> sum = Polynomials.add(pm, qm);
        System.out.println("\nP(m) + Q(m) = " + sum);
    }
    static void algebraicOperationsMatrixComplex() {
        System.out.println("\n--- Algebraic Operations (Matrix<Complex>) ---");
        Polynomial<Matrix<Complex>> sum = Polynomials.add(pmz, qmz);
        System.out.println("\nP(mz) + Q(mz) = " + sum);
    }
}
