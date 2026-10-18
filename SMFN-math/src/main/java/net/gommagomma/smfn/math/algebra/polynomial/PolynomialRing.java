package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Rappresenta un anello di polinomi (R[x]) i cui coefficienti appartengono 
 * a un anello scalare sottostante. Estende il semianello dei polinomi implementando 
 * le operazioni algebriche del gruppo additivo, inclusa la negazione dei polinomi.
 * 
 * @param <K> il tipo degli elementi scalari (coefficienti) del polinomio
 * @param <S> il tipo della struttura algebrica scalare sottostante (anello e struttura scalare)
 */
public class PolynomialRing<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>>
extends PolynomialSemiring<K, S>
implements Ring<Polynomial<K>>
{
    /**
     * Costruisce un anello di polinomi basato sulla struttura di anello scalare specificata.
     * 
     * @param scalarStructure la struttura algebrica dei coefficienti
     */
    public PolynomialRing(S scalarStructure) {
        super(scalarStructure);
    }


    @Override // AdditiveGroup impls
    public Polynomial<K> negate(Polynomial<K> e) {
        if (isZero(e)) return e;

        List<K> negatedCoeffs = new ArrayList<>(e.getCoefficients().size());
        for (K coeff : e.getCoefficients()) {
            negatedCoeffs.add(scalarStructure.negate(coeff));
        }

        return new Polynomial<>(this, scalarStructure, negatedCoeffs);
    }


    /**
     * Differenza {@code a - b} di due polinomi dello stesso grado i cui coefficienti di testa
     * coincidono per costruzione (e' il passo di una divisione euclidea): il coefficiente di testa
     * del risultato e' esattamente zero, invece del residuo di arrotondamento che la sottrazione
     * in virgola mobile lascerebbe, e il grado scende davvero.
     */
    public Polynomial<K> subtractCancellingLeadingTerm(Polynomial<K> a, Polynomial<K> b) {
        int top = a.degree();
        List<K> coefficients = new ArrayList<>(top);
        for (int i = 0; i < top; i++) {
            coefficients.add(scalarStructure.subtract(a.getCoefficient(i), b.getCoefficient(i)));
        }
        return of(coefficients);
    }

    @Override // AlgebraicStructure impls (override)
    public String getName() {
        return "Polynomial Ring over " + scalarStructure.getName();
    }
}