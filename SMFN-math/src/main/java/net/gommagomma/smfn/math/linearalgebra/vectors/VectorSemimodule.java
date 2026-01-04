package net.gommagomma.smfn.math.linearalgebra.vectors;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.CompositeElementFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Semimodule;

import java.util.Arrays;

public class VectorSemimodule<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
implements Semimodule<Vector<K>, K, S>, CompositeElementFactory<Vector<K>, K[]>
{
    protected final S scalarStructure;
    protected final int dimension;

    public VectorSemimodule(S scalarStructure, int dimension) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("Dimension must be positive");
        }
        this.scalarStructure = scalarStructure;
        this.dimension = dimension;
    }

    @Override
    public S getScalarStructure() {
        return scalarStructure;
    }

    @Override
    public String getName() {
        return "V(" + dimension + ", " + scalarStructure.getName() + ")";
    }

    // --- AdditiveMonoid ---

    @Override
    public Vector<K> zero() {
        ScalarElement<?>[] data = new ScalarElement[dimension];
        K zeroScalar = scalarStructure.zero();
        Arrays.fill(data, zeroScalar);
        
        return new Vector<>(this, scalarStructure, data);
    }

    @Override
    public Vector<K> add(Vector<K> a, Vector<K> b) {
        checkDimensions(a);
        checkDimensions(b);

        ScalarElement<?>[] resultData = new ScalarElement[dimension];
        for (int i = 0; i < dimension; i++) {
            resultData[i] = scalarStructure.add(a.get(i), b.get(i));
        }

        return new Vector<>(this, scalarStructure, resultData);
    }

    // --- LinearStructure ---

    @Override
    public Vector<K> scale(K scalar, Vector<K> vector) {
        checkDimensions(vector);

        ScalarElement<?>[] resultData = new ScalarElement[dimension];
        for (int i = 0; i < dimension; i++) {
            resultData[i] = scalarStructure.multiply(scalar, vector.get(i));
        }

        return new Vector<>(this, scalarStructure, resultData);
    }

    // --- Utility e Validazione ---

    protected void checkDimensions(Vector<K> v) {
        if (v.size() != dimension) {
            throw new IllegalArgumentException("Vector dimension mismatch. Expected " + dimension + ", got " + v.size());
        }
    }

    @Override
    public boolean contains(Vector<K> element) {
        return element != null && element.size() == dimension && 
               element.getScalarStructure().equals(scalarStructure);
    }

    @Override
    public boolean areEqual(Vector<K> a, Vector<K> b) {
        return a.equals(b); 
    }

	@Override
	public Vector<K> of(K[] data) {
		return new Vector<>(this, scalarStructure, data);
	}
}
