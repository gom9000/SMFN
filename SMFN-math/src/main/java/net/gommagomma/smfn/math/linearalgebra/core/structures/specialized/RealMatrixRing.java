package net.gommagomma.smfn.math.linearalgebra.core.structures.specialized;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.SemiringMatrixSemimodule;
import net.gommagomma.smfn.math.linearalgebra.real.RealMatrix;
import net.gommagomma.smfn.math.linearalgebra.real.RealMatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;
import net.gommagomma.smfn.math.linearalgebra.real.RealVectorSpace;

public final class RealMatrixRing
extends AbstractMatrixRing<Real, RealVector, RealMatrix> 
{
    private final RealMatrixSpace baseSpace;

    public RealMatrixRing(int n) {
        super(n); 
        this.baseSpace = new RealMatrixSpace(n, n);
    }

    @Override
    public String getName() {
        return "Ring of " + n + "x" + n + " Real Matrices";
    }

    @Override
    public RealField getScalarStructure() {
        return baseSpace.getScalarStructure();
    }

    @Override
    public RealVectorSpace getVectorStructure() {
        return baseSpace.getVectorStructure();
    }

    // --- Metodi Factory: Basta delegare una volta sola ---

    @Override
    public RealMatrix createMatrix(Real[][] data) { return baseSpace.createMatrix(data); }

    @Override
    public RealMatrix createMatrix(double[][] data) { return baseSpace.createMatrix(data); }

    @Override
    public RealMatrix createMatrix(long[][] data) { return baseSpace.createMatrix(data); }

    @Override
    public RealMatrix createMatrix(int[][] data) { return baseSpace.createMatrix(data); }

    @Override
    public RealMatrix createZeroMatrix(int rows, int cols) { return baseSpace.createZeroMatrix(rows, cols); }

    // --- Implementazione specifica di Ring ---

    @Override
    public RealMatrix getZero() {
        return baseSpace.createZeroMatrix(n, n);
    }

    @Override
    public RealMatrix getIdentity() {
        // Molto più pulito: usiamo la factory che abbiamo appena perfezionato
        return baseSpace.createIdentityMatrix(n);
    }

    @Override
    public RealMatrix of(double value) {
        Real scalar = getScalarStructure().of(value);
        return getIdentity().scale(scalar); 
    }

    @Override
    public RealMatrix of(long value) {
        return of((double) value);
    }

    @Override
    public RealMatrix of(int value) {
        return of((double) value);
    }

	@Override
	public boolean contains(RealMatrix e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RealMatrix createIdentityMatrix(int dimension) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SemiringMatrixSemimodule<Real, RealVector, RealMatrix> getSpaceOfDimensions(int rows, int cols) {
		// TODO Auto-generated method stub
		return null;
	}
}