package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;

public class ComplexFactory
implements NumericFactory<Complex>
{
    private static final ComplexFactory INSTANCE = new ComplexFactory();
    private static final Complex ZERO = new Complex(0.0, 0.0);
    private static final Complex ONE = new Complex(1.0, 0.0);


    private ComplexFactory() {}
    public static ComplexFactory getInstance() { return INSTANCE; }


    @Override // NumericFactory impls
    public Complex zero() { return ZERO; }

    @Override // NumericFactory impls
    public Complex one() { return ONE; }

    @Override // NumericFactory impls
    public Complex fromDouble(double value) {
        return new Complex(value);
    }

    @Override // NumericFactory impls
    public Complex fromLong(long value) {
        return new Complex(value);
    }

    @Override // NumericFactory impls
    public Complex fromInt(int value) {
        return new Complex(value);
    }
}