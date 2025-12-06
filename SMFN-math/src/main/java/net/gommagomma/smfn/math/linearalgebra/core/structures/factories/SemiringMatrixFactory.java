package net.gommagomma.smfn.math.linearalgebra.core.structures.factories;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.SemiringMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;


public interface SemiringMatrixFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>>
{
	SemimoduleVectorFactory<K, V> getVectorFactory();

	default NumericFactory<K> getScalarFactory() {
        return getVectorFactory().getScalarFactory();
    }

	M createMatrix(K[][] data); 
	M createZeroMatrix(int rows, int cols); 

//	default M createIdentity(int size) {
//        K one = getScalarFactory(.one(); 
//        M matrix = createZeroMatrix(size, size);
//        for (int i = 0; i < size; i++) {
//            matrix.set(i, i, one);
//        }
//
//        return matrix;
//    }

	default M createMatrix(double[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0);
        }
        int cols = data[0].length;

        NumericFactory<K> scalarFactory = getScalarFactory();
        
        K[][] components = (K[][]) new SemiringElement[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                 throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < cols; j++) {
                components[i][j] = scalarFactory.fromDouble(data[i][j]);
            }
        }

        return createMatrix(components);
    }
}
