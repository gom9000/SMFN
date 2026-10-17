package net.gommagomma.smfn.graphics.plotting;

/**
 * Il tratto di curva di livello che attraversa una cella rettangolare, dai valori che la funzione
 * assume sui suoi quattro vertici (algoritmo "marching squares"). Condiviso fra i plotter di
 * curve di livello, 2D e 3D, che differiscono solo in come proiettano il tratto.
 * <p>
 * Un vertice e' "sopra" il livello se il suo valore e' maggiore o uguale al livello: la
 * classificazione e' la stessa per le due celle che condividono un lato, quindi le curve non
 * hanno interruzioni.
 */
final class MarchingSquares
{
    private MarchingSquares() {}

    /** Riceve un segmento, in coordinate (u, v) della cella, ciascuna in [0, 1]. */
    interface SegmentSink
    {
        void accept(double u1, double v1, double u2, double v2);
    }

    /**
     * @param v00 valore sul vertice (u=0, v=0)
     * @param v10 valore sul vertice (u=1, v=0)
     * @param v11 valore sul vertice (u=1, v=1)
     * @param v01 valore sul vertice (u=0, v=1)
     * @param level il livello
     * @param sink riceve zero, uno o (nel caso di sella) due segmenti. Se un vertice non e' finito
     *        la cella non ha una curva e non viene emesso nulla.
     */
    static void segments(double v00, double v10, double v11, double v01, double level, SegmentSink sink)
    {
        if (!Double.isFinite(v00) || !Double.isFinite(v10) || !Double.isFinite(v11) || !Double.isFinite(v01)) return;

        boolean a00 = v00 >= level;
        boolean a10 = v10 >= level;
        boolean a11 = v11 >= level;
        boolean a01 = v01 >= level;

        // Punti di attraversamento sui quattro lati: basso, destra, alto, sinistra (NaN se assente)
        boolean bottom = a00 != a10;
        boolean right = a10 != a11;
        boolean top = a01 != a11;
        boolean left = a00 != a01;

        double bottomU = bottom ? fraction(v00, v10, level) : 0.0;   // (bottomU, 0)
        double rightV = right ? fraction(v10, v11, level) : 0.0;     // (1, rightV)
        double topU = top ? fraction(v01, v11, level) : 0.0;         // (topU, 1)
        double leftV = left ? fraction(v00, v01, level) : 0.0;       // (0, leftV)

        int crossings = (bottom ? 1 : 0) + (right ? 1 : 0) + (top ? 1 : 0) + (left ? 1 : 0);
        if (crossings == 0) return;

        if (crossings == 2)
        {
            // I due lati attraversati sono gli estremi del segmento
            double[] p = new double[4];
            int n = 0;
            if (bottom) { p[n++] = bottomU; p[n++] = 0.0; }
            if (right)  { p[n++] = 1.0;     p[n++] = rightV; }
            if (top)    { p[n++] = topU;    p[n++] = 1.0; }
            if (left)   { p[n++] = 0.0;     p[n++] = leftV; }
            sink.accept(p[0], p[1], p[2], p[3]);
            return;
        }

        // Sella: i vertici sopra sono opposti. Il valore medio dei quattro dice se la regione
        // "sopra" si estende al centro (e unisce i suoi due vertici) o no.
        boolean centerAbove = (v00 + v10 + v11 + v01) / 4.0 >= level;
        if (centerAbove == a00)
        {
            // Il centro sta con i vertici 00 e 11 (o 10 e 01, se sono loro "sopra" nel verso opposto):
            // le curve girano attorno ai vertici 10 e 01
            sink.accept(bottomU, 0.0, 1.0, rightV);
            sink.accept(0.0, leftV, topU, 1.0);
        }
        else
        {
            // Le curve girano attorno ai vertici 00 e 11
            sink.accept(bottomU, 0.0, 0.0, leftV);
            sink.accept(1.0, rightV, topU, 1.0);
        }
    }

    /** Posizione in [0, 1] fra i valori a e b (che stanno da parti opposte del livello) in cui vale il livello. */
    private static double fraction(double a, double b, double level)
    {
        return (level - a) / (b - a);
    }
}
