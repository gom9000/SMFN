package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.InnerProductSpace;

public final class RationalVectorSpace
implements InnerProductSpace<Rational, RationalVector>
{    
    private static final RationalVectorSpace INSTANCE = new RationalVectorSpace();


    private RationalVectorSpace() {}
    public static RationalVectorSpace getInstance() { return INSTANCE; }


    @Override // AlgebraicStructure impls
    public String getName() {
        return "Rational Vector Space (Q^n)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(RationalVector v) {
        return (v != null);
    }


    @Override // VectorSpace impls
    public Field<Rational> getScalarStructure() {
        return RationalField.getInstance();
    }


    @Override // VectorElementFactory impls
    public RationalVector createZeroVector(int dimension) { return new RationalVector(dimension); }

    @Override // VectorElementFactory impls
    public RationalVector createVector(Rational[] data) { return new RationalVector(data); }

    @Override // VectorElementFactory impls
	public RationalVector createVector(double[] data) {
    	Rational[] components = new Rational[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
	}

    @Override // VectorElementFactory impls
    public RationalVector createVector(long[] data) {
    	Rational[] components = new Rational[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
    }

    @Override // VectorElementFactory impls
    public RationalVector createVector(int[] data) {
    	Rational[] components = new Rational[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
    }
}
