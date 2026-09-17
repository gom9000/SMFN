package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.CompositeElementFactory;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Fabbrica pura di Polynomial<K>: dati dei coefficienti, restituisce l'istanza.
 * Nessuna decisione algebrica qui dentro -- quella la fa PolynomialStructureFactory,
 * a cui questa classe si appoggia per sapere quale struttura usare.
 */
public final class PolynomialElementFactory
{
    private PolynomialElementFactory() {}

    public static <K extends ScalarElement<K>> Polynomial<K> of(ScalarStructure<K> scalarStructure, List<K> coefficients) {
        ScalarStructure<Polynomial<K>> polynomialStructure = PolynomialStructureFactory.getStructureFor(scalarStructure);
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
}
