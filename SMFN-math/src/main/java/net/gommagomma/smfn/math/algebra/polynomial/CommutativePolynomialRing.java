package net.gommagomma.smfn.math.algebra.polynomial;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Rappresenta un anello di polinomi commutativo (K[x]) i cui coefficienti appartengono 
 * a un anello commutativo sottostante.
 * 
 * @param <K> il tipo degli elementi scalari (coefficienti) del polinomio
 * @param <S> il tipo della struttura algebrica scalare sottostante (anello commutativo e struttura scalare)
 */
public class CommutativePolynomialRing<K extends ScalarElement<K>, S extends CommutativeRing<K> & ScalarStructure<K>> 
extends PolynomialRing<K, S> 
implements CommutativeRing<Polynomial<K>>
{
    /**
     * Costruisce un anello di polinomi commutativo basato sulla struttura scalare specificata.
     * 
     * @param scalarStructure la struttura algebrica dei coefficienti
     */
    public CommutativePolynomialRing(S scalarStructure) {
        super(scalarStructure);
    }


    @Override // AlgebraicStructure impls (override)
    public String getName() {
        return "Commutative Polynomial Ring over " + scalarStructure.getName();
    }
}