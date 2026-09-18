package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;

/**
 * Un Osservabile e', per definizione fisica, un operatore hermitiano:
 * M = M^dagger (autovalori reali). Non e' una gerarchia di tipo a parte --
 * e' un vincolo verificato al momento della costruzione, sfruttando
 * isHermitian() gia' disponibile su SquareMatrix. Un SquareMatrix<Complex>
 * qualunque puo' rappresentare un operatore lineare (lo e' gia', via
 * LinearOperator); un Observable garantisce in piu' che sia fisicamente
 * valido come quantita' misurabile.
 */
public final class Observable
{
	private final SquareMatrix<Complex> operator;

	public Observable(SquareMatrix<Complex> operator) {
		if (!operator.isHermitian()) {
			throw new IllegalArgumentException("Un Observable deve essere rappresentato da un operatore hermitiano (M = M^dagger).");
		}
		this.operator = operator;
	}

	public SquareMatrix<Complex> asOperator() {
		return operator;
	}
}
