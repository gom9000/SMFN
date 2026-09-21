package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Risultato di un EigenvalueSolver: autovalori e autovettori, in ordine
 * corrispondente (l'autovalore in posizione i va con l'autovettore in
 * posizione i).
 *
 * Non generica su K, deliberatamente: sia gli autovalori sia gli
 * autovettori vivono sempre in Complex, a prescindere dal tipo scalare
 * della matrice di partenza -- Complex e' chiuso algebricamente, copre
 * senza perdita qualunque caso (una matrice reale non simmetrica puo'
 * avere autovalori E autovettori genuinamente complessi).
 *
 * Un solo costruttore: chi produce risultati genuinamente reali (Jacobi)
 * avvolge lui stesso i valori in Complex(v,0) prima di costruire -- non e'
 * compito di questa classe scegliere tra "versione reale" e "versione
 * complessa" della propria costruzione.
 *
 * toRealDecomposition(tolerance) e' l'unico modo per ottenere la vista
 * reale: verifica autovalori E autovettori insieme, con la stessa
 * tolleranza, e li restituisce gia' accoppiati in un RealEigenDecomposition
 * -- non tronca mai silenziosamente una componente genuinamente complessa.
 */
public final class EigenDecomposition
{
	private static final RealField R = RealField.INSTANCE;

	private final List<Complex> eigenvalues;
	private final List<Vector<Complex>> eigenvectors;

	public EigenDecomposition(List<Complex> eigenvalues, List<Vector<Complex>> eigenvectors) {
		this.eigenvalues = eigenvalues;
		this.eigenvectors = eigenvectors;
	}

	public List<Complex> getEigenvalues() { return eigenvalues; }
	public List<Vector<Complex>> getEigenvectors() { return eigenvectors; }

	public RealEigenDecomposition toRealDecomposition(Real tolerance) {
		double tol = tolerance.getValue();

		for (Complex c : eigenvalues) {
			if (Math.abs(c.getIm()) > tol) {
				throw new IllegalStateException("Decomposition contains a complex eigenvalue exceeding tolerance: " + c);
			}
		}
		for (Vector<Complex> v : eigenvectors) {
			for (int i = 0; i < v.size(); i++) {
				if (Math.abs(v.get(i).getIm()) > tol) {
					throw new IllegalStateException("Decomposition contains a complex eigenvector component exceeding tolerance: " + v);
				}
			}
		}

		List<Real> realEigenvalues = new ArrayList<>(eigenvalues.size());
		for (Complex c : eigenvalues) {
			realEigenvalues.add(new Real(c.getRe()));
		}

		List<Vector<Real>> realEigenvectors = new ArrayList<>(eigenvectors.size());
		for (Vector<Complex> v : eigenvectors) {
			int n = (int) v.size();
			VectorSpace<Real, RealField> space = new VectorSpace<>(R, n);
			Real[] data = new Real[n];
			for (int i = 0; i < n; i++) {
				data[i] = new Real(v.get(i).getRe());
			}
			realEigenvectors.add(VectorElementFactory.of(space, List.of(data)));
		}

		return new RealEigenDecomposition(realEigenvalues, realEigenvectors);
	}
}
