package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Rappresenta l'esito di una misurazione quantistica effettuata su un sistema in un determinatoQuantumState.
 * 
 * In accordo con i postulati della meccanica quantistica (assioma della misurazione di Von Neumann/Dirac)
 * il valore misurato e' un valore proprio reale (autovalore E_n) associato all'operatore Observable.
 * A seguito della misurazione, lo stato del sistema subisce un collasso istantaneo proiettandosi
 * sull'autostato proprio |psi_n> corrispondente all'autovalore ottenuto.
 */
public final class MeasurementOutcome
{
	private final Real value;
	private final QuantumState collapsedState;

	public MeasurementOutcome(Real value, QuantumState collapsedState) {
		this.value = value;
		this.collapsedState = collapsedState;
	}

	public Real getValue() { return value; }
	public QuantumState getCollapsedState() { return collapsedState; }
}
