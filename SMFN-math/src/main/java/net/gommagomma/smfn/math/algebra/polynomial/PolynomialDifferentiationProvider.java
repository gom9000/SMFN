package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.SymbolicDifferentiationProvider;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Provider per il calcolo della derivata simbolica di polinomi definiti su un anello.
 * Implementa l'interfaccia SymbolicDifferentiationProvider applicando la regola di derivazione per i polinomi.
 * 
 * @param <K> il tipo degli elementi scalari (coefficienti)
 * @param <S> il tipo della struttura algebrica che funge da anello, struttura scalare e fabbrica numerica per i coefficienti
 */
public class PolynomialDifferentiationProvider<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K> & NumericFactory<K>>
implements SymbolicDifferentiationProvider<K, Polynomial<K>>
{
    private final S scalarStructure;
    private final PolynomialRing<K, S> polynomialRing;

    /**
     * Costruisce un provider di derivazione per i polinomi basato sulla struttura scalare specificata.
     * 
     * @param scalarStructure la struttura algebrica dei coefficienti scalari
     */
    public PolynomialDifferentiationProvider(S scalarStructure) {
        this.scalarStructure = scalarStructure;
        this.polynomialRing = new PolynomialRing<>(scalarStructure);
    }

    @Override
    public Polynomial<K> derivative(Polynomial<K> p) {
        if (p.degree() <= 0) {
            return polynomialRing.zero();
        }

        List<K> derivCoeffs = new ArrayList<>(p.degree());
        for (int i = 1; i <= p.degree(); i++) {
            K nAsScalar = scalarStructure.of(i);
            derivCoeffs.add(scalarStructure.multiply(nAsScalar, p.getCoefficient(i)));
        }

        return polynomialRing.of(derivCoeffs);
    }
}