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
 */
public final class EigenDecomposition
{
	private static final RealField R = RealField.INSTANCE;

	private final List<Complex> eigenvalues;
	private final List<Vector<Complex>> eigenvectors;

	public EigenDecomposition(List<Complex> eigenvalues, List<Vector<Complex>> eigenvectors) {
		this.eigenvalues = List.copyOf(eigenvalues);
		this.eigenvectors = List.copyOf(eigenvectors);
	}

	private double maxEigenvalueModulus() {
		double max = 0.0;
		for (Complex c : eigenvalues) max = Math.max(max, c.modulus());
		return max;
	}

	private static double maxComponentModulus(Vector<Complex> v) {
		double max = 0.0;
		for (int i = 0; i < v.size(); i++) max = Math.max(max, v.get(i).modulus());
		return max;
	}

	public List<Complex> getEigenvalues() { return eigenvalues; }
	public List<Vector<Complex>> getEigenvectors() { return eigenvectors; }


	/**
	 * Gli autovalori come numeri reali.
	 *
	 * @param tolerance la parte immaginaria massima ammessa, relativa al massimo modulo degli autovalori
	 * @throws IllegalStateException se un autovalore ha parte immaginaria oltre la tolleranza
	 */
	public List<Real> getRealEigenvalues(Real tolerance) {
		double tol = tolerance.getValue() * maxEigenvalueModulus();
		List<Real> result = new ArrayList<>(eigenvalues.size());
		for (Complex c : eigenvalues) {
			if (Math.abs(c.getIm()) > tol) {
				throw new IllegalStateException("Decomposition contains a complex eigenvalue exceeding tolerance: " + c);
			}
			result.add(new Real(c.getRe()));
		}
		return result;
	}

	/**
	 * La decomposizione reale.
	 *
	 * @param tolerance la parte immaginaria massima ammessa, relativa al massimo modulo degli
	 *                  autovalori per gli autovalori e al massimo modulo delle componenti del
	 *                  vettore per ciascun autovettore
	 * @throws IllegalStateException se un autovalore o un autovettore ha parte immaginaria oltre la tolleranza
	 */
	public RealEigenDecomposition toRealDecomposition(Real tolerance) {
		double tol = tolerance.getValue() * maxEigenvalueModulus();

		for (Complex c : eigenvalues) {
			if (Math.abs(c.getIm()) > tol) {
				throw new IllegalStateException("Decomposition contains a complex eigenvalue exceeding tolerance: " + c);
			}
		}
		for (Vector<Complex> v : eigenvectors) {
			double componentTol = tolerance.getValue() * maxComponentModulus(v);
			for (int i = 0; i < v.size(); i++) {
				if (Math.abs(v.get(i).getIm()) > componentTol) {
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
