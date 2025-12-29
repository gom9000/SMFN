package net.gommagomma.smfn.math.analysis.core.functionals;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;

/**
 * Un Funzionale trasforma un Morfismo (funzione) in uno scalare.
 * Esempio: Integrale Definito (prende un polinomio, restituisce un Real).
 */
public interface Functional<K extends FieldElement<K>, V extends AlgebraicElement<V>, M extends Morphism<V, K>> 
extends Mapping<M, K>
{
    @Override
    K apply(M function);
}
