package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.HilbertSpace;


public final class RealVectorSpace
implements HilbertSpace<Real, RealVector>
{
    private static final RealVectorSpace INSTANCE = new RealVectorSpace();


    private RealVectorSpace() {}
    public static RealVectorSpace getInstance() { return INSTANCE; }


    @Override // AlgebraicStructure impls
    public String getName() {
        return "Real Vector Space (R^n)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(RealVector v)
    {
        return true;
    }


    @Override // VectorSpace impls
    public Field<Real> getScalarStructure()
    {
        return RealField.getInstance();
    }


    @Override // InnerProductSpace impls
    public Real distance(RealVector v1, RealVector v2)
    {
        RealVector difference = v1.subtract(v2);

        return difference.norm(); 
    }


    @Override // VectorElementFactory impls
    public RealVector createZeroVector(int dimension) { return new RealVector(dimension); }

    @Override // VectorElementFactory impls
    public RealVector createVector(Real[] data) { return new RealVector(data); }

    @Override // VectorElementFactory impls
	public RealVector createVector(double[] data) {
    	Real[] components = new Real[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
	}

    @Override // VectorElementFactory impls
    public RealVector createVector(long[] data) {
    	Real[] components = new Real[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
    }

    @Override // VectorElementFactory impls
    public RealVector createVector(int[] data) {
    	Real[] components = new Real[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
    }
}