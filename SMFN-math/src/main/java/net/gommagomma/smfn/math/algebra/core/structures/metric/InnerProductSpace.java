package net.gommagomma.smfn.math.algebra.core.structures.metric;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Conjugable;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Rappresenta uno spazio con prodotto interno (o spazio pre-hilbertiano), 
 * ovvero uno spazio normato in cui è definito un prodotto scalare hermitiano (o bilineare) 
 * che soddisfa le proprietà di sesquilinearità, simmetria coniugata e positività definita.
 *
 * @param <V> il tipo dell'elemento vettoriale
 * @param <K> il tipo dello scalare del campo sottostante
 * @param <S> il tipo del campo scalare di supporto
 */
public interface InnerProductSpace<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> 
extends NormedSpace<V, K, S>
{
	/**
     * Calcola il prodotto interno (o prodotto scalare) tra due vettori.
     * 
     * @param a il primo vettore
     * @param b il secondo vettore
     * @return lo scalare risultante dal prodotto interno
     */
	K innerProduct(V a, V b);

	/**
     * Coniuga lo scalare se l'elemento implementa l'interfaccia Conjugable.
     * 
     * @param value lo scalare da eventuale coniugazione
     * @return lo scalare coniugato se applicabile, oppure il valore inalterato
     */
	@SuppressWarnings("unchecked")
	default K conjugateIfPossible(K value) {
		if (value instanceof Conjugable) {
			return (K) ((Conjugable<K>) value).conjugate();
		}
		return value;
	}
}
