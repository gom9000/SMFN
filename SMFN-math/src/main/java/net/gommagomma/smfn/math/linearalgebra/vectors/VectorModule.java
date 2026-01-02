package net.gommagomma.smfn.math.linearalgebra.vectors;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Struttura ambiente che definisce un Modulo di vettori.
 * Richiede che lo scalare K appartenga a un Ring (Anello).
 * Introduce le operazioni di negazione e sottrazione.
 */
public class VectorModule<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>>
extends VectorSemimodule<K, S>
implements Module<Vector<K>, K, S>
{
    public VectorModule(S scalarStructure, int dimension) {
        super(scalarStructure, dimension);
    }

    // --- AbelianGroup ---

    @Override
    public Vector<K> negate(Vector<K> v) {
        checkDimensions(v);

        ScalarElement<?>[] resultData = new ScalarElement[dimension];
        for (int i = 0; i < dimension; i++) {
            // Delega la negazione allo scalare (es. x -> -x)
            resultData[i] = scalarStructure.negate(v.get(i));
        }

        return new Vector<>(scalarStructure, resultData);
    }

    @Override
    public Vector<K> subtract(Vector<K> a, Vector<K> b) {
        checkDimensions(a);
        checkDimensions(b);

        ScalarElement<?>[] resultData = new ScalarElement[dimension];
        for (int i = 0; i < dimension; i++) {
            // Delega la sottrazione allo scalare (es. a - b)
            resultData[i] = scalarStructure.subtract(a.get(i), b.get(i));
        }

        return new Vector<>(scalarStructure, resultData);
    }
}