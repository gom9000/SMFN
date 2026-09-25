package net.gommagomma.smfn.math.algebra.structures;

import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.ApproximateStructure;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Rappresenta il campo algebrico dei numeri complessi (C) in aritmetica a virgola mobile approssimata.
 * Implementa le operazioni di campo (somma, prodotto, inversione), la gestione della tolleranza numerica 
 * tramite epsilon e le funzioni di fabbrica per la creazione di elementi complessi.
 */
public final class ComplexField
implements Field<Complex>, ApproximateStructure<Complex>, NumericFactory<Complex>
{
    private static final Complex ZERO = new Complex(0.0, 0.0);
    private static final Complex ONE = new Complex(1.0, 0.0);

    /** Istanza singleton predefinita del campo complesso basata sulla tolleranza standard di MathConstants.EPSILON */
    public static final ComplexField INSTANCE = new ComplexField(MathConstants.EPSILON);
    
    private final double epsilon;

    /**
     * Costruisce un campo complesso con una specifica tolleranza epsilon per i confronti approssimati.
     * 
     * @param epsilon la tolleranza numerica per l'uguaglianza e la precisione
     */
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

    /**
     * Crea un numero complesso a partire da una parte reale e una parte immaginaria specificate.
     * 
     * @param re la parte reale
     * @param im la parte immaginaria
     * @return il numero complesso corrispondente
     */
    public Complex of(double re, double im) { return new Complex(re, im); }

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
        return e != null
                && !Double.isNaN(e.getRe()) && !Double.isInfinite(e.getRe())
                && !Double.isNaN(e.getIm()) && !Double.isInfinite(e.getIm());
    }

   @Override
    public boolean areEqual(Complex a, Complex b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        double diffRe = a.getRe() - b.getRe();
        double diffIm = a.getIm() - b.getIm();

        return (diffRe * diffRe + diffIm * diffIm) < (epsilon() * epsilon());
    }
}