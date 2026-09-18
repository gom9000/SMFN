package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.core.LinearOperator;
import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Conjugable;
import net.gommagomma.smfn.math.algebra.core.elements.tensors.TensorElement;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * Una matrice quadrata n x n e', per costruzione, un operatore lineare su
 * Vector<K>: applicarla a un vettore e' il prodotto matrice-vettore usuale,
 * che rispetta additivita' e omogeneita' per definizione della formula
 * (nessuna verifica a parte serve: la linearita' discende direttamente dalla
 * distributivita' dello scalare sottostante). Implementando LinearOperator
 * (che estende Operator, che estende Mapping) SquareMatrix ottiene gratis
 * compose() e power(n) -- la composizione di operatori coincide esattamente
 * con il prodotto tra matrici gia' definito in SquareMatrixSemiring.
 */
public final class SquareMatrix<K extends ScalarElement<K>> 
implements LinearElement<SquareMatrix<K>, K>, ScalarElement<SquareMatrix<K>>, TensorElement<SquareMatrix<K>, K>, LinearOperator<Vector<K>>
{
	private final Matrix<K> internalMatrix;
    private final ScalarStructure<SquareMatrix<K>> structure;

    protected SquareMatrix(ScalarStructure<SquareMatrix<K>> structure, Matrix<K> matrix) {
        if (matrix.getRows() != matrix.getCols()) {
            throw new IllegalArgumentException("La matrice interna deve essere quadrata.");
        }
        this.internalMatrix = matrix;
        this.structure = structure;
    }


    @Override
    public ScalarStructure<SquareMatrix<K>> getStructure() {
        return structure;
    }

    public K get(int r, int c) { return internalMatrix.get(r, c); }
    public int getRows() { return internalMatrix.getRows(); }
    public int getCols() { return internalMatrix.getCols(); }
    public int getN() { return internalMatrix.getRows(); }
    public K[] getData() { return internalMatrix.getData(); }

    @Override
    public SquareMatrix<K> copy() {
        // Essendo immutabile, possiamo restituire this o una nuova istanza con la stessa internalMatrix
        return new SquareMatrix<>(structure, internalMatrix);
    }

    public Matrix<K> asMatrix() {
        return internalMatrix;
    }

    /**
     * Applica la matrice a un vettore: y = A*x. E' questo metodo che rende
     * SquareMatrix un LinearOperator<Vector<K>> -- non un'interfaccia a parte,
     * solo la formula del prodotto matrice-vettore gia' nota in algebra lineare.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Vector<K> apply(Vector<K> v) {
        int n = getN();
        if (v.size() != n) {
            throw new IllegalArgumentException("Dimensione del vettore incompatibile: attesa " + n + ", ricevuta " + v.size());
        }

        ScalarStructure<K> scalarStructure = getScalarStructure();
        K[] resultData = (K[]) new ScalarElement[n];
        for (int i = 0; i < n; i++) {
            K sum = scalarStructure.zero();
            for (int j = 0; j < n; j++) {
                sum = scalarStructure.add(sum, scalarStructure.multiply(get(i, j), v.get(j)));
            }
            resultData[i] = sum;
        }

        VectorSemimodule<K, ScalarStructure<K>> vectorSpace = new VectorSemimodule<>(scalarStructure, n);
        return vectorSpace.of(resultData);
    }


    @Override
    public int rank() { return 2; }

    @Override
    public int[] getShape() { return new int[]{getN(), getN()}; }

    @Override
    public long size() { return (long) getN() * getN(); }

    @Override
    public K get(int... indices) {
    	if (indices.length != 2) {
            throw new IllegalArgumentException("SquareMatrix richiede esattamente 2 indici.");
        }
        return get(indices[0], indices[1]);
    }

    @Override
    public String toString() {
        // Sostituiamo il prefisso "Matrix" con "SquareMatrix" per chiarezza
        return internalMatrix.toString().replaceFirst("Matrix", "SquareMatrix");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SquareMatrix)) return false;
        SquareMatrix<?> that = (SquareMatrix<?>) o;
        return internalMatrix.equals(that.internalMatrix);
    }

    @Override
    public int hashCode() {
        return internalMatrix.hashCode();
    }

    /**
     * Metodo di convenienza per la traccia, tipico delle matrici quadrate.
     */
    public K trace() {
        K total = internalMatrix.getScalarStructure().zero();
        for (int i = 0; i < getN(); i++) {
            total = internalMatrix.getScalarStructure().add(total, get(i, i));
        }
        return total;
    }

    /**
     * Trasposta coniugata (aggiunta hermitiana): M^dagger[i][j] = conj(M[j][i]).
     * Su scalari senza coniugazione (Real, Rational) coincide con la trasposta.
     */
    @SuppressWarnings("unchecked")
    public SquareMatrix<K> conjugateTranspose() {
        int n = getN();
        K[] data = (K[]) new ScalarElement[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                data[i * n + j] = conjugateIfPossible(get(j, i));
            }
        }
        return SquareMatrixElementFactory.of(getScalarStructure(), Arrays.asList(data));
    }

    @SuppressWarnings("unchecked")
    private K conjugateIfPossible(K value) {
        if (value instanceof Conjugable) {
            return (K) ((Conjugable<K>) value).conjugate();
        }
        return value;
    }

    /**
     * Una matrice e' hermitiana se coincide con la propria trasposta coniugata:
     * M = M^dagger. E' la proprieta' che rende un operatore fisicamente un
     * "Observable" (autovalori reali) -- non serve una gerarchia di tipo a
     * parte, e' un fatto verificabile su una SquareMatrix qualunque.
     */
    public boolean isHermitian() {
        return getStructure().areEqual(this, conjugateTranspose());
    }


    public Matrix<K> getInternalMatrix() {
    	return internalMatrix;
    }


	@Override
	public ScalarStructure<K> getScalarStructure() {
		return internalMatrix.getScalarStructure();
	}
}
