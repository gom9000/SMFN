package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces;

import net.gommagomma.smfn.math.algebra.core.AlgebraicStructure;

public interface DimensionalStructure<S extends AlgebraicStructure<?>>
{
    /**
     * Restituisce un'istanza di questa Struttura Algebrica con le dimensioni specificate.
     * Necessario per le operazioni che cambiano la dimensione, come la trasposta.
     * @param rows Il numero di righe richiesto.
     * @param cols Il numero di colonne richiesto.
     * @return Una nuova istanza di S (la Struttura) con le dimensioni (rows x cols).
     */
    S getSpaceOfDimensions(int rows, int cols);
}