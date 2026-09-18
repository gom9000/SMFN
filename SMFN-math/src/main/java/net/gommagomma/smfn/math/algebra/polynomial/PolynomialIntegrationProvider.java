package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.SymbolicIntegrationProvider;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Provider per il calcolo dell'integrale simbolico indefinito di polinomi definiti su un campo.
 * Implementa l'interfaccia SymbolicIntegrationProvider applicando l'integrazione termine a termine 
 * e gestendo la costante di integrazione arbitraria come termine noto.
 * 
 * @param <K> il tipo degli elementi scalari (coefficienti) definiti su un campo
 * @param <S> il tipo della struttura algebrica che funge da campo, struttura scalare e fabbrica numerica per i coefficienti
 */
public class PolynomialIntegrationProvider<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K> & NumericFactory<K>>
implements SymbolicIntegrationProvider<K, Polynomial<K>>
{
    private final S scalarStructure;
    private final PolynomialRing<K, S> polynomialRing;

    /**
     * Costruisce un provider di integrazione per i polinomi basato sul campo scalare specificato.
     * 
     * @param scalarStructure la struttura di campo dei coefficienti scalari
     */
    public PolynomialIntegrationProvider(S scalarStructure) {
        this.scalarStructure = scalarStructure;
        this.polynomialRing = new PolynomialRing<>(scalarStructure);
    }

    /**
     * Calcola l'integrale indefinito del polinomio specificato, aggiungendo la costante di integrazione 
     * fornita come termine noto ($a_0$).
     * 
     * @param p il polinomio da integrare
     * @param constant la costante di integrazione iniziale
     * @return il polinomio risultante dall'integrazione simbolica
     */
    @Override
    public Polynomial<K> integrate(Polynomial<K> p, K constant) {
        int oldDegree = p.degree();
        List<K> newCoeffs = new ArrayList<>(oldDegree + 2);
        newCoeffs.add(constant);

        for (int i = 0; i <= oldDegree; i++) {
            K ai = p.getCoefficient(i);
            K divisor = scalarStructure.of(i + 1);
            newCoeffs.add(scalarStructure.divide(ai, divisor));
        }

        return polynomialRing.of(newCoeffs);
    }
}