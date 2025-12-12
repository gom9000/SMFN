package net.gommagomma.smfn.math.linearalgebra.core.elements.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.ModuleElement;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.RingMatrixModule;


/**
 * Classe astratta di base per le matrici su un Anello (es. SignedInt).
 * Estende AbstractSemiringMatrix e implementa la logica per la negazione e la sottrazione
 * degli elementi matrice.
 */
public abstract class AbstractRingMatrix<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>, S extends RingMatrixModule<K, V, M>>
extends AbstractSemiringMatrix<K, V, M, S>
implements RingMatrixElement<K, V, M>
{
    protected AbstractRingMatrix(K[][] data, S matrixStructure) {
        super(data, matrixStructure);
    }

    protected AbstractRingMatrix(int rows, int cols, S matrixStructure) {
        super(rows, cols, matrixStructure);
    }


    @Override // AdditiveMonoidElement impls
    public M negate() {
        K[][] resultData = createMatrixArray(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[i][j] = this.data[i][j].negate();
			}
		}
		return matrixStructure.createMatrix(resultData);
    }
}
