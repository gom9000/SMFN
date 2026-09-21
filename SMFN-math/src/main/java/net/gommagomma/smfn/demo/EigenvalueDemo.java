package net.gommagomma.smfn.demo;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenvalueSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.GeneralEigenvalueSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.HermitianEigenvalueSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.JacobiEigenvalueSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.RealEigenDecomposition;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

/**
 * Dimostra i due rami oggi implementati di EigenvalueSolver: matrice reale
 * simmetrica (JacobiEigenvalueSolver) e matrice complessa hermitiana
 * (HermitianEigenvalueSolver) -- entrambi usati sia direttamente sia
 * tramite GeneralEigenvalueSolver, il dispatcher che sceglie da solo.
 */
public class EigenvalueDemo
{
	public static void main(String[] args) {
		RealField R = RealField.INSTANCE;
		ComplexField C = ComplexField.INSTANCE;
		ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 100);

		System.out.println("=== Caso 1: matrice reale simmetrica (JacobiEigenvalueSolver) ===");
		// A = [[2,1],[1,2]] -- autovalori noti 1 e 3, autovettori (1,1)/sqrt(2), (1,-1)/sqrt(2)
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 2.0, 1.0, 1.0, 2.0);

		EigenDecomposition realResult = new JacobiEigenvalueSolver().solve(A, params);
		System.out.println("autovalori = " + realResult.getEigenvalues());

		RealEigenDecomposition realOnly = realResult.toRealDecomposition(new Real(1e-9));
		System.out.println("autovalori (vista reale) = " + realOnly.getEigenvalues());
		System.out.println("autovettori (vista reale) = " + realOnly.getEigenvectors());

		System.out.println("\n=== Caso 2: matrice complessa hermitiana (HermitianEigenvalueSolver) ===");
		// H = [[2,1+i],[1-i,3]] -- autovalori noti 1 e 4 (verificati a mano e con numpy)
		SquareMatrix<Complex> H = SquareMatrixElementFactory.of(C,
			new Complex(2, 0), new Complex(1, 1),
			new Complex(1, -1), new Complex(3, 0)
		);

		EigenDecomposition hermitianResult = new HermitianEigenvalueSolver().solve(H, params);
		System.out.println("autovalori = " + hermitianResult.getEigenvalues());
		System.out.println("autovettori (genuinamente complessi) = " + hermitianResult.getEigenvectors());

		System.out.println("\n=== Stesse due matrici, via GeneralEigenvalueSolver (dispatcher unico) ===");
		EigenvalueSolver<Real> realDispatcher = new GeneralEigenvalueSolver<>();
		EigenvalueSolver<Complex> complexDispatcher = new GeneralEigenvalueSolver<>();
		System.out.println("caso reale, via dispatcher      = " + realDispatcher.solve(A, params).getEigenvalues());
		System.out.println("caso hermitiano, via dispatcher = " + complexDispatcher.solve(H, params).getEigenvalues());
	}
}
