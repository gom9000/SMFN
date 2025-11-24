package net.gommagomma.smfn.math.linearalgebra.rational;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.linearalgebra.core.NormedVectorElement;
import net.gommagomma.smfn.math.linearalgebra.core.VectorSpace;

/**
 * Rappresenta un vettore immutabile di numeri razionali.
 * Forma uno Spazio Vettoriale (VectorSpace) sul Campo dei Razionali (Q).
 */
public final class RationalVector
implements NormedVectorElement<Rational, RationalVector> // I razionali sono un campo ordinato
{
    private final Rational[] data;
    private final int dimension;

    /**
     * Costruisce un RationalVector da un numero variabile di componenti Rational.
     * @param components Le componenti del vettore.
     */
    public RationalVector(Rational... components) {
        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Components cannot be null or empty.");
        }
        // Copia difensiva per garantire l'immutabilità
        this.data = Arrays.copyOf(components, components.length);
        this.dimension = components.length;
    }
    
    /**
     * Costruisce un vettore nullo della dimensione specificata.
     */
    public RationalVector(int dimension) {
        if (dimension <= 0) {
             throw new IllegalArgumentException("Dimension must be positive.");
        }
        this.dimension = dimension;
        this.data = new Rational[dimension];
        Arrays.fill(this.data, Rational.ZERO);
    }

    /**
     * Helper statico per creare vettori da array di long primitivi (visti come razionali a denominatore 1).
     */
    public static RationalVector fromLongs(long... data) {
        Rational[] rationalComponents = Arrays.stream(data)
                                          .mapToObj(Rational::new) 
                                          .toArray(Rational[]::new);
        return new RationalVector(rationalComponents);
    }
    
    // --- Implementazioni di AlgebraicElement e AdditiveMonoidElement ---

    @Override
    public boolean isEqual(RationalVector other) {
        return Arrays.equals(this.data, other.data); // Funziona perché Rational.equals è esatto
    }

    @Override
    public RationalVector copy() {
        return new RationalVector(this.data);
    }

	@Override
	public RationalVector getZero() {
		return new RationalVector(dimension);
	}

    @Override
    public RationalVector add(RationalVector other) {
        if (this.dimension != other.dimension) throw new IllegalArgumentException("Dimensions must match.");
        Rational[] resultData = new Rational[dimension];
        for (int i = 0; i < dimension; i++) {
            resultData[i] = this.data[i].add(other.data[i]);
        }
        return new RationalVector(resultData);
    }
    
    @Override
    public RationalVector negate() {
        Rational[] negatedData = new Rational[dimension];
        for (int i = 0; i < dimension; i++) {
            negatedData[i] = this.data[i].negate();
        }
        return new RationalVector(negatedData);
    }

    // --- Implementazione di VectorElement (dotProduct, multiplyByScalar) ---
    
    @Override
    public Rational dotProduct(RationalVector other) {
        if (this.dimension != other.dimension) throw new IllegalArgumentException("Dimensions must match for dot product.");
        Rational result = Rational.ZERO;
        for (int i = 0; i < dimension; i++) {
            Rational product = this.data[i].multiply(other.data[i]);
            result = result.add(product);
        }
        return result;
    }

    @Override
    public RationalVector multiplyByScalar(Rational scalar) {
        Rational[] scaledData = new Rational[dimension];
        for (int i = 0; i < dimension; i++) {
            scaledData[i] = this.data[i].multiply(scalar);
        }
        return new RationalVector(scaledData);
    }

    // --- Implementazione di NormedVectorElement (norm) ---

    @Override
    public Rational norm() {
        // La norma L2 è la radice quadrata del prodotto scalare con se stesso.
        // Poiché Rational ha sqrt() solo per quadrati perfetti, questo può lanciare un'eccezione
        // se la norma è irrazionale (es. il vettore (1/2, 1/2) ha norma sqrt(1/2)).
        // Questo è un limite della precisione esatta!
        return this.dotProduct(this).sqrt(); 
    }
    
    // --- Implementazione di SpaceElement/VectorElement (utilità) ---

    @Override public int dimension() { return this.dimension; }
    @Override public Rational get(int index) { return this.data[index]; }

    @Override
    public VectorSpace<RationalVector, Rational> getModule() {
        return RationalVectorSpace.getInstance(); 
    }
    
    // --- Java Standard impls ---

    @Override
    public String toString() {
        return "Q^" + dimension + Arrays.toString(data);
    }
    
    @Override public final boolean equals(Object other) {
        return (other instanceof RationalVector) && isEqual((RationalVector)other);
    }
    
    @Override public final int hashCode() {
        return Arrays.hashCode(data);
    }
}
