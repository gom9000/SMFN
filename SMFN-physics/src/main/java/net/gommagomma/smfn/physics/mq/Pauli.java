package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

/**
 * Le matrici di Pauli, come costanti pronte all'uso.
 */
public final class Pauli
{
	private Pauli() {}

	private static final ComplexField C = ComplexField.INSTANCE;

	public static final SquareMatrix<Complex> IDENTITY = SquareMatrixElementFactory.of(C,
		new Complex(1, 0), new Complex(0, 0),
		new Complex(0, 0), new Complex(1, 0)
	);

	public static final SquareMatrix<Complex> SIGMA_X = SquareMatrixElementFactory.of(C,
		new Complex(0, 0), new Complex(1, 0),
		new Complex(1, 0), new Complex(0, 0)
	);

	public static final SquareMatrix<Complex> SIGMA_Y = SquareMatrixElementFactory.of(C,
		new Complex(0, 0), new Complex(0, -1),
		new Complex(0, 1), new Complex(0, 0)
	);

	public static final SquareMatrix<Complex> SIGMA_Z = SquareMatrixElementFactory.of(C,
		new Complex(1, 0), new Complex(0, 0),
		new Complex(0, 0), new Complex(-1, 0)
	);
}
