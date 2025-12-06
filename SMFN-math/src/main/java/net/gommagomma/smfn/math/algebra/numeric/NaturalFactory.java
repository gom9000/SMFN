package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.utils.MathConstants;


public final class NaturalFactory
implements NumericFactory<Natural>
{
    private static final NaturalFactory INSTANCE = new NaturalFactory();
    public static final Natural ZERO = new Natural(0);
    public static final Natural ONE = new Natural(1);

    private NaturalFactory() {}
    public static NaturalFactory getInstance() {
        return INSTANCE;
    }


    @Override
    public Natural zero() {
        return ZERO;
    }

    @Override
    public Natural one() {
        return ONE;
    }

    @Override
    public Natural fromDouble(double value) {
        // Logica di controllo per i Numeri Naturali (solo interi >= 0)
        if (value < 0.0) {
            throw new IllegalArgumentException("Cannot create Natural from negative value: " + value);
        }
        
        long roundedValue = Math.round(value);
        if (Math.abs(value - roundedValue) > MathConstants.EPSILON) {
            throw new IllegalArgumentException("Cannot create Natural from non-integer value: " + value);
        }
        
        return new Natural(roundedValue);
    }

    @Override
    public Natural fromInt(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot create Natural from negative integer: " + value);
        }
        return new Natural(value);
    }
}
