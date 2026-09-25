package net.gommagomma.smfn.math.analysis.functions;

import net.gommagomma.smfn.math.analysis.core.functions.ScalarFunction;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;

/**
 * Modellizza una funzione affine/lineare ad una variabile scalare della forma: f(x) = m * x + q
 * definita su un anello algebrico K, dove m e' il coefficiente angolare (pendenza) e q e' l'intercetta (termine noto).
 * @param <K> Il tipo dello scalare appartenente al campo sottostante
 * @param <S> La struttura algebrica di campo e struttura scalare associata a K
 */
public final class LinearFunction<K extends ScalarElement<K>>
implements ScalarFunction<K>
{
	private final Ring<K> ring;
    private final K m; // coefficiente angolare
    private final K q; // intercetta


    public LinearFunction(Ring<K> field, K m, K q) {
        if (m == null || q == null) {
            throw new IllegalArgumentException("I coefficienti m e q non possono essere nulli.");
        }
        this.ring = field;
        this.m = m;
        this.q = q;
    }

    @Override
    public K apply(K x) {
        return ring.add(ring.multiply(m, x), q);
    }

    /**
     * Composizione di funzioni: (f.g)(x) = f(g(x)) = (m1*m2)*x + (m1*q2+q1).
     * Operazione legittima di per se' -- solo non e' la "moltiplicazione"
     * di una struttura di anello, che qui non esiste.
     */
    public LinearFunction<K> compose(LinearFunction<K> other) {
        K newM = ring.multiply(this.m, other.m);
        K newQ = ring.add(ring.multiply(this.m, other.q), this.q);
        return new LinearFunction<>(ring, newM, newQ);
    }

    public LinearFunction<K> add(LinearFunction<K> other) {
        return new LinearFunction<>(ring, ring.add(this.m, other.m), ring.add(this.q, other.q));
    }

    public K getSlope() { return m; }
    public K getIntercept() { return q; }
    public Ring<K> getField() { return ring; }

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
