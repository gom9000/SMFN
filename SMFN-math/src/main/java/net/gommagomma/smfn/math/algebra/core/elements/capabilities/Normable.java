package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;

// Interfaccia base per qualsiasi elemento che possiede una norma.
// N è il tipo del campo su cui la norma è definita (es. Real, Rational).
// E è il tipo dell'elemento stesso (es. Complex, RealVector).
public interface Normable<N extends FieldElement<N>, E extends Normable<N, E>> 
extends AlgebraicElement<E> 
{
    N norm();
}