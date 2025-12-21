package net.gommagomma.smfn.math.linearalgebra.core.structures.specialized;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.FieldMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.FieldMatrixSpace;

public abstract class AbstractMatrixRing<K extends FieldElement<K, ?>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>>
implements MatrixRing<K, V, M>, FieldMatrixSpace<K, V, M> 
{
	protected final int n; // Dimensione n x n

	// Costruttore che forza le dimensioni quadrate
	protected AbstractMatrixRing(int n) {
		if (n <= 0) {
			throw new IllegalArgumentException("Matrix Ring dimensions (n x n) must be positive.");
		}
		this.n = n;
	}

	// --- Metodi di FieldMatrixSpace/RingMatrixModule ---


	@Override
	public int getMatrixRows() { return n; }

	@Override
	public int getMatrixColumns() { return n; }

	// --- Metodi di Ring<M> / NumericFactory<M> ---

	@Override
	public M additiveIdentity() {
		return getZero();
	}

	@Override
	public M multiplicativeIdentity() {
		return getIdentity();
	}

	// Implementazione del metodo factory zero() (che si appoggia al metodo getZero() dello Spazio)
	@Override
	public M zero() {
		return getZero();
	}

	// Implementazione del metodo factory one() (che si appoggia al metodo getIdentity() dello Spazio)
	@Override
	public M one() {
		return getIdentity();
	}

	// Metodi per creare uno scalare * Identità
	// (Devi delegare la creazione dello scalare al campo scalare)
	// Poiché AbstractMatrixRing non sa come costruire M, deleghiamo al concreto

	@Override
	public abstract M of(double value);

	@Override
	public abstract M of(long value);

	@Override
	public abstract M of(int value);

	// Metodi di fabbrica che la classe concreta deve implementare:

	// 1. Matrice Zero (Elemento neutro additivo)
	public abstract M getZero();

	// 2. Matrice Identità (Elemento neutro moltiplicativo)
	@Override // MatrixRing impls
	public abstract M getIdentity();
}
