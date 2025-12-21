package net.gommagomma.smfn.math.linearalgebra.core.structures.specialized;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.SemiringMatrixSemimodule;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.VectorSpace;
import net.gommagomma.smfn.math.linearalgebra.real.RealMatrix;
import net.gommagomma.smfn.math.linearalgebra.real.RealMatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;

public final class RealMatrixRing
extends AbstractMatrixRing<Real, RealVector, RealMatrix> 
{
    private final RealMatrixSpace baseSpace; // Delegato per i metodi factory di FieldMatrixSpace

    public RealMatrixRing(int n) {
        super(n); // Inizializza n x n
        this.baseSpace = new RealMatrixSpace(n, n);
    }
    
    // --- Delega a RealMatrixSpace per i metodi base ---

    @Override
    public Field<Real, Natural> getScalarStructure() {
        return baseSpace.getScalarStructure();
    }

    @Override
    public VectorSpace<Real, RealVector> getVectorStructure() {
        return baseSpace.getVectorStructure();
    }
    
    @Override
    public String getName() {
        return "Ring of " + n + "x" + n + " Real Matrices";
    }


    @Override
    public RealMatrix createMatrix(Real[][] data) {
        return baseSpace.createMatrix(data);
    }

	@Override
	public RealMatrix createMatrix(double[][] data) {
		return baseSpace.createMatrix(data);
	}

	@Override
	public RealMatrix createMatrix(long[][] data) {
		return baseSpace.createMatrix(data);
	}

	@Override
	public RealMatrix createMatrix(int[][] data) {
		return baseSpace.createMatrix(data);
	}

	@Override
	public RealMatrix createZeroMatrix(int rows, int cols) {
		return baseSpace.createZeroMatrix(rows, cols);
	}

    
    // --- Implementazione specifica di Ring ---

    @Override
    public RealMatrix getZero() {
        return baseSpace.createZeroMatrix(n, n);
    }
    
    @Override
    public RealMatrix getIdentity() {
        Real[][] data = new Real[n][n];
        Real zero = RealField.getInstance().zero();
        Real one = RealField.getInstance().one();
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                data[i][j] = (i == j) ? one : zero;
            }
        }
        return baseSpace.createMatrix(data);
    }
    
   // @Override
    public RealMatrix of(double value) {
        Real scalar = getScalarStructure().of(value);
        RealMatrix identity = getIdentity();
        // Assume che RealMatrix abbia un metodo per moltiplicare per scalare
        return identity.multiplyByScalar(scalar); 
    }
    
    // Implementa of(long) e of(int) delegando a of(double)
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
	public SemiringMatrixSemimodule<Real, RealVector, RealMatrix> getSpaceOfDimensions(int rows, int cols) {
		// TODO Auto-generated method stub
		return null;
	}
    

}
