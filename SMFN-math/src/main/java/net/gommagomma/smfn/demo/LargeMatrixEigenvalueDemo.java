package net.gommagomma.smfn.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.JacobiEigenvalueSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.QREigenvalueSolver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * QREigenvalueSolver su una matrice n x n non piu' piccola, con un confronto onesto
 * di costo contro JacobiEigenvalueSolver sullo stesso problema.
 * <p>
 * La matrice e' il Laplaciano 1D discreto (Toeplitz tridiagonale: 2 sulla diagonale,
 * -1 sulle due sottodiagonali adiacenti): simmetrica, quindi GeneralEigenvalueSolver la
 * instraderebbe a JacobiEigenvalueSolver, non a QREigenvalueSolver -- per questo qui i
 * due solver sono chiamati direttamente, non tramite il dispatcher, cosi' da vederli
 * entrambi all'opera sullo stesso problema invece che uno solo di nascosto.
 * <p>
 * I suoi autovalori hanno una forma chiusa nota, {@code 2 - 2*cos(k*pi/(n+1))} per
 * k = 1..n: una verifica indipendente forte quanto quella numerica con numpy usata
 * altrove in questa libreria, ma calcolabile qui direttamente in Java senza uscire dal
 * processo.
 */
public class LargeMatrixEigenvalueDemo
{
	public static void main(String[] args) {
		RealField R = RealField.INSTANCE;
		int n = 50;

		System.out.println("=== Laplaciano 1D discreto " + n + "x" + n + " (Toeplitz tridiagonale, simmetrica) ===");
		SquareMatrix<Real> A = buildTridiagonalLaplacian(R, n);

		System.out.println("\n--- QREigenvalueSolver (chiamato direttamente: non sfrutta la simmetria) ---");
		StoppingParameters qrParams = new StoppingParameters(new Real(1e-10), 1000);
		long qrStart = System.currentTimeMillis();
		SolverResult<EigenDecomposition> qrSolverResult = new QREigenvalueSolver<Real>().solve(A, qrParams);
		long qrElapsed = System.currentTimeMillis() - qrStart;
		System.out.println("stato = " + qrSolverResult.getStatus()
			+ ", iterazioni = " + qrSolverResult.getIterationsExecuted()
			+ ", tempo = " + qrElapsed + " ms");

		EigenDecomposition qrDecomposition = qrSolverResult.getValue();
		List<Double> qrEigenvalues = sortedRealParts(qrDecomposition.getEigenvalues());
		List<Double> closedForm = closedFormLaplacianEigenvalues(n);
		System.out.printf("massima differenza dagli autovalori noti in forma chiusa: %.3e%n",
			maxAbsDiff(qrEigenvalues, closedForm));
		System.out.printf("massimo residuo |A*v - lambda*v| sui %d autovettori: %.3e%n",
			n, maxDefiningEquationResidual(A, qrDecomposition));

		System.out.println("\n--- JacobiEigenvalueSolver (chiamato direttamente, per confronto sullo stesso problema) ---");
		// Con lo stesso budget di 1000 iterazioni usato sopra, Jacobi su n=50 NON converge:
		// ogni sua iterazione azzera una sola coppia di elementi fuori diagonale, quindi il
		// numero di iterazioni necessarie cresce con n (qui ne servono circa 4000), mentre
		// QREigenvalueSolver ne usa circa 150. QREigenvalueSolver riduce prima a forma di
		// Hessenberg (HessenbergReduction, O(n^3) una tantum) e poi ogni sua iterazione costa
		// O(n^2) sfruttando quella struttura -- non piu' O(n^3) come su una matrice densa --
		// ma resta comunque piu' caro della singola iterazione O(n) di Jacobi. Risultato
		// empirico su questa macchina: Jacobi arriva a convergenza vera in circa 20 ms, QR in
		// circa 90 ms: un divario molto piu' piccolo di quello (circa 30x) che si vedrebbe
		// senza la riduzione a Hessenberg, ma Jacobi resta comunque piu' economico qui perche'
		// sfrutta la simmetria che QREigenvalueSolver ignora deliberatamente. Per una matrice
		// comunque simmetrica, GeneralEigenvalueSolver sceglie Jacobi da solo -- ecco perche'
		// conviene lasciarlo fare.
		StoppingParameters jacobiParams = new StoppingParameters(new Real(1e-10), 5000);
		long jacobiStart = System.currentTimeMillis();
		SolverResult<EigenDecomposition> jacobiSolverResult = new JacobiEigenvalueSolver().solve(A, jacobiParams);
		long jacobiElapsed = System.currentTimeMillis() - jacobiStart;
		System.out.println("stato = " + jacobiSolverResult.getStatus()
			+ ", iterazioni = " + jacobiSolverResult.getIterationsExecuted()
			+ ", tempo = " + jacobiElapsed + " ms");

		List<Double> jacobiEigenvalues = sortedRealParts(jacobiSolverResult.getValue().getEigenvalues());
		System.out.printf("massima differenza QR vs Jacobi sugli stessi autovalori: %.3e%n",
			maxAbsDiff(qrEigenvalues, jacobiEigenvalues));
	}

	/** A_ii = 2, A_i,i+-1 = -1, altrove 0: il Laplaciano 1D discreto con condizioni al contorno di Dirichlet. */
	private static SquareMatrix<Real> buildTridiagonalLaplacian(RealField R, int n) {
		List<Real> data = new ArrayList<>(n * n);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j) {
					data.add(new Real(2.0));
				} else if (Math.abs(i - j) == 1) {
					data.add(new Real(-1.0));
				} else {
					data.add(new Real(0.0));
				}
			}
		}
		return SquareMatrixElementFactory.of(R, data);
	}

	private static List<Double> closedFormLaplacianEigenvalues(int n) {
		List<Double> values = new ArrayList<>(n);
		for (int k = 1; k <= n; k++) {
			values.add(2.0 - 2.0 * Math.cos(k * Math.PI / (n + 1)));
		}
		Collections.sort(values);
		return values;
	}

	private static List<Double> sortedRealParts(List<Complex> eigenvalues) {
		List<Double> values = new ArrayList<>(eigenvalues.size());
		for (Complex c : eigenvalues) {
			values.add(c.getRe());
		}
		Collections.sort(values);
		return values;
	}

	private static double maxAbsDiff(List<Double> a, List<Double> b) {
		double max = 0.0;
		for (int i = 0; i < a.size(); i++) {
			max = Math.max(max, Math.abs(a.get(i) - b.get(i)));
		}
		return max;
	}

	/**
	 * Verifica indipendente A*v == lambda*v per ciascuna coppia autovalore/autovettore, senza
	 * fidarsi del solver stesso: stesso schema di QREigenvalueSolverTest, generalizzato a n
	 * qualunque. A e' reale, v e lambda sono Complex (EigenDecomposition li modella sempre cosi',
	 * anche per una matrice reale): il prodotto A*v va quindi fatto a mano elemento per elemento,
	 * incorporando ogni A_ij reale in Complex con parte immaginaria nulla.
	 */
	private static double maxDefiningEquationResidual(SquareMatrix<Real> A, EigenDecomposition decomposition) {
		int n = A.getN();
		List<Complex> eigenvalues = decomposition.getEigenvalues();
		List<Vector<Complex>> eigenvectors = decomposition.getEigenvectors();
		double maxResidual = 0.0;

		for (int idx = 0; idx < eigenvalues.size(); idx++) {
			Complex lambda = eigenvalues.get(idx);
			Vector<Complex> v = eigenvectors.get(idx);
			for (int i = 0; i < n; i++) {
				double sumRe = 0.0, sumIm = 0.0;
				for (int j = 0; j < n; j++) {
					double aij = A.get(i, j).getValue();
					Complex vj = v.get(j);
					sumRe += aij * vj.getRe();
					sumIm += aij * vj.getIm();
				}
				Complex vi = v.get(i);
				double expectedRe = lambda.getRe() * vi.getRe() - lambda.getIm() * vi.getIm();
				double expectedIm = lambda.getRe() * vi.getIm() + lambda.getIm() * vi.getRe();
				double residual = Math.hypot(sumRe - expectedRe, sumIm - expectedIm);
				maxResidual = Math.max(maxResidual, residual);
			}
		}
		return maxResidual;
	}
}
