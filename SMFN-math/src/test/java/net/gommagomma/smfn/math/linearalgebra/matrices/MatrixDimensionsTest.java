package net.gommagomma.smfn.math.linearalgebra.matrices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixSemiring;

/**
 * Le matrici vuote non sono ammesse: l'anello delle matrici 0x0 ha un solo elemento (zero e uno
 * coincidono), e il determinante "per convenzione 1" non e' coerente con il resto della libreria.
 * Si rifiuta subito, con un messaggio chiaro, invece di restituire risultati sbagliati.
 */
@DisplayName("Dimensioni delle matrici: ordine, righe e colonne devono essere almeno 1")
class MatrixDimensionsTest
{
	private final RealField R = RealField.INSTANCE;

	@Test
	@DisplayName("Matrici rettangolari con zero righe o colonne, o negative, sono rifiutate")
	void rectangularRejectsEmpty() {
		assertThrows(IllegalArgumentException.class, () -> new MatrixSemimodule<>(R, 0, 3));
		assertThrows(IllegalArgumentException.class, () -> new MatrixSemimodule<>(R, 3, 0));
		assertThrows(IllegalArgumentException.class, () -> new MatrixSemimodule<>(R, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> new MatrixModule<>(R, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new MatrixSpace<>(R, 2, -2));
	}

	@Test
	@DisplayName("Matrici quadrate di ordine 0 o negativo sono rifiutate, in tutte e tre le strutture")
	void squareRejectsEmpty() {
		assertThrows(IllegalArgumentException.class, () -> new SquareMatrixSemiring<>(R, 0));
		assertThrows(IllegalArgumentException.class, () -> new SquareMatrixRing<>(R, 0));
		assertThrows(IllegalArgumentException.class, () -> new SquareMatrixAlgebra<>(R, 0));
		assertThrows(IllegalArgumentException.class, () -> new SquareMatrixRing<>(R, -3));
	}

	@Test
	@DisplayName("Il messaggio dice cosa non va")
	void messageSaysWhat() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new SquareMatrixRing<>(R, 0));
		assertTrue(e.getMessage().contains("almeno 1"));
		IllegalArgumentException f = assertThrows(IllegalArgumentException.class, () -> new MatrixSemimodule<>(R, 0, 2));
		assertTrue(f.getMessage().contains("0x2"));
	}

	@Test
	@DisplayName("Il caso minimo, ordine 1, funziona: determinante = elemento, invertibile se non nullo")
	void orderOneWorks() {
		SquareMatrixAlgebra<Real, RealField> algebra = new SquareMatrixAlgebra<>(R, 1);
		SquareMatrix<Real> m = algebra.of(new Real[] { new Real(5.0) });
		assertEquals(5.0, algebra.determinant(m).getValue(), 0.0);
		assertTrue(algebra.isInvertible(m));
		assertTrue(!algebra.isInvertible(algebra.zero()));
	}
}
