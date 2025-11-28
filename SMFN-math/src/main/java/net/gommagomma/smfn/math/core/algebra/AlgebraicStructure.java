/*
 * AlgebraicStructure.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.core.algebra;


/**
 * Represents an abstract algebraic structure (e.g., a Group, a Ring, a Field, a Vector Space).
 * It defines the general properties of the set in which the elements "live".
 *
 * @param <E> The type of elements that belong to this structure, extending {@link AlgebraicElement}.
 * @author gommagomma.net
 */
public interface AlgebraicStructure<E extends AlgebraicElement<E>>
{
	/**
     * Returns the formal name of the algebraic structure (e.g., "Rational Field (Q)").
     * @return The name of the structure.
     */
    String getName();

    /**
     * Verifica se l'elemento specificato appartiene a questa struttura algebrica (insieme).
     * @param e elemento da controllare.
     * @return true se l'elemento è contenuto nella struttura, false altrimenti.
     */
    boolean contains(E e);
}
