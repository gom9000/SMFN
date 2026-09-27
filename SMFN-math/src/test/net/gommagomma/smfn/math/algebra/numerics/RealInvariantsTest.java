package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Invarianti algebrici di Real sul catalogo di RealTestValues, sullo stesso spirito di
 * RationalInvariantsTest: leggi verificate su molti valori scelti apposta per includere
 * i bordi (qui: la scala in virgola mobile), non su una singola tripla come RealFieldTest.
 * <p>
 * A differenza di Rational, qui il limite non e' l'overflow di un long ma l'arrotondamento
 * in virgola mobile: a magnitudine estrema, una tolleranza assoluta (come l'epsilon di
 * RealField) non ha senso, quindi le leggi con arrotondamento (associativita',
 * distributivita') usano qui una tolleranza relativa esplicita per gli estremi, invece di
 * RealField.areEqual (pensata per una scala moderata).
 */
@DisplayName("Real: invarianti algebrici sul catalogo di valori (standard + estremi)")
class RealInvariantsTest
{
	private final RealField R = RealField.INSTANCE;

	/** Uguaglianza a tolleranza relativa, per confronti a scala arbitraria (anche 1e300). */
	private static boolean approxEqualRelative(double a, double b, double relativeTolerance) {
		if (a == b) {
			return true;
		}
		if (Double.isInfinite(a) || Double.isInfinite(b)) {
			return a == b;
		}
		double scale = Math.max(Math.abs(a), Math.abs(b));
		if (scale == 0.0) {
			return true;
		}
		return Math.abs(a - b) <= relativeTolerance * scale;
	}

	@Test
	@DisplayName("Addizione commutativa, bit-esatta (garanzia IEEE 754): a+b == b+a, su tutto il catalogo, estremi inclusi")
	void additionIsCommutative() {
		List<Real> values = RealTestValues.allValues();
		for (Real a : values) {
			for (Real b : values) {
				assertEquals(R.add(a, b), R.add(b, a), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Moltiplicazione commutativa, bit-esatta (garanzia IEEE 754): a*b == b*a, su tutto il catalogo, estremi inclusi")
	void multiplicationIsCommutative() {
		List<Real> values = RealTestValues.allValues();
		for (Real a : values) {
			for (Real b : values) {
				assertEquals(R.multiply(a, b), R.multiply(b, a), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Addizione associativa (a meno di arrotondamento): (a+b)+c ~= a+(b+c), su terne di valori standard")
	void additionIsAssociativeForStandardMagnitudes() {
		List<Real> values = RealTestValues.standardValues();
		for (Real a : values) {
			for (Real b : values) {
				for (Real c : values) {
					Real left = R.add(R.add(a, b), c);
					Real right = R.add(a, R.add(b, c));
					assertTrue(R.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c + " left=" + left + " right=" + right);
				}
			}
		}
	}

	@Test
	@DisplayName("Moltiplicazione associativa (a meno di arrotondamento): (a*b)*c ~= a*(b*c), su terne di valori standard")
	void multiplicationIsAssociativeForStandardMagnitudes() {
		List<Real> values = RealTestValues.standardValues();
		for (Real a : values) {
			for (Real b : values) {
				for (Real c : values) {
					Real left = R.multiply(R.multiply(a, b), c);
					Real right = R.multiply(a, R.multiply(b, c));
					assertTrue(R.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c + " left=" + left + " right=" + right);
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva (a meno di arrotondamento): a*(b+c) ~= a*b + a*c, su terne di valori standard")
	void multiplicationDistributesOverAdditionForStandardMagnitudes() {
		List<Real> values = RealTestValues.standardValues();
		for (Real a : values) {
			for (Real b : values) {
				for (Real c : values) {
					Real left = R.multiply(a, R.add(b, c));
					Real right = R.add(R.multiply(a, b), R.multiply(a, c));
					assertTrue(R.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c + " left=" + left + " right=" + right);
				}
			}
		}
	}

	@Test
	@DisplayName("Associativita' a scala estrema regge a tolleranza relativa quando i valori sono dello stesso segno (nessuna cancellazione)")
	void additionIsAssociativeAtExtremeScaleWithoutCancellation() {
		// Solo valori dello stesso segno: sommare termini con lo stesso segno non puo' mai
		// perdere cifre significative per cancellazione (a differenza di sottrarre due valori
		// vicini in modulo, vedi additionAssociativityFailsUnderCatastrophicCancellation sotto),
		// quindi qui la tolleranza relativa e' un confronto onesto, non ottimistico.
		List<Real> positiveExtremes = List.of(
			new Real(1e-308), new Real(1e-200), new Real(1e200), new Real(1e308), new Real(Double.MAX_VALUE)
		);
		double relTol = 1e-9;
		for (Real a : positiveExtremes) {
			for (Real b : positiveExtremes) {
				for (Real c : positiveExtremes) {
					double leftAdd = (a.getValue() + b.getValue()) + c.getValue();
					double rightAdd = a.getValue() + (b.getValue() + c.getValue());
					if (Double.isInfinite(leftAdd) || Double.isInfinite(rightAdd)) {
						assertEquals(Double.isInfinite(leftAdd), Double.isInfinite(rightAdd), "a=" + a + " b=" + b + " c=" + c);
						continue;
					}
					assertTrue(approxEqualRelative(leftAdd, rightAdd, relTol), "a=" + a + " b=" + b + " c=" + c + " left=" + leftAdd + " right=" + rightAdd);
				}
			}
		}
	}

	@Test
	@DisplayName("LIMITE NOTO: l'associativita' dell'addizione non regge sotto cancellazione catastrofica, nemmeno a tolleranza relativa")
	void additionAssociativityFailsUnderCatastrophicCancellation() {
		// (1e-308 + 1e308) - 1e308 arrotonda a 0.0 (1e-308 e' assorbito nell'arrotondamento
		// di una somma dominata da 1e308), mentre 1e-308 + (1e308 - 1e308) da' esattamente
		// 1e-308 (la sottrazione di due valori uguali e' esatta). Nessuna tolleranza --
		// relativa o assoluta -- puo' conciliare 0.0 con un valore diverso da zero: e' un
		// limite intrinseco della virgola mobile (perdita di precisione per cancellazione
		// catastrofica), non un difetto di RealField. Documentato qui perche' resti visibile,
		// non perche' vada "corretto".
		double a = 1e-308, b = 1e308, c = -1e308;
		double left = (a + b) + c;
		double right = a + (b + c);

		assertEquals(0.0, left, 0.0);
		assertEquals(1e-308, right, 0.0);
		assertTrue(left != right);
	}

	@Test
	@DisplayName("Zero e' identita' additiva (a meno di arrotondamento) su tutto il catalogo, estremi inclusi")
	void zeroIsAdditiveIdentity() {
		for (Real a : RealTestValues.allValues()) {
			assertTrue(R.areEqual(a, R.add(a, R.zero())), "a=" + a);
			assertTrue(R.areEqual(a, R.add(R.zero(), a)), "a=" + a);
		}
	}

	@Test
	@DisplayName("Uno e' identita' moltiplicativa (a meno di arrotondamento) su tutto il catalogo, estremi inclusi")
	void oneIsMultiplicativeIdentity() {
		for (Real a : RealTestValues.allValues()) {
			assertTrue(R.areEqual(a, R.multiply(a, R.one())), "a=" + a);
			assertTrue(R.areEqual(a, R.multiply(R.one(), a)), "a=" + a);
		}
	}

	@Test
	@DisplayName("Inverso additivo: a + (-a) == 0 esattamente (sottrazione di se stesso non arrotonda mai), su tutto il catalogo")
	void additiveInverseReturnsZero() {
		for (Real a : RealTestValues.allValues()) {
			Real sum = R.add(a, R.negate(a));
			assertTrue(R.areEqual(R.zero(), sum), "a=" + a + " sum=" + sum);
		}
	}

	@Test
	@DisplayName("abs() non e' mai negativo, su tutto il catalogo, estremi inclusi")
	void absIsNeverNegative() {
		for (Real a : RealTestValues.allValues()) {
			assertTrue(a.abs().getValue() >= 0.0, "a=" + a);
		}
	}

	@Test
	@DisplayName("compareTo e' antisimmetrico su tutto il catalogo, estremi inclusi")
	void compareToIsAntisymmetric() {
		List<Real> values = RealTestValues.allValues();
		for (Real a : values) {
			for (Real b : values) {
				assertEquals(Integer.signum(a.compareTo(b)), -Integer.signum(b.compareTo(a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("compareTo e' riflessivo: a.compareTo(a) == 0, su tutto il catalogo")
	void compareToIsReflexive() {
		for (Real a : RealTestValues.allValues()) {
			assertEquals(0, a.compareTo(a), "a=" + a);
		}
	}

	@Test
	@DisplayName("isLessThan e' coerente con compareTo su tutto il catalogo, estremi inclusi")
	void isLessThanIsConsistentWithCompareTo() {
		List<Real> values = RealTestValues.allValues();
		for (Real a : values) {
			for (Real b : values) {
				assertEquals(a.compareTo(b) < 0, a.isLessThan(b), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("sqrt(a)^2 ~= a per ogni valore non negativo del catalogo, a tolleranza relativa")
	void sqrtSquaredReturnsOriginalForNonNegativeValues() {
		for (Real a : RealTestValues.nonNegativeValues()) {
			double result = a.sqrt().power(2).getValue();
			if (Double.isInfinite(result) || Double.isInfinite(a.getValue())) {
				continue; // sqrt(MAX)^2 puo' sconfinare in Infinity: limite di rappresentazione noto, non un difetto
			}
			assertTrue(approxEqualRelative(result, a.getValue(), 1e-9), "a=" + a + " sqrt(a)^2=" + result);
		}
	}

	@Test
	@DisplayName("Somma di due valori vicini a Double.MAX_VALUE sconfina in Infinity, correttamente rifiutata da contains()")
	void overflowToInfinityIsRejectedByContains() {
		Real huge = new Real(Double.MAX_VALUE);
		Real sum = R.add(huge, huge);
		assertTrue(Double.isInfinite(sum.getValue()));
		assertTrue(!R.contains(sum), "Infinity non deve essere contenuto nel campo reale approssimato");
	}
}
