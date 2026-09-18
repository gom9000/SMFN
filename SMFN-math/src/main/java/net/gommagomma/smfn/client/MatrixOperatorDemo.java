package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialDivisionProvider;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialDivisionResult;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialRing;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSemimodule;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

public class MatrixOperatorDemo {
    static ComplexField C = ComplexField.INSTANCE;
    static RealField R = RealField.INSTANCE;

    public static void main(String[] args) {
        System.out.println("--- SquareMatrix come Operatore Lineare, endomorfismo (Complex) ---");
        SquareMatrixRing<Complex, ComplexField> M2z = new SquareMatrixRing<>(C, 2);
        VectorSemimodule<Complex, ComplexField> V2 = new VectorSemimodule<>(C, 2);

        // Rotazione di 90 gradi nel piano: [[0,-1],[1,0]]
        SquareMatrix<Complex> rotation90 = M2z.of(new Complex[] {
            new Complex(0, 0), new Complex(0, 1),
            new Complex(1, 0), new Complex(0, 0)
        });

        Vector<Complex> v = V2.of(new Complex[] { new Complex(1, 0), new Complex(0, 0) }); // (1,0)

        Vector<Complex> rotated = rotation90.apply(v);
        System.out.println("R90 applicata a (1,0) = " + rotated + " (atteso: (0,1))");

        // Composizione di operatori (ereditata da Mapping) coincide col prodotto tra matrici
        Vector<Complex> viaCompose  = rotation90.compose(rotation90).apply(v);
        Vector<Complex> viaProduct  = M2z.multiply(rotation90, rotation90).apply(v);
        System.out.println("R90 . R90 applicata a (1,0)        = " + viaCompose);
        System.out.println("(R90 * R90) applicata direttamente = " + viaProduct);
        System.out.println("Coincidono: " + viaCompose.equals(viaProduct));

        // power(n) (ereditato da Operator): 4 rotazioni di 90 tornano all'identita'
        Vector<Complex> afterFour = rotation90.power(4).apply(v);
        System.out.println("R90^4 applicata a (1,0) = " + afterFour + " (atteso: torna a (1,0))");

        System.out.println("\n--- Matrix come applicazione lineare m x n, non endomorfismo (Real) ---");
        // Una matrice rettangolare non e' un endomorfismo: implementa Mapping, non Operator/LinearOperator --
        // niente compose()/power(), perche' dominio e codominio hanno dimensione diversa.
        MatrixSemimodule<Real, RealField> M23 = new MatrixSemimodule<>(R, 2, 3);
        Matrix<Real> projection = M23.of(new Real[] {
            new Real(1), new Real(0), new Real(2),
            new Real(0), new Real(1), new Real(-1)
        });

        VectorSemimodule<Real, RealField> V3 = new VectorSemimodule<>(R, 3);
        Vector<Real> x = V3.of(new Real[] { new Real(1), new Real(2), new Real(3) });

        Vector<Real> y = projection.apply(x); // R^3 -> R^2
        System.out.println("A (2x3) applicata a (1,2,3) = " + y + " (atteso: (7, -1))");

        VectorSemimodule<Real, RealField> V2r = new VectorSemimodule<>(R, 2);
        Vector<Real> wrongSize = V2r.of(new Real[] { new Real(1), new Real(1) });
        try {
            projection.apply(wrongSize);
            System.out.println("ERRORE: doveva lanciare un'eccezione");
        } catch (IllegalArgumentException e) {
            System.out.println("Dimensione sbagliata correttamente rifiutata: " + e.getMessage());
        }

        System.out.println("\n--- Divisione polinomiale a coefficienti matriciali (Complex) ---");
        SquareMatrixAlgebra<Complex, ComplexField> MF2z = new SquareMatrixAlgebra<>(C, 2);
        PolynomialDivisionProvider<SquareMatrix<Complex>, SquareMatrixAlgebra<Complex, ComplexField>> divisionProvider =
            new PolynomialDivisionProvider<>(MF2z);
        PolynomialRing<SquareMatrix<Complex>, SquareMatrixAlgebra<Complex, ComplexField>> polyMatrixRing =
            new PolynomialRing<>(MF2z);

        SquareMatrix<Complex> identity = M2z.of(new Complex[] {
            new Complex(1, 0), new Complex(0, 1),
            new Complex(0, 0), new Complex(1, 0)
        });

        // pmz(x) = I + R90*x   (divisore: coefficiente di testa R90, invertibile)
        Polynomial<SquareMatrix<Complex>> pmz = polyMatrixRing.of(java.util.List.of(identity, rotation90));
        // qmz(x) = R90 + I*x
        Polynomial<SquareMatrix<Complex>> qmz = polyMatrixRing.of(java.util.List.of(rotation90, identity));

        PolynomialDivisionResult<SquareMatrix<Complex>> rmz = divisionProvider.divide(pmz, qmz);
        System.out.println("Quotient:  " + rmz.quotient());
        System.out.println("Remainder: " + rmz.remainder());

        // Verifica nel verso corretto: dividendo == quoziente*divisore + resto (mai il contrario, K non commuta)
        Polynomial<SquareMatrix<Complex>> check = polyMatrixRing.add(
            polyMatrixRing.multiply(rmz.quotient(), qmz), rmz.remainder());
        System.out.println("Verifica (pmz == Q*qmz + R) = " + pmz.equals(check));
    }
}
