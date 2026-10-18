package net.gommagomma.smfn.physics.mq.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.physics.mq.Hamiltonian;
import net.gommagomma.smfn.physics.mq.StationaryStates;

@DisplayName("HarmonicOscillator: modello canonico dell'oscillatore armonico quantistico")
class HarmonicOscillatorTest
{
	@Test
	@DisplayName("Primi livelli energetici coincidono con E_n = omega*(n+1/2), entro l'errore di discretizzazione atteso")
	void firstLevelsMatchAnalyticFormula() {
		int N = 80;
		double L = 8.0;
		double omega = 1.0;
		Hamiltonian<Real> oscillator = HarmonicOscillator.hamiltonian(N, L, omega);
		StoppingParameters params = new StoppingParameters(new Real(1e-10), 400000);

		StationaryStates result = oscillator.findStationaryStates(params);
		List<Double> levels = new ArrayList<>();
		for (Real r : result.getEnergyLevels()) levels.add(r.getValue());
		Collections.sort(levels);

		for (int n = 0; n <= 2; n++) {
			double expected = omega * (n + 0.5);
			double numeric = levels.get(n);
			assertEquals(expected, numeric, 0.02); // discretizzazione, non arrotondamento: tolleranza larga apposta
		}
	}

	@Test
	@DisplayName("I livelli restano equispaziati di circa omega, per i primi n")
	void firstLevelsAreEquallySpaced() {
		int N = 80;
		double L = 8.0;
		double omega = 1.0;
		Hamiltonian<Real> oscillator = HarmonicOscillator.hamiltonian(N, L, omega);
		StoppingParameters params = new StoppingParameters(new Real(1e-10), 400000);

		StationaryStates result = oscillator.findStationaryStates(params);
		List<Double> levels = new ArrayList<>();
		for (Real r : result.getEnergyLevels()) levels.add(r.getValue());
		Collections.sort(levels);

		double spacing1 = levels.get(1) - levels.get(0);
		double spacing2 = levels.get(2) - levels.get(1);
		assertEquals(omega, spacing1, 0.02);
		assertEquals(omega, spacing2, 0.02);
	}
}
