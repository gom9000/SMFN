package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta un ente geometrico nello spazio, modellato tramite la sua funzione implicita $f(P) = 0$.
 * Estende l'interfaccia Mapping mappando punti dello spazio in elementi algebrici di output 
 * (tipicamente valori reali che rappresentano una distanza o una funzione di livello).
 * 
 * @param <D> Il tipo di input (es. Point).
 * @param <C> Il tipo di output (es. Real).
 */
public interface GeometryEntity<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
extends Mapping<D, C>
{
	/**
     * Restituisce la dimensione dello spazio ambiente in cui l'ente è immerso.
     * 
     * @return la dimensione ambiente (es. 2 per il piano, 3 per lo spazio tridimensionale)
     */
	int getAmbientDimension();

	/**
     * Restituisce la dimensione intrinseca dell'ente geometrico.
     * 
     * @return la dimensione intrinseca (es. 1 per una linea o curva, 2 per una superficie)
     */
	int getEntityDimension();

	/**
     * Verifica se un determinato punto appartiene all'ente geometrico (ovvero se giace su di esso).
     * 
     * @param point il punto da verificare
     * @return true se il punto appartiene all'ente, false altrimenti
     */
	boolean isOnEntity(D point);

	/**
     * Valuta la funzione implicita dell'ente in corrispondenza del punto specificato.
     * Implementa il metodo di mapping ereditato da Mapping.
     * 
     * @param point il punto in cui valutare la funzione
     * @return il valore della funzione implicita nel punto
     */
	@Override
	default C apply(D point) {
		return implicitFunctionAt(point);
	}

	/**
     * Calcola il valore della funzione implicita f(P) dell'ente per il punto dato.
     * 
     * @param point il punto geometrico di valutazione
     * @return il valore algebrico risultante dalla funzione implicita
     */
	C implicitFunctionAt(D point);
}
