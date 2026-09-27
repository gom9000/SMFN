package net.gommagomma.smfn.math.algebra.numerics;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo condiviso di valori {@link Natural}, sullo stesso schema di
 * {@link RationalTestValues}: nessun assert qui dentro, solo dati.
 * <p>
 * A differenza degli altri tipi numerici di questo pacchetto, Natural non ammette
 * valori negativi (il costruttore li rifiuta), quindi qui non c'e' un equivalente di
 * Long.MIN_VALUE da isolare: {@link #extremeValues()} si limita al bordo superiore.
 */
final class NaturalTestValues
{
	private NaturalTestValues() {}

	static List<Natural> standardValues() {
		return List.of(
			new Natural(0),
			new Natural(1),
			new Natural(2),
			new Natural(10),
			new Natural(1000)
		);
	}

	static List<Natural> extremeValues() {
		return List.of(
			new Natural(Long.MAX_VALUE),
			new Natural(Long.MAX_VALUE - 1)
		);
	}

	static List<Natural> allValues() {
		List<Natural> all = new ArrayList<>(standardValues());
		all.addAll(extremeValues());
		return all;
	}
}
