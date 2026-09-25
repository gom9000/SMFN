package net.gommagomma.smfn.math.algebra.core.elements;

/**
 * Interfaccia radice per qualsiasi elemento all'interno di una struttura algebrica.
 * Sfrutta il polimorfismo F-bounded per garantire che le operazioni restituiscano il tipo concreto corretto,
 * evitando tediosi cast espliciti.
 *
 * @param <E> il tipo concreto dell'elemento algebrico
 */
public interface AlgebraicElement<E extends AlgebraicElement<E>>
{
	/**
     * Crea e restituisce una copia sicura (clone) dell'elemento corrente.
     * 
     * @return una nuova istanza identica all'elemento corrente
     */
	E copy();
}
