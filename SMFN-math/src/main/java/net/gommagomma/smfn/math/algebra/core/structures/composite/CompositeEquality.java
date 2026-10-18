package net.gommagomma.smfn.math.algebra.core.structures.composite;

import java.util.function.IntFunction;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Uguaglianza di oggetti composti (vettori, matrici, polinomi) a partire dalle loro componenti.
 * <p>
 * In una struttura approssimata due componenti sono uguali se differiscono per meno di
 * {@code epsilon} volte la scala dell'<i>intero</i> oggetto, cioe' la componente di modulo massimo
 * tra i due operandi: il rumore di arrotondamento di una componente dipende dalla grandezza delle
 * altre, non dalla sua. Una componente nulla in un operando e di ordine epsilon nell'altro e'
 * quindi lo stesso oggetto, mentre due oggetti di grandezza piccola ma ben distinti restano distinti.
 * In una struttura esatta il confronto e' esatto componente per componente.
 */
public final class CompositeEquality
{
	private CompositeEquality() {}

	/**
	 * @param scalars la struttura delle componenti
	 * @param count il numero di componenti
	 * @param a la componente i-esima del primo operando
	 * @param b la componente i-esima del secondo operando
	 */
	public static <K extends ScalarElement<K>> boolean areEqual(ScalarStructure<K> scalars, int count,
	                                                            IntFunction<K> a, IntFunction<K> b) {
		if (scalars.isExact()) {
			for (int i = 0; i < count; i++) {
				if (!scalars.areEqual(a.apply(i), b.apply(i))) return false;
			}
			return true;
		}

		double scale = 0.0;
		for (int i = 0; i < count; i++) {
			scale = Math.max(scale, scalars.magnitude(a.apply(i)).getValue());
			scale = Math.max(scale, scalars.magnitude(b.apply(i)).getValue());
		}
		Real reference = new Real(scale);
		for (int i = 0; i < count; i++) {
			if (!scalars.areEqual(a.apply(i), b.apply(i), reference)) return false;
		}
		return true;
	}
}
