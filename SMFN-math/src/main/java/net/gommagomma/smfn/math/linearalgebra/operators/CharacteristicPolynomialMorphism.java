package net.gommagomma.smfn.math.linearalgebra.operators;

import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialRing;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomials;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;

public class CharacteristicPolynomialMorphism<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> 
implements Morphism<SquareMatrix<K>, Polynomial<K>>
{
    @Override
    public Polynomial<K> evaluate(SquareMatrix<K> matrix) {
        @SuppressWarnings("unchecked")
        S scalarStructure = (S) matrix.getScalarStructure();
        PolynomialRing<K, S> polyRing = new PolynomialRing<K, S>(scalarStructure); 
        SquareMatrix<Polynomial<K>> lambdaMatrix = buildCharacteristicMatrix(matrix, polyRing, scalarStructure);

        var matrixPolyRing = new SquareMatrixRing<>(polyRing, matrix.getN());
        return matrixPolyRing.determinant(lambdaMatrix);
    }

    @SuppressWarnings("unchecked")
    private SquareMatrix<Polynomial<K>> buildCharacteristicMatrix(SquareMatrix<K> matrix, PolynomialRing<K, S> polyRing, S scalarStructure) {
        int n = matrix.getN();
        K one = scalarStructure.one();

        Polynomial<K>[] polyData = (Polynomial<K>[]) new Polynomial[n * n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                K val = matrix.get(i, j);
                if (i == j) {
                    // Diagonale: a_ii - 1x
                    polyData[i * n + j] = Polynomials.of(scalarStructure, val, scalarStructure.negate(one));
                } else {
                    // Extra: a_ij (grado 0)
                    polyData[i * n + j] = Polynomials.of(scalarStructure, val);
                }
            }
        }

        var polyMatrixStructure = new SquareMatrixRing<>(polyRing, n);
        return polyMatrixStructure.of(polyData);
    }
}