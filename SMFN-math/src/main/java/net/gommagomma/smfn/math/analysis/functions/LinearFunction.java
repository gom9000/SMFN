package net.gommagomma.smfn.math.analysis.functions;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;


/**
 * Rappresenta una funzione lineare f(x) = m*x + q.
 * 
 * @param <K> Il tipo di campo dei coefficienti (es. Real, Rational, Complex)
 */
public final class LinearFunction<K extends FieldElement<K>>
implements CommutativeRingElement<LinearFunction<K>>, Mapping<K, K>
{
    private final K m; // Coefficiente angolare
    private final K q; // Intercetta (termine noto)
    private final K zeroScalar;


    /**
     * Costruisce una funzione lineare f(x) = m*x + q.
     */
    public LinearFunction(K m, K q) {
        if (m == null || q == null) {
            throw new IllegalArgumentException("I coefficienti m e q non possono essere nulli.");
        }
        this.m = m;
        this.q = q;
        this.zeroScalar = m.getZero();
    }
    
    // --- Implementazione di MathFunction ---

    @Override
    public K apply(K x) {
        // f(x) = m*x + q
        return m.multiply(x).add(q);
    }

    // --- Implementazione di AlgebraicElement ---

    @Override
    public boolean isMathematicallyEqualTo(LinearFunction<K> other) {
        return this.m.isMathematicallyEqualTo(other.m) && this.q.isMathematicallyEqualTo(other.q);
    }

    @Override
    public LinearFunction<K> copy() {
        return new LinearFunction<>(this.m.copy(), this.q.copy());
    }

    @Override
    public LinearFunction<K> getZero() {
        return new LinearFunction<>(zeroScalar.getZero(), zeroScalar.getZero());
    }

    @Override
    public LinearFunction<K> getOne() {
        K one = zeroScalar.getOne();
        K zero = zeroScalar.getZero();
        return new LinearFunction<>(one, zero); // f(x) = 1*x + 0
    }

    // --- Implementazione di AdditiveMonoidElement / GroupElement ---

    @Override
    public LinearFunction<K> add(LinearFunction<K> other) {
        // (f+g)(x) = (m1+m2)x + (q1+q2)
        K newM = this.m.add(other.m);
        K newQ = this.q.add(other.q);
        return new LinearFunction<>(newM, newQ);
    }

    @Override
    public LinearFunction<K> negate() {
        // -f(x) = -m*x + -q
        K newM = this.m.negate();
        K newQ = this.q.negate();
        return new LinearFunction<>(newM, newQ);
    }
    
    // --- Implementazione di MultiplicativeMonoidElement ---

    @Override
    public LinearFunction<K> multiply(LinearFunction<K> other) {
        // La moltiplicazione in un anello di funzioni è la COMPOSIZIONE (f o g)(x) = f(g(x))
        // f(x) = m1*x + q1
        // g(x) = m2*x + q2
        // f(g(x)) = m1*(m2*x + q2) + q1 = (m1*m2)*x + (m1*q2 + q1)
        
        return compose(other);
    }

    public LinearFunction<K> compose(LinearFunction<K> other) {
        K newM = this.m.multiply(other.m);
        K newQ = this.m.multiply(other.q).add(this.q);
        return new LinearFunction<>(newM, newQ);
    }
}
