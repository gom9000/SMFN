package net.gommagomma.smfn.demo;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.LinearSystemProblem;
import net.gommagomma.smfn.math.analysis.numerical.solvers.linear.GaussianEliminationSolver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

public class LinearSystemDemo {
    public static void main(String[] args) {
        RealField R = RealField.INSTANCE;

        // Sistema noto, soluzione verificabile a mano:
        //   3x + 2y -  z =  1
        //   2x - 2y + 4z = -2
        //  -x + 0.5y - z =  0
        // Soluzione attesa: x=1, y=-2, z=-2
        SquareMatrix<Real> A = SquareMatrixElementFactory.of(R,
            3.0,  2.0, -1.0,
            2.0, -2.0,  4.0,
           -1.0,  0.5, -1.0
        );
        VectorSpace<Real, RealField> V3 = new VectorSpace<>(R, 3);
        Vector<Real> b = VectorElementFactory.of(V3, 1.0, -2.0, 0.0);

        GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, 3);
        Vector<Real> x = solver.solve(A, b);

        System.out.println("Soluzione: " + x + " (atteso: (1.0, -2.0, -2.0))");

        // Verifica indipendente: Ax deve ridare b
        Vector<Real> check = A.apply(x);
        System.out.println("A*x = " + check + " (deve coincidere con b = " + b + ")");

        // Stesso sistema, stesso solutore, passando il problema come singolo oggetto:
        LinearSystemProblem<Real> problem = new LinearSystemProblem<>(A, b);
        Vector<Real> xViaProblem = solver.solve(problem);
        System.out.println("Soluzione via LinearSystemProblem: " + xViaProblem);
    }
}