package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Invarianti di CentralDifferenceDifferentiator sul catalogo di DifferentiationTestValues.
 * L'invariante e' la vicinanza tra la derivata numerica e quella analitica nota, entro l'errore
 * di troncamento atteso per una differenza centrale O(h^2).
 */
@DisplayName("CentralDifferenceDifferentiator: invarianti di accuratezza sul catalogo di funzioni con derivata analitica nota")
class DifferentiationInvariantsTest
{
	private static final RealField R = RealField.INSTANCE;

	@Test
	@DisplayName("La derivata numerica coincide con quella analitica, entro tolleranza, su tutto il catalogo")
	void numericDerivativeMatchesAnalyticDerivative() {
		CentralDifferenceDifferentiator<Real> differentiator = new CentralDifferenceDifferentiator<>(R, new Real(1e-5));

		for (DifferentiationTestValues.FunctionCase testCase : DifferentiationTestValues.cases()) {
			Mapping<Real, Real> derivative = differentiator.apply(testCase.f);

			for (double x : testCase.evaluationPoints) {
				double expected = testCase.analyticDerivative.applyAsDouble(x);
				double actual = derivative.apply(new Real(x)).getValue();
				assertTrue(Math.abs(expected - actual) < 1e-6,
					"caso: " + testCase.name + " x=" + x + ": atteso=" + expected + " ottenuto=" + actual);
			}
		}
	}

	@Test
	@DisplayName("Errore O(h^2): dimezzare il passo h riduce l'errore di un fattore ~4, per una funzione con curvatura non nulla")
	void centralDifferenceHasSecondOrderError() {
		// f(x) = x^3 - 2x, la cui derivata seconda non e' nulla in x=2 (termine di errore O(h^2*f''')).
		Mapping<Real, Real> f = x -> R.subtract(R.multiply(R.multiply(x, x), x), R.multiply(new Real(2.0), x));
		double x0 = 2.0;
		double analyticDerivative = 3.0 * x0 * x0 - 2.0;

		CentralDifferenceDifferentiator<Real> coarse = new CentralDifferenceDifferentiator<>(R, new Real(1e-2));
		CentralDifferenceDifferentiator<Real> fine = new CentralDifferenceDifferentiator<>(R, new Real(0.5e-2));

		double errorCoarse = Math.abs(analyticDerivative - coarse.apply(f).apply(new Real(x0)).getValue());
		double errorFine = Math.abs(analyticDerivative - fine.apply(f).apply(new Real(x0)).getValue());

		assertTrue(errorFine > 0.0, "l'errore a passo fine non dovrebbe essere esattamente zero in aritmetica in virgola mobile");
		double ratio = errorCoarse / errorFine;
		assertTrue(ratio > 2.5, "rapporto di riduzione dell'errore troppo basso per un errore di troncamento O(h^2): " + ratio
			+ " (atteso ~4, errore grezzo=" + errorCoarse + " errore fine=" + errorFine + ")");
	}

	@Test
	@DisplayName("Il costruttore rifiuta un passo h nullo o zero")
	void constructorRejectsZeroOrNullStep() {
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
			() -> new CentralDifferenceDifferentiator<>(R, new Real(0.0)));
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
			() -> new CentralDifferenceDifferentiator<Real>(R, null));
	}
}
