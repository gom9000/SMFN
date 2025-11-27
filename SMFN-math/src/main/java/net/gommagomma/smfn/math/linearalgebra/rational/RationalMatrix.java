package net.gommagomma.smfn.math.linearalgebra.rational;

import java.util.Arrays;
import java.util.Comparator;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.AbstractMatrix;
import net.gommagomma.smfn.math.linearalgebra.core.algorithms.GaussJordanElimination;
import net.gommagomma.smfn.math.linearalgebra.core.algorithms.GaussianElimination;

/**
 * Rappresenta una matrice di numeri razionali immutabile, fornendo precisione esatta.
 * Aderisce all'interfaccia Matrix<Rational, RationalVector, RationalMatrix>.
 */
public final class RationalMatrix
extends AbstractMatrix<Rational, RationalVector, RationalMatrix, RationalMatrixFactory>
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
			Arrays.fill(this.data[i], Real.ZERO);
		}
	}

    @Override
    protected Class<Rational> getScalarClass() {
        return Rational.class;
    }


    // MatrixElement impls

	@Override
    public Rational determinant()
    {
		if (rows != cols) {
            throw new IllegalStateException("Determinant can only be calculated for square matrices.");
        }

        Comparator<Rational> rationalComparator = Comparator.naturalOrder();

        return GaussianElimination.determinant(this.data, Rational.ZERO, rationalComparator);
    }

    @Override
    public RationalMatrix inverse()
    {
    	if (rows != cols) {
    		throw new IllegalStateException("Inverse can only be calculated for square matrices.");
    	}

        Comparator<Rational> rationalComparator = Comparator.naturalOrder();
        Rational[][] invertedData = GaussJordanElimination.inverse(this.data, Rational.ZERO, Rational.ONE,rationalComparator);

        return new RationalMatrix(invertedData);
    }

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


    // Java Standard impls
    
    /**
     * WARNING: This equals method uses epsilon comparisons via Real.isEqual,
     * violating the strict transitivity contract of Object.equals() in standard Java collections.
     */
    @Override
    public final boolean equals(Object other) {
        return (other instanceof RationalMatrix) && isEqual((RationalMatrix)other);
    }
    
    @Override
    public final int hashCode() {
        int result = java.util.Objects.hash(rows, cols);
        result = 31 * result + Arrays.deepHashCode(data);
        return result;
    }
}
