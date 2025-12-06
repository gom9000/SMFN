package net.gommagomma.smfn.math.linearalgebra.natural;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.numeric.NaturalFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;


public final class NaturalVectorFactory
implements SemimoduleVectorFactory<Natural, NaturalVector>
{
    private static final NaturalVectorFactory INSTANCE = new NaturalVectorFactory();
    private final NaturalFactory scalarFactory = NaturalFactory.getInstance();


    private NaturalVectorFactory() {}
    public static NaturalVectorFactory getInstance() { return INSTANCE; }


    @Override // SemimoduleVectorFactory impls
    public Class<Natural> getScalarClass() { return Natural.class; }

    @Override // SemimoduleVectorFactory impls
    public NumericFactory<Natural> getScalarFactory() { return this.scalarFactory; }

    @Override // SemimoduleVectorFactory impls
    public NaturalVector createVector(int dimension) { return new NaturalVector(dimension); }
    
    @Override // SemimoduleVectorFactory impls
    public NaturalVector createVector(Natural[] data) { return new NaturalVector(data); }
}
