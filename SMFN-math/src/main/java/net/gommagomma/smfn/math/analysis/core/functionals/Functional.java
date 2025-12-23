package net.gommagomma.smfn.math.analysis.core.functionals;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;

/**
 * Funzionale: Mappa una Funzione (F: D -> K) e un Contesto C a uno Scalare K.
 * K: Il campo scalare di output (es. Real).
 * D: Il tipo del Dominio della Funzione f (es. Real, VectorElement).
 * C: Il tipo del Contesto di Valutazione (es. un Intervallo, un Punto).
 */
public interface Functional<K extends FieldElement<K>, D extends AlgebraicElement<D>, C>
{    
    /**
     * Esegue l'operazione funzionale sulla funzione 'f' dato il 'context'.
     *
     * @param f La funzione MathFunction (D -> K) su cui operare.
     * @param context Il contesto C (es. l'intervallo [a, b] per l'integrale definito).
     * @return Il risultato scalare K.
     */
    K evaluate(Mapping<D, K> f, C context);
}
