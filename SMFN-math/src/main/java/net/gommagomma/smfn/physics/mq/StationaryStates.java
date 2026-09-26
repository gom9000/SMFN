package net.gommagomma.smfn.physics.mq;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Contenitore che racchiude lo spettro discreto e gli autostati dell'energia calcolati per un operatore hamiltoniano.
 * 
 * Un generico stato stazionario |psi_n> soddisfa l'equazione agli autovalori indipendente
 * dal tempo per l'Hamiltoniano H: H |psi_n> = E_n |psi_n> dove E_n rappresenta il valore proprio
 * (livello di energia)e |psi_n> rappresenta lo stato proprio corrispondente.
 * L'ordine degli elementi nelle due liste e' strettamente posizionale: il k-esimo valore energetico
 * corrisponde al k-esimo autostato.
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
