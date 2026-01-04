package net.gommagomma.smfn.math.algebra.structures;

import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.ApproximateStructure;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.utils.MathConstants;

public final class ComplexField
implements Field<Complex>, ApproximateStructure<Complex>, NumericFactory<Complex>
{
	private static final Complex ZERO = new Complex(0.0, 0.0);
	private static final Complex ONE = new Complex(1.0, 0.0);

    public static final ComplexField INSTANCE = new ComplexField(MathConstants.EPSILON);
    private final double epsilon;
    private ComplexField(double epsilon) { this.epsilon = epsilon; }


    // ApproximateStructure impls
    @Override
    public double epsilon() { return epsilon; }


    // NumericFactory impls
    @Override
    public Complex of(double value) { return new Complex(value, 0); }

    @Override
    public Complex of(long value) { return new Complex(value, 0); }

    @Override
    public Complex of(int value) { return new Complex(value, 0); }

    @Override
    public Complex zero() { return ZERO; }

    @Override
    public Complex one() { return ONE; }


    // ScalarStructure impls
    @Override
    public Real magnitude(Complex z) {
    	if (z == null) {
            return RealField.INSTANCE.zero();
        }
        return RealField.INSTANCE.of(z.modulus());
    }


    // AdditiveMonoid impls
    @Override
    public Complex add(Complex a, Complex b) {
        return new Complex(a.getRe() + b.getRe(), a.getIm() + b.getIm());
    }


    // MultiplicativeMonoid impls
    @Override
    public Complex multiply(Complex a, Complex b) {
        double re = a.getRe() * b.getRe() - a.getIm() * b.getIm();
        double im = a.getRe() * b.getIm() + a.getIm() * b.getRe();

        return new Complex(re, im);
    }


    // AdditiveGroup impls
    @Override
    public Complex negate(Complex e) {
        return new Complex(-e.getRe(), -e.getIm());
    }


    // MultiplicativeGroup impls
    @Override
    public Complex inverse(Complex e) {
        if (isZero(e)) throw new ArithmeticException("Division by zero");
        double den = e.getRe() * e.getRe() + e.getIm() * e.getIm();

        return new Complex(e.getRe() / den, -e.getIm() / den);
    }


    // AlgebraicStructure impls
    @Override
    public String getName() { return "Complex Field (C)"; }
    
    @Override
    public boolean contains(Complex e) {
        return e != null && !Double.isNaN(e.getRe()) && !Double.isInfinite(e.getRe());
    }

   @Override
    public boolean areEqual(Complex a, Complex b) {
        if (a == b) return true;

        double diffRe = a.getRe() - b.getRe();
        double diffIm = a.getIm() - b.getIm();

        return (diffRe * diffRe + diffIm * diffIm) < (epsilon * epsilon);
    }
}