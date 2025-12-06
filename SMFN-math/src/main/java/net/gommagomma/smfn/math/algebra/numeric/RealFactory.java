package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;

public class RealFactory
implements NumericFactory<Real>
{
    private static final RealFactory INSTANCE = new RealFactory();
    private static final Real ZERO = new Real(0.0);
    private static final Real ONE = new Real(1.0);


    private RealFactory() {}
    public static RealFactory getInstance() { return INSTANCE; }


    @Override // NumericFactory impls
    public Real zero() { return ZERO; }

    @Override // NumericFactory impls
    public Real one() { return ONE; }

    @Override // NumericFactory impls
    public Real fromDouble(double value) {
        return new Real(value);
    }

    @Override // NumericFactory impls
    public Real fromLong(long value) {
        return new Real(value);
    }

    @Override // NumericFactory impls
    public Real fromInt(int value) {
        return new Real(value);
    }
}