package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;

/**
 * Catalogo condiviso di valori {@link Polynomial}, sullo stesso schema delle catalog class
 * di {@code numerics} (es. {@code RationalTestValues}): nessun assert qui dentro, solo dati.
 * <p>
 * I polinomi sono costruiti su coefficienti {@link Rational} tramite
 * {@link EuclideanPolynomialRing}, cosi' da poter esercitare anche la divisione euclidea
 * (che richiede un campo come struttura scalare, non solo un anello).
 * <p>
 * {@link #standardValues()} copre i casi tipici (zero, uno, costanti, gradi bassi,
 * coefficienti razionali non interi). {@link #extremeValues()} copre i bordi: grado alto,
 * coefficienti a magnitudine estrema (compreso il numeratore Long.MIN_VALUE, che e' un
 * limite gia' noto e isolato per {@code Rational.negate()} -- vedi
 * {@link #valuesSafeForNegation()}), e un coefficiente vicino a zero ma non zero.
 */
final class PolynomialTestValues
{
	private PolynomialTestValues() {}

	private static final RationalField Q = RationalField.INSTANCE;

	/** Struttura polinomiale condivisa da tutto il catalogo: stesso campo di coefficienti per ogni valore. */
	static final EuclideanPolynomialRing<Rational, RationalField> RING = new EuclideanPolynomialRing<>(Q);

	private static Polynomial<Rational> poly(long... coeffs) {
		List<Rational> list = new ArrayList<>();
		for (long c : coeffs) {
			list.add(new Rational(c, 1));
		}
		return RING.of(list);
	}

	private static Polynomial<Rational> polyR(Rational... coeffs) {
		return RING.of(List.of(coeffs));
	}

	static List<Polynomial<Rational>> standardValues() {
		return List.of(
			RING.zero(),                                  // 0
			RING.one(),                                    // 1
			poly(-1),                                       // -1
			poly(0, 1),                                     // x
			poly(0, -1),                                     // -x
			poly(3, 2),                                       // 3 + 2x
			poly(-1, 0, 1),                                     // x^2 - 1
			poly(1, -2, 1),                                      // (x-1)^2
			poly(1, -2, 3, -4),                                   // -4x^3 + 3x^2 - 2x + 1
			polyR(new Rational(1, 2), new Rational(-3, 4), new Rational(5, 6)) // coefficienti razionali non interi
		);
	}

	/**
	 * Polinomi "strutturalmente" estremi (grado alto, molti zeri iniziali) ma con coefficienti
	 * a magnitudine piccola (0, 1, -1): sicuri da combinare tra loro o con standardValues()
	 * tramite add()/multiply()/divide() senza incappare nel contratto di overflow separato
	 * di Rational (vedi {@link #extremeScalarValues()}).
	 */
	static List<Polynomial<Rational>> extremeStructuralValues() {
		List<Rational> alternating = new ArrayList<>();
		for (int i = 0; i <= 12; i++) {
			alternating.add(new Rational(i % 2 == 0 ? 1 : -1, 1));
		}

		return List.of(
			poly(0, 0, 0, 0, 0, 0, 0, 0, 0, 1), // x^9, tanti zeri iniziali: esercita la normalizzazione del grado
			RING.of(alternating)                 // grado alto (12), coefficienti alterni +-1
		);
	}

	/**
	 * Polinomi con coefficienti Rational a magnitudine genuinamente estrema. Questi vanno usati
	 * SOLO in verifiche su un singolo polinomio alla volta (grado, getCoefficient(), copy(),
	 * identita' additiva/moltiplicativa con zero/uno): combinarli fra loro o con altri valori
	 * tramite add()/multiply()/divide() rientra nel contratto di overflow gia' documentato e
	 * verificato a parte per Rational (RationalInvariantsTest), non e' un difetto di Polynomial.
	 * Includono anche il numeratore Long.MIN_VALUE, che rende Rational.negate() non rappresentabile
	 * per quel coefficiente -- limite gia' noto, isolato da {@link #valuesSafeForNegation()}.
	 */
	static List<Polynomial<Rational>> extremeScalarValues() {
		return List.of(
			poly(Long.MAX_VALUE),                            // costante a magnitudine estrema
			poly(Long.MIN_VALUE),                              // costante a magnitudine estrema (numeratore non negabile)
			polyR(new Rational(1, Long.MAX_VALUE))               // coefficiente vicino a zero ma non zero
		);
	}

	static List<Polynomial<Rational>> extremeValues() {
		List<Polynomial<Rational>> all = new ArrayList<>(extremeStructuralValues());
		all.addAll(extremeScalarValues());
		return all;
	}

	/** Standard + estremi, per verifiche che toccano un solo polinomio alla volta (mai due combinati fra loro). */
	static List<Polynomial<Rational>> allValues() {
		List<Polynomial<Rational>> all = new ArrayList<>(standardValues());
		all.addAll(extremeValues());
		return all;
	}

	/**
	 * Standard + estremi strutturali (esclusi gli estremi scalari): il sottoinsieme sicuro da
	 * combinare liberamente tramite add()/multiply()/divide() senza attraversare il contratto
	 * di overflow separato di Rational.
	 */
	static List<Polynomial<Rational>> combinationSafeValues() {
		List<Polynomial<Rational>> all = new ArrayList<>(standardValues());
		all.addAll(extremeStructuralValues());
		return all;
	}

	/** Come {@link #allValues()}, escludendo il polinomio zero: utile per test che dividono per il catalogo stesso. */
	static List<Polynomial<Rational>> nonZeroValues() {
		return filterNonZero(allValues());
	}

	/** Come {@link #combinationSafeValues()}, escludendo il polinomio zero. */
	static List<Polynomial<Rational>> nonZeroCombinationSafeValues() {
		return filterNonZero(combinationSafeValues());
	}

	private static List<Polynomial<Rational>> filterNonZero(List<Polynomial<Rational>> values) {
		List<Polynomial<Rational>> result = new ArrayList<>();
		for (Polynomial<Rational> p : values) {
			if (!RING.isZero(p)) {
				result.add(p);
			}
		}
		return result;
	}

	/**
	 * Come {@link #combinationSafeValues()}, escludendo i polinomi che hanno almeno un coefficiente
	 * con numeratore Long.MIN_VALUE: negate() (e quindi l'inverso additivo) non e' rappresentabile
	 * per quel coefficiente -- limite gia' noto e verificato a livello di Rational, non un
	 * difetto del polinomio. Dato che gli estremi scalari sono gia' esclusi da combinationSafeValues(),
	 * qui non ce n'e' effettivamente bisogno, ma il filtro resta per chiarezza e robustezza futura.
	 */
	static List<Polynomial<Rational>> valuesSafeForNegation() {
		List<Polynomial<Rational>> result = new ArrayList<>();
		for (Polynomial<Rational> p : combinationSafeValues()) {
			boolean hasUnsafeCoefficient = false;
			for (Rational c : p.getCoefficients()) {
				if (c.getNumerator() == Long.MIN_VALUE) {
					hasUnsafeCoefficient = true;
					break;
				}
			}
			if (!hasUnsafeCoefficient) {
				result.add(p);
			}
		}
		return result;
	}
}
