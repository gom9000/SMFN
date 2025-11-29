package net.gommagomma.smfn.math.analysis.solvers.differential;


import net.gommagomma.smfn.math.algebra.core.MathFunction;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;


/**
 * Interfaccia per i metodi di differenziazione numerica.
 * Calcola un'approssimazione della derivata di una funzione in un punto.
 */
public interface NumericalDifferentiator<K extends FieldElement<K>>
{    
    /**
     * Calcola la derivata approssimata di f(x) nel punto x.
     * @param function La funzione f(x).
     * @param x Il punto in cui valutare la derivata.
     * @param h Lo step size (dimensione del passo), un valore piccolo e non nullo.
     * @return L'approssimazione della derivata f'(x).
     */
    K derivativeAt(MathFunction<K, K> function, K x, K h);
}
