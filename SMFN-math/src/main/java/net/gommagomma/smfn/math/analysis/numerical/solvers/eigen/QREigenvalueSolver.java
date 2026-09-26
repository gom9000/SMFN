package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.solvers.BasicSolverResult;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.TerminationStatus;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.operators.HessenbergReduction;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Autovalori e autovettori di una matrice quadrata qualunque (K = Real o Complex),
 * senza richiedere simmetria/hermitianita': algoritmo QR con shift di Wilkinson,
 * interamente in aritmetica complessa.
 */
public final class QREigenvalueSolver<K extends ScalarElement<K>>
implements EigenvalueSolver<K>
{
	private static final ComplexField C = ComplexField.INSTANCE;

	@Override
	public SolverResult<EigenDecomposition> solve(SquareMatrix<K> matrix, StoppingParameters params) {
		int n = matrix.getN();
		Complex[][] initial = toComplexArray(matrix, n);

		HessenbergReduction.Result reduction = HessenbergReduction.reduce(initial, n);
		Complex[][] a = reduction.h;
		Complex[][] q = reduction.q;

		double tolerance = params.tolerance.getValue();
		int m = n;
		int iterationsUsed = 0;
		TerminationStatus status = TerminationStatus.CONVERGED;

		outer:
		while (m > 1) {
			boolean deflatedThisRound = false;

			for (int it = 0; it < params.maxIterations; it++) {
				iterationsUsed++;

				if (hasDeflatedTrailingEntry(a, m, tolerance)) {
					deflatedThisRound = true;
					m--;
					break;
				}

				Complex shift = wilkinsonShift(a, m);
				hessenbergQrStep(a, q, m, n, shift);

				if (iterationsUsed >= params.maxIterations && !hasDeflatedTrailingEntry(a, m, tolerance)) {
					status = TerminationStatus.MAX_ITERATIONS_REACHED;
					break outer;
				}
			}

			if (!deflatedThisRound) {
				status = TerminationStatus.MAX_ITERATIONS_REACHED;
				break;
			}
		}

		EigenDecomposition decomposition = buildResult(a, q, n);
		return new BasicSolverResult<>(decomposition, status, iterationsUsed);
	}

	// --- Estrazione degli autovalori/autovettori dalla forma (quasi) triangolare finale ---

	private EigenDecomposition buildResult(Complex[][] t, Complex[][] q, int n) {
		List<Complex> eigenvalues = new ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			eigenvalues.add(t[i][i]);
		}

		List<Vector<Complex>> eigenvectors = new ArrayList<>(n);
		VectorSpace<Complex, ComplexField> space = new VectorSpace<>(C, n);

		for (int j = 0; j < n; j++) {
			Complex[] y = backSubstitute(t, n, j);
			Complex[] eigenvectorData = multiply(q, y, n);
			normalize(eigenvectorData, n);
			eigenvectors.add(VectorElementFactory.of(space, List.of(eigenvectorData)));
		}

		return new EigenDecomposition(eigenvalues, eigenvectors);
	}

	/**
	 * Risolve (T - lambda_j*I) y = 0 per sostituzione all'indietro, con y[j] = 1 come
	 * riferimento e y[i] per i > j pari a zero (T e' triangolare superiore: l'autovettore
	 * dell'autovalore in posizione j non ha componenti sotto j nella base di Schur).
	 * Un pivot quasi nullo (autovalore ripetuto/matrice difettiva) viene sostituito con
	 * un valore piccolo non nullo: risultato migliore disponibile, non un'eccezione --
	 * coerente con la scelta di non far fallire il solutore su un caso limite algebrico.
	 */
	private Complex[] backSubstitute(Complex[][] t, int n, int j) {
		Complex lambda = t[j][j];
		Complex[] y = new Complex[n];
		for (int i = 0; i < n; i++) {
			y[i] = C.zero();
		}
		y[j] = C.one();

		for (int i = j - 1; i >= 0; i--) {
			Complex sum = C.zero();
			for (int k = i + 1; k <= j; k++) {
				sum = C.add(sum, C.multiply(t[i][k], y[k]));
			}
			Complex denom = C.subtract(t[i][i], lambda);
			if (denom.modulus() < 1e-6) {
				// Autovalore ripetuto/quasi ripetuto (matrice difettiva): il pivot e' quasi nullo.
				// Non esiste in questo caso un autovettore esatto da recuperare per sostituzione
				// all'indietro -- si sostituisce un pivot piccolo ma non nullo (piu' grande della
				// tolleranza approssimata di ComplexField, altrimenti divide() lo tratterebbe
				// comunque come zero) per restituire il miglior risultato disponibile.
				denom = new Complex(1e-6, 0.0);
			}
			y[i] = C.negate(divideRaw(sum, denom));
		}
		return y;
	}

	/*
	 * Divisione complessa "grezza", senza passare da ComplexField.divide()/inverse():
	 * quei metodi rifiutano un denominatore vicino a zero secondo l'uguaglianza
	 * approssimata di ComplexField (MathConstants.EPSILON = 1E-12), ma qui il
	 * denominatore e' deliberatamente un pivot piccolo-ma-non-nullo (vedi
	 * backSubstitute) scelto apposta per essere piu' grande di quell'epsilon:
	 * va diviso davvero, non trattato come zero.
	 */
	private Complex divideRaw(Complex a, Complex b) {
		double br = b.getRe();
		double bi = b.getIm();
		double denom = br * br + bi * bi;
		double re = (a.getRe() * br + a.getIm() * bi) / denom;
		double im = (a.getIm() * br - a.getRe() * bi) / denom;
		return new Complex(re, im);
	}

	private void normalize(Complex[] v, int n) {
		double sumSquares = 0.0;
		for (int i = 0; i < n; i++) {
			sumSquares += v[i].modulusSquared();
		}
		double norm = Math.sqrt(sumSquares);
		if (norm < 1e-300) {
			return;
		}
		for (int i = 0; i < n; i++) {
			v[i] = new Complex(v[i].getRe() / norm, v[i].getIm() / norm);
		}
	}

	// --- Un passo dell'iterazione QR con shift, su una matrice Hessenberg ---

	/**
	 * Criterio di deflazione: l'ultima riga attiva (sotto la diagonale) e' trascurabile.
	 * Basterebbe controllare il solo a[m-1][m-2], perche' hessenbergQrStep preserva la
	 * struttura di Hessenberg (a meno di arrotondamento): tutto il resto della riga e' gia'
	 * strutturalmente zero. Si controlla comunque l'intera riga, costo trascurabile (O(m),
	 * contro l'O(n*m) di un intero passo QR) rispetto alla garanzia in piu' che da': non
	 * fidarsi ciecamente del fatto che l'arrotondamento non abbia introdotto valori residui
	 * altrove.
	 */
	private boolean hasDeflatedTrailingEntry(Complex[][] a, int m, double tolerance) {
		if (m < 2) {
			return true;
		}
		double diagonalScale = a[m - 1][m - 1].modulus();
		for (int k = 0; k < m - 1; k++) {
			double neighbourScale = a[k][k].modulus() + diagonalScale;
			if (a[m - 1][k].modulus() >= tolerance * neighbourScale + tolerance) {
				return false;
			}
		}
		return true;
	}

	/** Shift di Wilkinson: l'autovalore del blocco 2x2 finale piu' vicino a a[m-1][m-1]. */
	private Complex wilkinsonShift(Complex[][] a, int m) {
		if (m == 1) {
			return a[0][0];
		}
		Complex p = a[m - 2][m - 2];
		Complex q = a[m - 2][m - 1];
		Complex r = a[m - 1][m - 2];
		Complex s = a[m - 1][m - 1];

		Complex trace = C.add(p, s);
		Complex det = C.subtract(C.multiply(p, s), C.multiply(q, r));
		Complex discriminant = C.subtract(C.multiply(trace, trace), C.multiply(new Complex(4.0, 0.0), det)).sqrt();

		Complex mu1 = C.multiply(new Complex(0.5, 0.0), C.add(trace, discriminant));
		Complex mu2 = C.multiply(new Complex(0.5, 0.0), C.subtract(trace, discriminant));

		double d1 = C.subtract(mu1, s).modulus();
		double d2 = C.subtract(mu2, s).modulus();
		return d1 <= d2 ? mu1 : mu2;
	}

	/**
	 * Un passo di iterazione QR con shift sul blocco attivo Hessenberg a[0..m-1][0..m-1]
	 * (le righe/colonne m..n-1 sono gia' deflazionate: la deflazione avviene sempre in
	 * basso a destra, quindi restano fuori da ogni operazione qui sotto), aggiornando
	 * anche la trasformazione unitaria complessiva accumulata in q (n x n).
	 */
	private void hessenbergQrStep(Complex[][] a, Complex[][] q, int m, int n, Complex shift) {
		for (int i = 0; i < m; i++) {
			a[i][i] = C.subtract(a[i][i], shift);
		}

		Complex[] v0s = new Complex[m - 1];
		Complex[] v1s = new Complex[m - 1];
		double[] vNormSquareds = new double[m - 1];
		boolean[] active = new boolean[m - 1];

		// Fattorizzazione: R = H_{m-2}*...*H_1*H_0*A, applicando ogni riflessione da sinistra
		// man mano che si procede. Le colonne a sinistra di k sono gia' azzerate sotto la
		// diagonale dai passi precedenti (o gia' zero per struttura di Hessenberg all'inizio);
		// si tocca fino a n-1, non solo m-1, per propagare l'aggiornamento anche alle colonne
		// gia' deflazionate (servono intatte per la sostituzione all'indietro finale).
		for (int k = 0; k < m - 1; k++) {
			Complex x0 = a[k][k];
			Complex x1 = a[k + 1][k];
			double normX = Math.sqrt(x0.modulusSquared() + x1.modulusSquared());
			if (normX < 1e-300) {
				active[k] = false;
				continue;
			}

			double x0Modulus = x0.modulus();
			Complex phase = x0Modulus < 1e-300 ? C.one() : new Complex(x0.getRe() / x0Modulus, x0.getIm() / x0Modulus);
			Complex alpha = C.negate(C.multiply(phase, new Complex(normX, 0.0)));

			Complex v0 = C.subtract(x0, alpha);
			Complex v1 = x1;
			double vNormSquared = v0.modulusSquared() + v1.modulusSquared();
			if (vNormSquared < 1e-300) {
				active[k] = false;
				continue;
			}

			applyPairLeft(a, v0, v1, vNormSquared, k, k, n);

			v0s[k] = v0;
			v1s[k] = v1;
			vNormSquareds[k] = vNormSquared;
			active[k] = true;
		}

		// A := R*Q, Q accumulata sia in a (per il blocco attivo) sia nella q globale:
		// si riapplicano le stesse riflessioni da destra, nello stesso ordine k=0..m-2
		// (Q = H_0*H_1*...*H_{m-2}, essendo ciascuna Householder Hermitiana e unitaria:
		// H_k^dagger = H_k).
		for (int k = 0; k < m - 1; k++) {
			if (!active[k]) {
				continue;
			}
			applyPairRight(a, v0s[k], v1s[k], vNormSquareds[k], k, 0, m);
			applyPairRight(q, v0s[k], v1s[k], vNormSquareds[k], k, 0, n);
		}

		for (int i = 0; i < m; i++) {
			a[i][i] = C.add(a[i][i], shift);
		}
	}

	/** mat[row][colStart..colEnd-1] e mat[row+1][colStart..colEnd-1] := riflessi con v=(v0,v1). */
	private void applyPairLeft(Complex[][] mat, Complex v0, Complex v1, double vNormSquared, int row, int colStart, int colEnd) {
		Complex v0Conj = v0.conjugate();
		Complex v1Conj = v1.conjugate();
		Complex scale = new Complex(2.0 / vNormSquared, 0.0);
		for (int col = colStart; col < colEnd; col++) {
			Complex dot = C.add(C.multiply(v0Conj, mat[row][col]), C.multiply(v1Conj, mat[row + 1][col]));
			Complex factor = C.multiply(scale, dot);
			mat[row][col] = C.subtract(mat[row][col], C.multiply(factor, v0));
			mat[row + 1][col] = C.subtract(mat[row + 1][col], C.multiply(factor, v1));
		}
	}

	/** mat[rowStart..rowEnd-1][col] e mat[rowStart..rowEnd-1][col+1] := riflessi con v=(v0,v1). */
	private void applyPairRight(Complex[][] mat, Complex v0, Complex v1, double vNormSquared, int col, int rowStart, int rowEnd) {
		Complex v0Conj = v0.conjugate();
		Complex v1Conj = v1.conjugate();
		Complex scale = new Complex(2.0 / vNormSquared, 0.0);
		for (int row = rowStart; row < rowEnd; row++) {
			Complex dot = C.add(C.multiply(mat[row][col], v0), C.multiply(mat[row][col + 1], v1));
			Complex factor = C.multiply(scale, dot);
			mat[row][col] = C.subtract(mat[row][col], C.multiply(factor, v0Conj));
			mat[row][col + 1] = C.subtract(mat[row][col + 1], C.multiply(factor, v1Conj));
		}
	}

	// --- Utility di algebra lineare su Complex[][] grezzi ---

	private Complex[][] toComplexArray(SquareMatrix<K> matrix, int n) {
		Complex[][] a = new Complex[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				a[i][j] = toComplex(matrix.get(i, j));
			}
		}
		return a;
	}

	private Complex toComplex(K value) {
		if (value instanceof Complex) {
			return (Complex) value;
		}
		if (value instanceof Real) {
			return new Complex(((Real) value).getValue(), 0.0);
		}
		throw new UnsupportedOperationException("QREigenvalueSolver supporta solo K = Real o K = Complex.");
	}

	private Complex[] multiply(Complex[][] a, Complex[] v, int n) {
		Complex[] result = new Complex[n];
		for (int i = 0; i < n; i++) {
			Complex sum = C.zero();
			for (int k = 0; k < n; k++) {
				sum = C.add(sum, C.multiply(a[i][k], v[k]));
			}
			result[i] = sum;
		}
		return result;
	}
}
