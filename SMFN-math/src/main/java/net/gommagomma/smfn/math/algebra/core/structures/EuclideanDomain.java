package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Absolutable;

/**
 * Rappresenta un dominio euclideo, ovvero un anello integro dotato di norma euclidea
 * che permette di definire la divisione con resto (algoritmo di Euclide).
 *
 * @param <E> il tipo degli elementi dell'anello
 * @param <N> il tipo del codominio della norma euclidea (es. interi o naturali)
 */
public interface EuclideanDomain<E extends AlgebraicElement<E>, N extends AlgebraicElement<N>>
extends CommutativeRing<E>
{
	/**
     * Calcola il quoziente della divisione euclidea tra due elementi.
     * 
     * @param a il dividendo
     * @param b il divisore
     * @return il quoziente della divisione
     */
	E quotient(E a, E b);

	/**
     * Calcola il resto della divisione euclidea tra due elementi.
     * 
     * @param a il dividendo
     * @param b il divisore
     * @return il resto della divisione
     */
	E remainder(E a, E b);

	/**
     * Restituisce il grado (o misura euclidea) di un elemento.
     * 
     * @param e l'elemento di cui valutare il grado
     * @return l'elemento rappresentante il grado
     */
	N degree(E e);

	/**
     * Calcola il Massimo Comun Divisore (gcd) tra due elementi utilizzando l'algoritmo di Euclide.
     * 
     * @param a il primo elemento
     * @param b il secondo elemento
     * @return il massimo comun divisore normalizzato
     */
	default E gcd(E a, E b) {
        E x = a;
        E y = b;
        while (!isZero(y)) {
            E temp = y;
            y = remainder(x, y);
            x = temp;
        }
        return normalize(x);
    }

	/**
     * Calcola il Minimo Comune Multiplo (mcm) tra due elementi.
     * 
     * @param a il primo elemento
     * @param b il secondo elemento
     * @return il minimo comune multiplo normalizzato
     */
	default E lcm(E a, E b) {
		if (isZero(a) || isZero(b)) return zero();
		E gcd = gcd(a, b);
		return normalize(multiply(quotient(a, gcd), b));
	}

	/**
     * Normalizza l'elemento (ad esempio rendendolo positivo se supporta la capacità Absolutable).
     * 
     * @param element l'elemento da normalizzare
     * @return l'elemento normalizzato
     */
	@SuppressWarnings("unchecked")
	default E normalize(E element) {
	    if (element instanceof Absolutable) {
	        return (E) ((Absolutable<?>) element).abs();
	    }
	    return element;
	}
}
