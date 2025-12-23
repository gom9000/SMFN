package net.gommagomma.smfn.math.algebra.core.algorithms;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.EuclideanDomainElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Orderable;

/**
 * Algoritmi algebrici universali operanti su strutture euclidee.
 */
public final class AlgebraicAlgorithms
{
    private AlgebraicAlgorithms() {}

    /**
     * Calcola il Massimo Comun Divisore (GCD) tra due elementi di un Dominio Euclideo.
     * Utilizza l'algoritmo di Euclide basato sulle divisioni successive.
     * @param <E> Il tipo dell'elemento (es. SignedInt, EuclideanPolynomial)
     * @param <N> Il tipo della norma (es. SignedInt, Natural)
     */
    public static <E extends EuclideanDomainElement<E, N>, N extends Orderable<N>> E gcd(E a, E b) {
        E r0 = a;
        E r1 = b;

        while (!r1.isZero()) {
            E temp = r1;
            r1 = r0.remainder(r1);
            r0 = temp;
        }
        
        return r0;
    }

    /**
     * Calcola il Minimo Comune Multiplo (LCM).
     * Nota: Richiede che il dominio sia commutativo e integro.
     */
    public static <E extends EuclideanDomainElement<E, N>, N extends Orderable<N>> E lcm(E a, E b) {
        if (a.isZero() || b.isZero()) return a.getZero();
        
        E gcd = gcd(a, b);
        // lcm(a, b) = |a * b| / gcd(a, b)
        return a.quotient(gcd).multiply(b);
    }
}