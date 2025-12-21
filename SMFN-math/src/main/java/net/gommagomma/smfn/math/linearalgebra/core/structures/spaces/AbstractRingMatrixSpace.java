package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.RingMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.ModuleElement;

public abstract class AbstractRingMatrixSpace<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>>
extends AbstractSemiringMatrixSpace<K, V, M>
implements RingMatrixModule<K, V, M> 
{
    protected AbstractRingMatrixSpace(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public abstract Ring<K> getScalarStructure();

    @Override
    public abstract Module<K, V> getVectorStructure();
}