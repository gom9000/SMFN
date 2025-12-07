package net.gommagomma.smfn.math.linearalgebra.core.factories;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.SemiringMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;


public interface SemiringMatrixFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>>
{
	SemimoduleVectorFactory<K, V> getVectorFactory();

	default NumericFactory<K> getScalarFactory() {
        return getVectorFactory().getScalarFactory();
    }

	M createMatrix(K[][] data); 
	M createMatrix(double[][] data);
	M createMatrix(long[][] data);
	M createMatrix(int[][] data);
	M createZeroMatrix(int rows, int cols); 

	default V createVector(K[] data) { return getVectorFactory().createVector(data); }
	default K getZeroScalar() { return getScalarFactory().zero(); }
    default K getOneScalar() { return getScalarFactory().one(); }
}
