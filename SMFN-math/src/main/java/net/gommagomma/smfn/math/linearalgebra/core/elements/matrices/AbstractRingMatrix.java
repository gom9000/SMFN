package net.gommagomma.smfn.math.linearalgebra.core.elements.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.ModuleElement;
import net.gommagomma.smfn.math.linearalgebra.core.factories.RingMatrixFactory;


/**
 * Classe astratta di base per le matrici su un Anello (es. SignedInt).
 * Estende AbstractSemiringMatrix e implementa la logica per la negazione e la sottrazione
 * degli elementi matrice.
 */
public abstract class AbstractRingMatrix<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>, F extends RingMatrixFactory<K, V, M>>
extends AbstractSemiringMatrix<K, V, M, F>
implements RingMatrixElement<K, V, M>
{
    /**
     * Costruttore principale.
     * @param data I dati della matrice.
     * @param factory La factory specifica per il tipo K (Anello).
     */
    protected AbstractRingMatrix(K[][] data, F factory) {
        super(data, factory);
    }

    /**
     * Costruttore helper per creare matrici vuote/zero.
     * @param rows Il numero di righe.
     * @param cols Il numero di colonne.
     * @param factory La factory specifica per il tipo K (Anello).
     */
    protected AbstractRingMatrix(int rows, int cols, F factory) {
        super(rows, cols, factory);
    }


    @Override // AdditiveMonoidElement impls
    public M negate() {
        K[][] resultData = createMatrixArray(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[i][j] = this.data[i][j].negate();
			}
		}
		return factory.createMatrix(resultData);
    }
}
