package net.gommagomma.smfn.math.algebra.polynomials;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

import java.util.ArrayList;
import java.util.List;

public final class Polynomials
{
    private Polynomials() {}

    /**
     * Crea un polinomio partendo da una struttura e coefficienti generici.
     * È il punto di ingresso universale.
     */
    @SafeVarargs
    public static <K extends ScalarElement<K>> Polynomial<K> of(Semiring<K> structure, K... coeffs) {
        return new Polynomial<>(structure, List.of(coeffs));
    }

    /**
     * Crea un polinomio partendo da valori double (molto comodo per RealField).
     */
    @SuppressWarnings("unchecked")
	public static <K extends ScalarElement<K>> Polynomial<K> of(ScalarStructure<K> structure, double... values) {
        List<K> coeffs = new ArrayList<>(values.length);
        for (double v : values) {
            coeffs.add(structure.of(v));
        }

        return new Polynomial<>((Semiring<K>) structure, coeffs);
    }

    /**
     * Esegue la somma rilevando automaticamente la struttura corretta.
     */
    public static <K extends ScalarElement<K>> Polynomial<K> add(Polynomial<K> a, Polynomial<K> b) {
    	PolynomialSemiring<K> polyStructure = new PolynomialSemiring<>(getSemiring(a));
        return polyStructure.add(a, b);
    }

    /**
     * Esegue la moltiplicazione.
     */
    public static <K extends ScalarElement<K>> Polynomial<K> multiply(Polynomial<K> a, Polynomial<K> b) {
    	PolynomialSemiring<K> polyStructure = new PolynomialSemiring<>(getSemiring(a));
        return polyStructure.multiply(a, b);
    }


    @SuppressWarnings("unchecked")
    public static <K extends ScalarElement<K>> PolynomialDivisionResult<K> divide(Polynomial<K> a, Polynomial<K> b) {
        ScalarStructure<K> s = a.getScalarStructure();
        if (s instanceof Field) {
            return new EuclideanPolynomialRing<>((Field<K>) s).divide(a, b);
        }
        throw new UnsupportedOperationException("Polynomial division requires Field coefficients.");
    }


    public static <K extends ScalarElement<K>> Polynomial<K> derivative(Polynomial<K> p) {
        Semiring<K> s = getSemiring(p);
        if (p.degree() <= 0) {
            return new PolynomialSemiring<>(s).zero();
        }

        List<K> derivCoeffs = new ArrayList<>();
        
        for (int i = 1; i <= p.degree(); i++) {
            K nAsScalar = ((ScalarStructure<K>) s).of(i);
            derivCoeffs.add(s.multiply(nAsScalar, p.getCoefficient(i)));
        }

        return new Polynomial<>(s, derivCoeffs);
    }


    public static <K extends ScalarElement<K>> Polynomial<K> integrate(Polynomial<K> p, K constant) {
        // Recuperiamo la struttura scalare dal polinomio stesso
        ScalarStructure<K> s = p.getScalarStructure();
        
        if (!(s instanceof Field)) {
            throw new UnsupportedOperationException("Formal integration requires a Field of coefficients.");
        }

        Field<K> field = (Field<K>) s;
        int oldDegree = p.degree();
        List<K> newCoeffs = new ArrayList<>(oldDegree + 2);
        
        // 1. Il nuovo termine noto è la costante C
        newCoeffs.add(constant);
        
        // 2. Integrazione dei termini esistenti
        for (int i = 0; i <= oldDegree; i++) {
            K ai = p.getCoefficient(i);
            K divisor = s.of((double) i + 1); 
            
            // Calcolo: a_i / (i + 1)
            newCoeffs.add(field.divide(ai, divisor));
        }
        
        return new Polynomial<>(field, newCoeffs);
    }

    // --- Helper Interni ---

    @SuppressWarnings("unchecked")
    private static <K extends ScalarElement<K>> Semiring<K> getSemiring(Polynomial<K> p) {
        return (Semiring<K>) p.getScalarStructure();
    }
}
