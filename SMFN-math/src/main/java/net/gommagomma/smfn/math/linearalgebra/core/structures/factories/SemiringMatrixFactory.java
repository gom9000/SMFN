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

	M createMatrix(int rows, int cols);
	M createMatrix(K[][] data); 
	M createZeroMatrix(int rows, int cols); 

	default M createIdentity(int size) {
        NumericFactory<K> numFactory = getScalarFactory();
        K zero = numFactory.zero();
        K one = numFactory.one();
        
        M matrix = createMatrix(size, size);
        // Ciclo per settare la diagonale a 'one' e il resto a 'zero'
        return matrix;
    }

	default M createMatrix(double[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createMatrix(0, 0);
        }
        int cols = data[0].length;

        NumericFactory<K> scalarFactory = getScalarFactory();
        
        // 1. Creiamo l'array bidimensionale di scalari K
        @SuppressWarnings("unchecked")
        K[][] components = (K[][]) new SemiringElement[rows][cols];

        // 2. Popoliamo la matrice convertendo ogni double
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                 throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < cols; j++) {
                components[i][j] = scalarFactory.fromDouble(data[i][j]);
            }
        }
        
        // 3. Creiamo la matrice M
        return createMatrix(components);
    }
}
