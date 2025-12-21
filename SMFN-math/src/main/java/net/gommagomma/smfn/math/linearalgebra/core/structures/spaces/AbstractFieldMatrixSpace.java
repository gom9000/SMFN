package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.FieldMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;

public abstract class AbstractFieldMatrixSpace<K extends FieldElement<K, ?>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>>
extends AbstractRingMatrixSpace<K, V, M>
implements FieldMatrixSpace<K, V, M> 
{
    protected AbstractFieldMatrixSpace(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public abstract Field<K, ?> getScalarStructure();

    @Override
    public abstract VectorSpace<K, V> getVectorStructure();
}