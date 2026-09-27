package net.gommagomma.smfn.math.algebra.numerics;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo condiviso di valori {@link ZnElement} per i test di questo tipo, sullo stesso
 * schema di {@link RationalTestValues} e delle altre catalog class del pacchetto: nessun
 * assert qui dentro, solo dati.
 * <p>
 * A differenza degli altri tipi numerici, ZnElement e' parametrizzato da un modulo: due
 * elementi con modulo diverso non sono nemmeno confrontabili con add()/multiply() (lo
 * anello stesso lo impedisce, {@code checkModulus} lancia {@link IllegalArgumentException}).
 * Per questo il catalogo e' organizzato per modulo: {@link #groupedByModulus()} restituisce
 * un gruppo di valori per ciascun modulo scelto, e i test sugli invarianti algebrici devono
 * scorrere le coppie/terne solo all'interno di un singolo gruppo.
 * <p>
 * I moduli scelti coprono: il caso banale (modulo 1, dove tutto si riduce a 0), un primo
 * piccolo (7, dove ogni elemento non nullo e' invertibile), un composto piccolo con
 * divisori dello zero (12 = 3*4), e un modulo enorme vicino a Long.MAX_VALUE (dove
 * ZnRing usa BigInteger internamente proprio per evitare l'overflow di un long).
 */
final class ZnTestValues
{
	private ZnTestValues() {}

	static List<SignedInt> standardModuli() {
		return List.of(
			new SignedInt(2),
			new SignedInt(7),
			new SignedInt(12)
		);
	}

	static List<SignedInt> extremeModuli() {
		return List.of(
			new SignedInt(1),
			new SignedInt(Long.MAX_VALUE)
		);
	}

	static List<SignedInt> allModuli() {
		List<SignedInt> all = new ArrayList<>(standardModuli());
		all.addAll(extremeModuli());
		return all;
	}

	/**
	 * Valori rappresentativi per un dato modulo: 0, 1, -1, 2, -2, il bordo superiore
	 * (modulo-1), un valore appena oltre il modulo (per verificare la riduzione), e i
	 * casi estremi di SignedInt (Long.MIN_VALUE/Long.MAX_VALUE), sempre normalizzati
	 * dal costruttore di ZnElement rispetto al modulo dato.
	 */
	static List<ZnElement> valuesForModulus(SignedInt modulus) {
		long m = modulus.getValue();

		List<Long> raws = new ArrayList<>();
		raws.add(0L);
		raws.add(1L);
		raws.add(-1L);
		raws.add(2L);
		raws.add(-2L);
		raws.add(1000L);
		raws.add(-1000L);
		raws.add(Long.MAX_VALUE);
		raws.add(Long.MIN_VALUE);
		if (m > 1) {
			raws.add(m - 1);
		}
		if (m < Long.MAX_VALUE) {
			raws.add(m + 1); // evita l'overflow quando il modulo e' gia' Long.MAX_VALUE
		}

		List<ZnElement> values = new ArrayList<>();
		for (long raw : raws) {
			values.add(new ZnElement(new SignedInt(raw), modulus));
		}
		return values;
	}

	/** Un gruppo di valori per ciascun modulo standard: le coppie/terne dei test vanno scorse solo dentro un gruppo. */
	static List<List<ZnElement>> standardGroupedByModulus() {
		List<List<ZnElement>> groups = new ArrayList<>();
		for (SignedInt m : standardModuli()) {
			groups.add(valuesForModulus(m));
		}
		return groups;
	}

	/** Come {@link #standardGroupedByModulus()}, ma su tutti i moduli (standard + estremi). */
	static List<List<ZnElement>> allGroupedByModulus() {
		List<List<ZnElement>> groups = new ArrayList<>();
		for (SignedInt m : allModuli()) {
			groups.add(valuesForModulus(m));
		}
		return groups;
	}
}
