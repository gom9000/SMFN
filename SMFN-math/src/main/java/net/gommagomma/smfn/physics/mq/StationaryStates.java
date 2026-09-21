package net.gommagomma.smfn.physics.mq;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Risultato di Hamiltonian.findStationaryStates(): i livelli energetici e
 * gli stati stazionari corrispondenti, in ordine accoppiato -- traduzione
 * fisica di EigenDecomposition (autovalori/autovettori), come
 * MeasurementOutcome lo e' per l'esito di una misura.
 */
public final class StationaryStates
{
	private final List<Real> energyLevels;
	private final List<QuantumState> states;

	StationaryStates(List<Real> energyLevels, List<QuantumState> states) {
		this.energyLevels = energyLevels;
		this.states = states;
	}

	public List<Real> getEnergyLevels() { return energyLevels; }
	public List<QuantumState> getStates() { return states; }
}
