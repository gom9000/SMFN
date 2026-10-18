package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Rappresenta la probabilita' associata a un singolo esito di misurazione per un'osservabile quantistica.
 * 
 * In accordo con la regola di Born, per uno stato quantistico |psi> e un autostato |a_n>  dell'osservabile
 * corrispondente all'autovalore a_n: P(a_n) = |<a_n | psi>|^2 dove getValue() restituisce il valore misurabile a_n
 * (autovalore reale) e getProbability() restituisce il valore di probabilita' P(a_n) \in [0, 1].
 */
public final class MeasurementProbability
{
	private final Real value;
	private final Real probability;

	public MeasurementProbability(Real value, Real probability) {
		this.value = value;
		this.probability = probability;
	}

	public Real getValue() { return value; }
	public Real getProbability() { return probability; }

	@Override
	public String toString() { return "P(" + value + ") = " + probability; }
}
