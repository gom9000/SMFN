package net.gommagomma.smfn.demo;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenvalueSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.GeneralEigenvalueSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.HermitianEigenvalueSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.JacobiEigenvalueSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.QREigenvalueSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.RealEigenDecomposition;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

/**
 * Dimostra i tre rami oggi implementati di EigenvalueSolver: matrice reale
 * simmetrica (JacobiEigenvalueSolver), matrice complessa hermitiana
 * (HermitianEigenvalueSolver) e matrice qualunque senza simmetria/hermitianita'
 * (QREigenvalueSolver, con K = Real o K = Complex) -- tutti usati sia
 * direttamente sia tramite GeneralEigenvalueSolver, il dispatcher che sceglie
 * da solo il solver giusto in base a cosa la matrice garantisce.
 */
public class EigenvalueDemo
{
	public static void main(String[] args) {
		RealField R = RealField.INSTANCE;
		ComplexField C = ComplexField.INSTANCE;
		StoppingParameters params = new StoppingParameters(new Real(1e-12), 100);

		System.out.println("=== Caso 1: matrice reale simmetrica (JacobiEigenvalueSolver) ===");
		// A = [[2,1],[1,2]] -- autovalori noti 1 e 3, autovettori (1,1)/sqrt(2), (1,-1)/sqrt(2)
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 2.0, 1.0, 1.0, 2.0);

		SolverResult<EigenDecomposition> realSolverResult = new JacobiEigenvalueSolver().solve(A, params);
		System.out.println("stato = " + realSolverResult.getStatus() + ", iterazioni = " + realSolverResult.getIterationsExecuted());
		EigenDecomposition realResult = realSolverResult.getValue();
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

		SolverResult<EigenDecomposition> hermitianSolverResult = new HermitianEigenvalueSolver().solve(H, params);
		System.out.println("stato = " + hermitianSolverResult.getStatus() + ", iterazioni = " + hermitianSolverResult.getIterationsExecuted());
		EigenDecomposition hermitianResult = hermitianSolverResult.getValue();
		System.out.println("autovalori = " + hermitianResult.getEigenvalues());
		System.out.println("autovettori (genuinamente complessi) = " + hermitianResult.getEigenvectors());

		System.out.println("\n=== Caso 3: matrice reale non simmetrica, autovalori reali (QREigenvalueSolver) ===");
		// G = [[1,2],[3,4]] -- non simmetrica: Jacobi non si applica. Autovalori noti (5+-sqrt(33))/2,
		// calcolati a mano risolvendo lambda^2 - 5*lambda - 2 = 0 (traccia 5, determinante -2).
		SquareMatrix<Real> G = SquareMatrixElementFactory.of(R, 1.0, 2.0, 3.0, 4.0);

		SolverResult<EigenDecomposition> qrRealResult = new QREigenvalueSolver<Real>().solve(G, params);
		System.out.println("stato = " + qrRealResult.getStatus() + ", iterazioni = " + qrRealResult.getIterationsExecuted());
		EigenDecomposition qrRealDecomposition = qrRealResult.getValue();
		// Anche con G reale, EigenDecomposition modella sempre autovalori/autovettori in Complex:
		// qui la parte immaginaria e' zero, ma il tipo restituito non lo sa in anticipo.
		System.out.println("autovalori (sempre Complex) = " + qrRealDecomposition.getEigenvalues());

		System.out.println("\n=== Caso 4: matrice reale con autovalori complessi coniugati (QREigenvalueSolver) ===");
		// N = [[0,-1],[1,0]] -- rotazione di 90 gradi: nessun autovettore reale, autovalori noti +-i.
		// E' il caso che JacobiEigenvalueSolver non potrebbe mai risolvere (non e' simmetrica) e che
		// il QR risolve senza bisogno di alcuna gestione speciale della coppia coniugata: lavorando
		// gia' interamente in Complex, le due voci convergono direttamente a due 1x1 sulla diagonale.
		SquareMatrix<Real> N = SquareMatrixElementFactory.of(R, 0.0, -1.0, 1.0, 0.0);

		SolverResult<EigenDecomposition> qrComplexPairResult = new QREigenvalueSolver<Real>().solve(N, params);
		System.out.println("stato = " + qrComplexPairResult.getStatus() + ", iterazioni = " + qrComplexPairResult.getIterationsExecuted());
		EigenDecomposition qrComplexPairDecomposition = qrComplexPairResult.getValue();
		System.out.println("autovalori = " + qrComplexPairDecomposition.getEigenvalues());
		System.out.println("autovettori (genuinamente complessi anche se N e' reale) = " + qrComplexPairDecomposition.getEigenvectors());
		// toRealDecomposition() qui lancerebbe: gli autovalori sono genuinamente complessi, non e'
		// un errore numerico da tollerare con una tolleranza piu' larga.

		System.out.println("\n=== Caso 5: matrice difettiva, autovalore ripetuto senza base completa (QREigenvalueSolver) ===");
		// J = [[1,1],[0,1]] -- blocco di Jordan: autovalore doppio 1, un solo autovettore indipendente.
		// Non e' un errore del solver ne' un caso limite da rifiutare: la matrice non e' diagonalizzabile
		// per costruzione. QREigenvalueSolver restituisce comunque un risultato (il secondo autovettore
		// e' la miglior approssimazione disponibile, non quello esatto, che semplicemente non esiste).
		SquareMatrix<Real> J = SquareMatrixElementFactory.of(R, 1.0, 1.0, 0.0, 1.0);

		SolverResult<EigenDecomposition> qrDefectiveResult = new QREigenvalueSolver<Real>().solve(J, params);
		System.out.println("stato = " + qrDefectiveResult.getStatus() + ", iterazioni = " + qrDefectiveResult.getIterationsExecuted());
		System.out.println("autovalori (ripetuti) = " + qrDefectiveResult.getValue().getEigenvalues());

		System.out.println("\n=== Stesse matrici, via GeneralEigenvalueSolver (dispatcher unico) ===");
		// Il dispatcher instrada da solo: simmetrica/hermitiana -> Jacobi/Hermitian, qualunque
		// altra cosa (K = Real o K = Complex) -> QREigenvalueSolver. Nessun ramo lancia piu'
		// UnsupportedOperationException per una matrice non simmetrica.
		EigenvalueSolver<Real> realDispatcher = new GeneralEigenvalueSolver<>();
		EigenvalueSolver<Complex> complexDispatcher = new GeneralEigenvalueSolver<>();
		System.out.println("caso 1 (simmetrica), via dispatcher      = " + realDispatcher.solve(A, params).getValue().getEigenvalues());
		System.out.println("caso 2 (hermitiana), via dispatcher      = " + complexDispatcher.solve(H, params).getValue().getEigenvalues());
		System.out.println("caso 3 (non simmetrica), via dispatcher  = " + realDispatcher.solve(G, params).getValue().getEigenvalues());
		System.out.println("caso 4 (coppia complessa), via dispatcher = " + realDispatcher.solve(N, params).getValue().getEigenvalues());
	}
}
