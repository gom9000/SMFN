package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.CompositeElementFactory;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public final class Polynomials
{
    private Polynomials() {}

    public static <K extends ScalarElement<K>> Polynomial<K> of(ScalarStructure<K> scalarStructure, List<K> coefficients) {
        ScalarStructure<Polynomial<K>> polynomialStructure = getStructureFor(scalarStructure);
        @SuppressWarnings("unchecked")
		CompositeElementFactory<Polynomial<K>, List<K>> factory = (CompositeElementFactory<Polynomial<K>, List<K>>) polynomialStructure;
        return factory.of(coefficients);
    }

    @SafeVarargs
    public static <K extends ScalarElement<K>> Polynomial<K> of(ScalarStructure<K> scalarStructure, K... values) {
        return of(scalarStructure, List.of(values));
    }

    public static <K extends ScalarElement<K>> Polynomial<K> of(ScalarStructure<K> scalarStructure, double... values) {
    	@SuppressWarnings("unchecked")
		NumericFactory<K> factory = (NumericFactory<K>) scalarStructure;
        List<K> coefficients = new ArrayList<>(values.length);
        for (double v : values) {
        	coefficients.add(factory.of(v));
        }

        return of(scalarStructure, coefficients);
    }
    
 
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
}
