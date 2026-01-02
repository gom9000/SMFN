package net.gommagomma.smfn.math.linearalgebra.vectors;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.LinearSpace;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public class VectorSpace<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
extends VectorModule<K, S>
implements LinearSpace<Vector<K>, K, S>
{
	public VectorSpace(S scalarStructure, int dimension) {
        super(scalarStructure, dimension);
    }
}
