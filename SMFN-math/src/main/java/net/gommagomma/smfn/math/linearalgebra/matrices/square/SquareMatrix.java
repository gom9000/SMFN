package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixModule;

public final class SquareMatrix<K extends ScalarElement<K>> 
extends Matrix<K>
implements ScalarElement<SquareMatrix<K>>
{
	protected SquareMatrix(ScalarStructure<SquareMatrix<K>> matrixStructure, ScalarStructure<K> scalarStructure, int n, K[] data) {
		super(matrixStructure, scalarStructure, n, n, data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ScalarStructure<SquareMatrix<K>> getStructure() {
		// Qui il cast è sicuro: SquareMatrix è sempre creata da una ScalarStructure (Ring/Field)
		return (ScalarStructure<SquareMatrix<K>>) matrixStructure;
	}

	public int getN() { return getRows(); }

    /**
     * Metodo di convenienza per la traccia, tipico delle matrici quadrate.
     */
    public K trace() {
        K total = getScalarStructure().zero();
        for (int i = 0; i < getN(); i++) {
            total = getScalarStructure().add(total, get(i, i));
        }
        return total;
    }
}
