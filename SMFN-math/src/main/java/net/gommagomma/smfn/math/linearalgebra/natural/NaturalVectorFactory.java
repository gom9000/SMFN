package net.gommagomma.smfn.math.linearalgebra.natural;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;


public final class NaturalVectorFactory
implements SemimoduleVectorFactory<Natural, NaturalVector>
{
    private static final NaturalVectorFactory INSTANCE = new NaturalVectorFactory();
    private final NaturalSemiring scalarFactory = NaturalSemiring.getInstance();


    private NaturalVectorFactory() {}
    public static NaturalVectorFactory getInstance() { return INSTANCE; }


    @Override // SemimoduleVectorFactory impls
    public Class<Natural> getScalarClass() { return Natural.class; }

    @Override // SemimoduleVectorFactory impls
    public NumericFactory<Natural> getScalarFactory() { return this.scalarFactory; }

    @Override // SemimoduleVectorFactory impls
    public NaturalVector createZeroVector(int dimension) { return new NaturalVector(dimension); }

    @Override // SemimoduleVectorFactory impls
    public NaturalVector createVector(Natural[] data) { return new NaturalVector(data); }

    @Override // SemimoduleVectorFactory impls
	public NaturalVector createVector(double[] data) {
        Natural[] components = new Natural[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
	}

    @Override // SemimoduleVectorFactory impls
    public NaturalVector createVector(long[] data) {
        Natural[] components = new Natural[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
    }

    @Override // SemimoduleVectorFactory impls
    public NaturalVector createVector(int[] data) {
        Natural[] components = new Natural[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
    }
}
