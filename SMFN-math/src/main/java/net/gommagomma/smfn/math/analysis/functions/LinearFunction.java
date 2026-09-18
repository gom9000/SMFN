package net.gommagomma.smfn.math.analysis.functions;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;

/**
 * f(x) = m*x + q, funzione affine (lineare + traslazione) su un campo K.
 *
 * Non e' un elemento di anello sotto (addizione, composizione): la
 * composizione di funzioni affini non distribuisce a sinistra sull'addizione
 * quando q != 0 (h.(f+g) != h.f + h.g in generale), e in generale f.g != g.f.
 * Dichiararla CommutativeRingElement, come faceva la versione precedente,
 * era matematicamente scorretto -- non solo un problema di API. Qui resta
 * un valore puro con una sola capacita' reale: valutarsi in un punto, tramite
 * Mapping<K,K>, esattamente come Polynomial/PolynomialFunction.
 */
public final class LinearFunction<K extends ScalarElement<K>>
implements Mapping<K, K>
{
	private final Field<K> field;
    private final K m; // coefficiente angolare
    private final K q; // intercetta


    public LinearFunction(Field<K> field, K m, K q) {
        if (m == null || q == null) {
            throw new IllegalArgumentException("I coefficienti m e q non possono essere nulli.");
        }
        this.field = field;
        this.m = m;
        this.q = q;
    }

    @Override
    public K apply(K x) {
        return field.add(field.multiply(m, x), q);
    }

    /**
     * Composizione di funzioni: (f.g)(x) = f(g(x)) = (m1*m2)*x + (m1*q2+q1).
     * Operazione legittima di per se' -- solo non e' la "moltiplicazione"
     * di una struttura di anello, che qui non esiste.
     */
    public LinearFunction<K> compose(LinearFunction<K> other) {
        K newM = field.multiply(this.m, other.m);
        K newQ = field.add(field.multiply(this.m, other.q), this.q);
        return new LinearFunction<>(field, newM, newQ);
    }

    public LinearFunction<K> add(LinearFunction<K> other) {
        return new LinearFunction<>(field, field.add(this.m, other.m), field.add(this.q, other.q));
    }

    public K getSlope() { return m; }
    public K getIntercept() { return q; }
    public Field<K> getField() { return field; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinearFunction)) return false;
        LinearFunction<?> other = (LinearFunction<?>) o;
        return java.util.Objects.equals(this.m, other.m) && java.util.Objects.equals(this.q, other.q);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(m, q);
    }

    @Override
    public String toString() {
        return m + "*x + " + q;
    }
}
