package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Interfaccia radice per qualsiasi struttura algebrica, definisce le proprietà 
 * e i contratti fondamentali condivisi da insiemi dotati di operazioni (come appartenenza e uguaglianza).
 *
 * @param <E> il tipo degli elementi appartenenti alla struttura
 */
public interface AlgebraicStructure<E extends AlgebraicElement<E>>
{
	/**
     * Restituisce il nome descrittivo della struttura algebrica.
     * 
     * @return una stringa contenente il nome della struttura
     */
    String getName();

    /**
     * Verifica se un determinato elemento appartiene a questa struttura algebrica.
     * 
     * @param e l'elemento da verificare
     * @return true se l'elemento appartiene alla struttura, false altrimenti
     */
    boolean contains(E e);

    /**
     * Verifica l'uguaglianza tra due elementi nel contesto di questa struttura.
     * 
     * @param a il primo elemento
     * @param b il secondo elemento
     * @return true se i due elementi sono considerati uguali, false altrimenti
     */
    boolean areEqual(E a, E b);
}
