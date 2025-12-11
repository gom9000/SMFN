package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;

public final class RealVectorFactory
implements SemimoduleVectorFactory<Real, RealVector>
{
    private static final RealVectorFactory INSTANCE = new RealVectorFactory();
    private final RealField scalarFactory = RealField.getInstance();


    private RealVectorFactory() {}
    public static RealVectorFactory getInstance() { return INSTANCE; }


    @Override // SemimoduleVectorFactory impls
    public Class<Real> getScalarClass() { return Real.class; }

    @Override // SemimoduleVectorFactory impls
    public NumericFactory<Real> getScalarFactory() { return this.scalarFactory; }

    @Override // SemimoduleVectorFactory impls
    public RealVector createZeroVector(int dimension) { return new RealVector(dimension); }

    @Override // SemimoduleVectorFactory impls
    public RealVector createVector(Real[] data) { return new RealVector(data); }

    @Override // SemimoduleVectorFactory impls
	public RealVector createVector(double[] data) {
    	Real[] components = new Real[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
	}

    @Override // SemimoduleVectorFactory impls
    public RealVector createVector(long[] data) {
    	Real[] components = new Real[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
    }

    @Override // SemimoduleVectorFactory impls
    public RealVector createVector(int[] data) {
    	Real[] components = new Real[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
    }
}
