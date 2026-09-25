package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.elements.ExactElement;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Rappresenta una struttura algebrica basata su elementi esatti 
 * (es. aritmetica intera o razionale senza errori di arrotondamento).
 *
 * @param <K> il tipo degli elementi scalari esatti
 */
public interface ExactStructure<K extends ExactElement<K>>
extends ScalarStructure<K>
{
	@Override
    default boolean isExact() { return true; }

	@Override
	default boolean areEqual(K a, K b) {
	    return Objects.equals(a, b);
	}	
}
