package net.gommagomma.smfn.math.linearalgebra.vectors;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.tensors.TensorElement;
import net.gommagomma.smfn.math.algebra.core.structures.composite.LinearStructure;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

import java.util.Arrays;
import java.util.Objects;

/**
 * Rappresenta un vettore denso di elementi scalari K.
 * È un elemento di rango 1 nella gerarchia dei tensori.
 */
public final class Vector<K extends ScalarElement<K>> 
implements LinearElement<Vector<K>, K>, TensorElement<Vector<K>, K>
{
    private final ScalarElement<?>[] data;
    private final LinearStructure<Vector<K>, K, ?> vectorStructure;
    private final ScalarStructure<K> scalarStructure;
    private final int size;

    /**
     * Costruttore protetto: la creazione dovrebbe passare attraverso le Factory 
     * o le Strutture Ambiente (VectorSpace, etc.).
     */
    protected Vector(LinearStructure<Vector<K>, K, ?> vectorStructure, ScalarStructure<K> scalarStructure, ScalarElement<?>[] data) {
    	this.vectorStructure = vectorStructure;
        this.scalarStructure = Objects.requireNonNull(scalarStructure);
        this.data = Objects.requireNonNull(data);
        this.size = data.length;
    }

    // --- Implementazione TensorElement ---

    @Override
    public int rank() {
        return 1;
    }

    @Override
    public int[] getShape() {
        return new int[]{size};
    }

    // --- Accesso ai Dati ---

    @SuppressWarnings("unchecked")
    public K get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        return (K) data[index];
    }

    @Override
    public long size() {
        return (long) this.size; 
    }

    @Override
    public ScalarStructure<K> getScalarStructure() {
        return scalarStructure;
    }

    // --- Metodi di utilità ---

    /**
     * Restituisce una copia difensiva dell'array interno.
     */
    public ScalarElement<?>[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;
        Vector<?> vector = (Vector<?>) o;
        if (size != vector.size) return false;
        
        // Deleghiamo il confronto dell'uguaglianza alla struttura scalare
        for (int i = 0; i < size; i++) {
            if (!scalarStructure.areEqual(this.get(i), (K) vector.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(scalarStructure, size);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(get(i));
            if (i < size - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }

	@Override
	public Vector<K> copy() {
		return new Vector<>(this.vectorStructure, this.scalarStructure, this.toArray());
	}

	@Override
	public K get(int... indices) {
		if (indices == null || indices.length != 1) {
            throw new IllegalArgumentException("Vector requires exactly one index, got: " + (indices == null ? "null" : indices.length));
        }
        return get(indices[0]);
	}

	public LinearStructure<Vector<K>, K, ?> getStructure() { return vectorStructure; }
}