package net.gommagomma.smfn.math.linearalgebra.rational;

import java.util.Arrays;
import java.util.Comparator;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.AbstractFieldMatrix;

/**
 * Rappresenta una matrice di numeri razionali immutabile, fornendo precisione esatta.
 * Aderisce all'interfaccia Matrix<Rational, RationalVector, RationalMatrix>.
 */
public final class RationalMatrix
extends AbstractFieldMatrix<Rational, RationalVector, RationalMatrix, RationalMatrixFactory>
{    
	private static final RationalMatrixFactory FACTORY_INSTANCE = RationalMatrixFactory.getInstance();


	/**
     * Costruttore principale per creare un RationalMatrix da un array bidimensionale di componenti.
     * @param data L'array di componenti Real.
     */
	public RationalMatrix(Rational[][] data) {
        super(data, FACTORY_INSTANCE);
    }
    
	/**
     * Costruttore per creare una matrice di zeri di dimensioni specifiche.
     * Usato internamente per getZero(), identity(), ecc.
     */
	RationalMatrix(int rows, int cols) {
		super(rows, cols, FACTORY_INSTANCE);
		for (int i = 0; i < rows; i++) {
			Arrays.fill(this.data[i], Rational.ZERO);
		}
	}

    @Override
    protected Class<Rational> getScalarClass() {
        return Rational.class;
    }

    @Override
    protected Comparator<Rational> getMagnitudeComparator() {
        return Comparator.naturalOrder(); 
    }


    // MatrixElement impls

    @Override
    public RationalVector getRowVector(int row)
    {
    	if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }

        return factory.createVector(Arrays.copyOf(data[row], cols));
	}

	@Override
	public RationalVector getColumnVector(int col)
	{
		if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }

        Rational[] columnData = new Rational[rows];
        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][col];
        }

        return factory.createVector(columnData);
	}


	// Helper Methods

    public static RationalMatrix identity(int size)
    {
        if (size <= 0) {
            throw new IllegalArgumentException("Dimension must be positive.");
        }

        RationalMatrix identityMatrix = new RationalMatrix(size, size);
        for (int i = 0; i < size; i++) {
             identityMatrix.data[i][i] = Rational.ONE;
        }

        return identityMatrix; 
    }
}
