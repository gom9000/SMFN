package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public final class Polynomials
{
    private Polynomials() {}

    public static <K extends ScalarElement<K>> ScalarStructure<Polynomial<K>> getStructureFor(ScalarStructure<K> s) {
        if (s instanceof Field) {
            return createEuclidean(s);
        }
        if (s instanceof CommutativeRing) {
            return createCommutativeRing(s);
        }
        if (s instanceof Ring) {
            return createRing(s);
        }
        return createSemiring(s);
    }

    @SuppressWarnings("unchecked")
	private static <K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> EuclideanPolynomialRing<K, S> createEuclidean(ScalarStructure<K> s) {
        return new EuclideanPolynomialRing<>((S) s);
    }

    @SuppressWarnings("unchecked")
	private static <K extends ScalarElement<K>, S extends CommutativeRing<K> & ScalarStructure<K>> PolynomialRing<K, S> createCommutativeRing(ScalarStructure<K> s) {
        return new PolynomialRing<>((S) s);
    }

    @SuppressWarnings("unchecked")
	private static <K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> PolynomialRing<K, S> createRing(ScalarStructure<K> s) {
        return new PolynomialRing<>((S) s);
    }

    @SuppressWarnings("unchecked")
	private static <K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> PolynomialSemiring<K, S> createSemiring(ScalarStructure<K> s) {
        return new PolynomialSemiring<>((S) s);
    }

    /**
     * Crea un polinomio partendo da una struttura e coefficienti generici.
     * È il punto di ingresso universale.
     */
    @SafeVarargs
    public static <K extends ScalarElement<K>> Polynomial<K> of(ScalarStructure<K> structure, K... coeffs) {
        return new Polynomial<>(structure, List.of(coeffs));
    }


    /**
     * Crea un polinomio partendo da valori double (molto comodo per RealField).
     */
    public static <K extends ScalarElement<K>> Polynomial<K> of(ScalarStructure<K> structure, double... values) {
        if (!(structure instanceof NumericFactory)) {
            throw new UnsupportedOperationException(
                "Structure " + structure.getName() + " does not support creation from numeric values."
            );
        }
        
        @SuppressWarnings("unchecked")
		NumericFactory<K> factory = (NumericFactory<K>) structure;
        List<K> coeffs = new ArrayList<>(values.length);
        for (double v : values) {
            coeffs.add(factory.of(v));
        }

        return new Polynomial<>(structure, coeffs);
    }


    public static <K extends ScalarElement<K>> Polynomial<K> add(Polynomial<K> a, Polynomial<K> b) {
        return (new PolynomialSemiring<K, ScalarStructure<K>>(a.getScalarStructure())).add(a, b);
    }

    public static <K extends ScalarElement<K>> Polynomial<K> multiply(Polynomial<K> a, Polynomial<K> b) {
    	return (new PolynomialSemiring<K, ScalarStructure<K>>(a.getScalarStructure())).multiply(a, b);
    }


    @SuppressWarnings("unchecked")
    public static <K extends ScalarElement<K>> PolynomialDivisionResult<K> divide(Polynomial<K> a, Polynomial<K> b) {
        ScalarStructure<K> s = a.getScalarStructure();
        if (s instanceof Field) {
            return new EuclideanPolynomialRing<>((Field<K> & ScalarStructure<K>) s).divide(a, b);
        }
        throw new UnsupportedOperationException("Polynomial division requires Field coefficients.");
    }


    @SuppressWarnings("unchecked")
	public static <K extends ScalarElement<K>> Polynomial<K> derivative(Polynomial<K> p) {
    	ScalarStructure<K> s = p.getScalarStructure();
        if (p.degree() <= 0) {
            return new PolynomialSemiring<>(s).zero();
        }

        if (!(s instanceof NumericFactory)) {
            throw new UnsupportedOperationException("Cannot derive: scalar structure is not a NumericFactory");
        }
        NumericFactory<K> factory = (NumericFactory<K>) s;

        List<K> derivCoeffs = new ArrayList<>();
        for (int i = 1; i <= p.degree(); i++) {
        	K nAsScalar = factory.of(i); 
            derivCoeffs.add(s.multiply(nAsScalar, p.getCoefficient(i)));
        }

        return new Polynomial<>(s, derivCoeffs);
    }


    @SuppressWarnings("unchecked")
	public static <K extends ScalarElement<K>> Polynomial<K> integrate(Polynomial<K> p, K constant) {
        ScalarStructure<K> s = p.getScalarStructure();
        
        if (!(s instanceof Field) || !(s instanceof NumericFactory)) {
            throw new UnsupportedOperationException("Integration requires a Field that implements NumericFactory.");
        }

        Field<K> field = (Field<K>) s;
        NumericFactory<K> factory = (NumericFactory<K>) s;

        int oldDegree = p.degree();
        List<K> newCoeffs = new ArrayList<>(oldDegree + 2);
        newCoeffs.add(constant);
        
        // iIntegrazione dei termini esistenti: formula [a_i / (i+1)] * x^(i+1)
        for (int i = 0; i <= oldDegree; i++) {
            K ai = p.getCoefficient(i);
            K divisor = factory.of(i + 1);
            newCoeffs.add(field.divide(ai, divisor));
        }
        
        return new Polynomial<>(s, newCoeffs);
    }
}
