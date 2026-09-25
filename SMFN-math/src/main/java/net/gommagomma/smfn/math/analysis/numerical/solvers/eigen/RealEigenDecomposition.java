package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Vista puramente reale di una EigenDecomposition.
 */
public final class RealEigenDecomposition
{
	private final List<Real> eigenvalues;
	private final List<Vector<Real>> eigenvectors;

	RealEigenDecomposition(List<Real> eigenvalues, List<Vector<Real>> eigenvectors) {
		this.eigenvalues = eigenvalues;
		this.eigenvectors = eigenvectors;
	}

	public List<Real> getEigenvalues() { return eigenvalues; }
	public List<Vector<Real>> getEigenvectors() { return eigenvectors; }
}
