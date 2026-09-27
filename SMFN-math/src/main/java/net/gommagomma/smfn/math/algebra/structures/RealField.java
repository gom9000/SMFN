package net.gommagomma.smfn.math.algebra.structures;

import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.ApproximateStructure;
import net.gommagomma.smfn.math.algebra.core.structures.metric.NormedSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Rappresenta il campo algebrico dei numeri reali (R) in aritmetica a virgola mobile approssimata.
 * Implementa le operazioni di campo (somma, prodotto, inversione), la gestione della tolleranza numerica
 * basata su epsilon per il confronto tra valori reali e le funzioni di fabbrica per gli elementi reali.
 * Implementa anche NormedSpace: R e' uno spazio vettoriale di dimensione 1 su se stesso
 */
public final class RealField
implements Field<Real>, ApproximateStructure<Real>, NumericFactory<Real>, NormedSpace<Real, Real, RealField>
{
    private static final Real ZERO = new Real(0.0);
    private static final Real ONE = new Real(1.0);

    /** Istanza singleton predefinita del campo reale basata sulla tolleranza standard di MathConstants.EPSILON */
    public static final RealField INSTANCE = new RealField(MathConstants.EPSILON);
    
    private final double epsilon;

    /**
     * Costruisce un campo reale con una specifica tolleranza epsilon per i confronti approssimati.
     * 
     * @param epsilon la tolleranza numerica per l'uguaglianza e la precisione
     */
    private RealField(double epsilon) { this.epsilon = epsilon; }


    @Override // ApproximateStructure impls
    public double epsilon() { return epsilon; }


    // NumericFactory  impls
    @Override public Real zero() { return ZERO; }
    @Override public Real one() { return ONE; }
    @Override public Real of(double v) { return new Real(v); }
    @Override public Real of(long v) { return new Real(v); }
    @Override public Real of(int v) { return new Real(v); }


    // ScalarStructure impls
    @Override
    public Real magnitude(Real a) {
        return a.abs();
    }


    // LinearStructure/CompositeStructure impls (R come spazio di dimensione 1 su se stesso)
    @Override
    public Real scale(Real scalar, Real vector) {
        return multiply(scalar, vector);
    }

    @Override
    public RealField getScalarStructure() {
        return this;
    }


    // NormedSpace impls
    @Override
    public Real norm(Real v) {
        return v.norm();
    }


    // AdditiveMonoid impls
    @Override
    public Real add(Real a, Real b) { 
        return new Real(a.getValue() + b.getValue()); 
    }


    // MultiplicativeMonoid impls
    @Override
    public Real multiply(Real a, Real b) { 
        return new Real(a.getValue() * b.getValue()); 
    }


     // AdditiveGroup impls
    @Override
    public Real negate(Real e) {
        return new Real(-e.getValue());
    }


    // MultiplicativeGroup impls
    @Override
    public Real inverse(Real e) {
        if (isZero(e)) throw new ArithmeticException("Division by zero");

        return new Real(1.0 / e.getValue());
    }


    // AlgebraicStructure impls
    @Override
    public String getName()    {
        return "Real Field (R)";
    }

    @Override
    public boolean contains(Real e)    {
        return e != null && !Double.isNaN(e.getValue()) && !Double.isInfinite(e.getValue());
    }

    @Override
    public boolean areEqual(Real a, Real b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        double av = a.getValue();
        double bv = b.getValue();

        if (av == bv) return true;

        return Math.abs(av - bv) < epsilon();
    }
}