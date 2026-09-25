package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;

/**
 * Rappresenta uno spazio lineare (o spazio vettoriale), in cui gli scalari formano un campo 
 * e la struttura supporta le proprietà geometriche e algebriche complete dei vettori.
 *
 * @param <V> il tipo dell'elemento vettoriale
 * @param <K> il tipo dello scalare appartenente al campo
 * @param <S> il tipo del campo scalare
 */
public interface LinearSpace<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> 
extends Module<V, K, S>
{}
