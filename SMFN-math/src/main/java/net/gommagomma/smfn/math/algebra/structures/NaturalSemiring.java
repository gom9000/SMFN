package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.ExactStructure;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.utils.MathConstants;


/**
 * Rappresenta il semianello algebrico dei numeri naturali (N) con aritmetica esatta a 64 bit.
 * Modella la struttura algebrica priva di inverso additivo per i numeri non negativi, gestendo 
 * il controllo degli overflow tramite {@link Math} e le funzioni di fabbrica per gli elementi naturali.
 */
public final class NaturalSemiring
implements Semiring<Natural>, ExactStructure<Natural>, NumericFactory<Natural>
{
    /** Elemento neutro additivo e moltiplicativo zero ($\mathbb{N}$). */
    public static final Natural ZERO = new Natural(0);
    
    /** Elemento neutro moltiplicativo uno ($\mathbb{N}$). */
    public static final Natural ONE = new Natural(1);

    /** Istanza singleton predefinita del semianello dei naturali. */
    public static final NaturalSemiring INSTANCE = new NaturalSemiring();
    
    private NaturalSemiring() {}


    // NumericFactory impls
    @Override public Natural zero() { return ZERO; }
    @Override public Natural one() { return ONE; }
    
    @Override
    public Natural of(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Cannot create a Natural number from a non-finite value: " + value);
        }
        if (value < 0.0) {
            throw new IllegalArgumentException("Cannot create Natural from negative value: " + value);
        }
        if (value > Long.MAX_VALUE) {
            throw new ArithmeticException("Value " + value + " is outside the range of Natural (long).");
        }

        long roundedValue = Math.round(value);
        if (Math.abs(value - roundedValue) > MathConstants.EPSILON) {
            throw new IllegalArgumentException("Cannot convert non-integer value " + value + " to integer type.");
        }
        
        return new Natural(roundedValue);
    }

    @Override
    public Natural of(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot create Natural from negative integer: " + value);
        }
        return new Natural(value);
    }

    @Override
    public Natural of(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot create Natural from negative integer: " + value);
        }
        return new Natural(value);
    }


    // ScalarStructure impls
    @Override
    public Real magnitude(Natural n) {
        return RealField.INSTANCE.of(n.getValue()); 
    }

    
     // AdditiveMonoid impls
    @Override
    public Natural add(Natural a, Natural b) {
        return new Natural(Math.addExact(a.getValue(), b.getValue()));
    }


    // MultiplicativeMonoid impls
    @Override
    public Natural multiply(Natural a, Natural b) {
        return new Natural(Math.multiplyExact(a.getValue(), b.getValue()));
    }


    @Override // AlgebraicStructure impls
    public String getName()
    {
        return "Natural Semiring (N)";
    }

    @Override
    public boolean contains(Natural e)
    {
        return (e != null);
    }
}