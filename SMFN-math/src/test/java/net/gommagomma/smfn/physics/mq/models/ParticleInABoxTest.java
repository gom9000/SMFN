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

@DisplayName("ParticleInABox: modello canonico della buca di potenziale infinita")
class ParticleInABoxTest
{
	@Test
	@DisplayName("Primi livelli energetici coincidono con E_n = n^2*pi^2/(2*L^2), entro l'errore di discretizzazione atteso")
	void firstLevelsMatchAnalyticFormula() {
		int N = 200;
		double L = 1.0;
		Hamiltonian<Real> box = ParticleInABox.hamiltonian(N, L);
		StoppingParameters params = new StoppingParameters(new Real(1e-10), 400000);

		StationaryStates result = box.findStationaryStates(params);
		List<Double> levels = new ArrayList<>();
		for (Real r : result.getEnergyLevels()) levels.add(r.getValue());
		Collections.sort(levels);

		for (int n = 1; n <= 3; n++) {
			double expected = n * n * Math.PI * Math.PI / (2 * L * L);
			double numeric = levels.get(n - 1);
			assertEquals(expected, numeric, 0.02); // discretizzazione, non arrotondamento: tolleranza larga apposta
		}
	}
}
