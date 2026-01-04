package net.gommagomma.smfn.math.algebra.core.elements.factories;

/**
 * Factory per la creazione di elementi composti all'interno di una struttura.
 * @param E Il tipo dell'elemento (es. Polynomial<K>)
 * @param D Il tipo del dato sorgente (es. List<K>)
 */
public interface CompositeElementFactory<E, D>
{
    /**
     * Crea un nuovo elemento iniettando la struttura corrente come contesto.
     */
    E of(D data);
}
