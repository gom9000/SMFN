package net.gommagomma.smfn.math.algebra.core.structures.metric;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.LinearSpace;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Rappresenta uno spazio normato, ovvero uno spazio vettoriale (spazio lineare) 
 * su un campo in cui è definita una norma che induce una metrica naturale.
 *
 * @param <V> il tipo dell'elemento vettoriale
 * @param <K> il tipo dello scalare appartenente al campo
 * @param <S> il tipo del campo scalare di supporto
 */
public interface NormedSpace<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> 
extends LinearSpace<V, K, S>, MetricSpace<V>
{
	/**
     * Calcola la norma (lunghezza o magnitudine) dell'elemento vettoriale specificato.
     * 
     * @param v l'elemento vettoriale
     * @return un'istanza di Real che rappresenta la norma
     */
	Real norm(V v);

	@Override
    default Real distance(V a, V b) {
        V difference = subtract(a, b); 
        return norm(difference);
    }
}
