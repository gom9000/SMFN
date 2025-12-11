package net.gommagomma.smfn.math.linearalgebra.core.operators;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.MathFunction;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;


/**
 * Rappresenta una Trasformazione Lineare astratta T: V -> W.
 * K: Tipo di scalare (FieldElement o RingElement)
 * V: Tipo del vettore del dominio (AbstractVectorElement)
 * W: Tipo del vettore del codominio (AbstractVectorElement)
 * M: Tipo della Matrice di trasformazione (AbstractMatrixElement)
 */
public abstract class AbstractLinearTransformation<
    K extends AlgebraicElement<K>, 
    V extends VectorElement<K, V>, 
    W extends VectorElement<K, W>,
    M extends MatrixElement<K, V, M>
> implements MathFunction<V, W> 
{
    protected final M matrix;

    // Dimensione del dominio (dim(V) = numero di colonne della matrice)
    protected final int domainDimension;
    
    // Dimensione del codominio (dim(W) = numero di righe della matrice)
    protected final int codomainDimension;

    public AbstractLinearTransformation(M matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Transformation matrix cannot be null.");
        }
        this.matrix = matrix;
        this.codomainDimension = matrix.getRows();
        this.domainDimension = matrix.getColumns();
    }

    /**
     * Applica la trasformazione al vettore dato, T(v) = Mv.
     * @param input Il vettore del dominio V.
     * @return Il vettore del codominio W.
     * * Nota: Questo richiede l'implementazione del metodo matrixVectorMultiply(V)
     * nelle classi concrete di Matrice (M).
     */
    @Override
    public W evaluate(V input) {
        if (input.dimension() != domainDimension) {
            throw new IllegalArgumentException(
                "Input vector dimension mismatch. Expected: " + domainDimension + 
                ", Actual: " + input.dimension()
            );
        }
        
        // Esegue il prodotto Matrice * Vettore
        return matrixVectorMultiply(input);
    }

    /**
     * Metodo astratto per eseguire il prodotto M * v, delegato alla matrice concreta.
     * Questo è necessario perché M è un tipo astratto, ma il risultato W
     * deve essere creato correttamente (es. RealMatrix * RealVector = RealVector).
     */
    protected abstract W matrixVectorMultiply(V input);


    // --- Operazioni Specializzate (che possono essere definite solo sui Campi) ---

    /**
     * Restituisce la matrice inversa della trasformazione (T^-1), se possibile.
     * Questa operazione è definita solo per trasformazioni sui Campi (Field) e se la matrice è quadrata e invertibile.
     */
    public abstract AbstractLinearTransformation<K, W, V, M> inverse();


    // --- Getters ---

    public M getMatrix() {
        return matrix;
    }

    public int getDomainDimension() {
        return domainDimension;
    }

    public int getCodomainDimension() {
        return codomainDimension;
    }
}