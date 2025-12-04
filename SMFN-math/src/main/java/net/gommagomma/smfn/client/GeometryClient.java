package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;
import net.gommagomma.smfn.math.linearalgebra.real.RealVectorSpace; // La tua classe di struttura

public class GeometryClient
{
	public static void main(String[] s) {
		GeometryClient client = new GeometryClient();
		client.runExamples();
	}
    // Otteniamo l'istanza dello spazio vettoriale reale (probabilmente un singleton)
    private final RealVectorSpace R2 = RealVectorSpace.getInstance(); // Assumendo esista un getInstance()

    public void runExamples() {
        // Creazione di punti (che contengono RealVector)
        Point p1 = new Point(new Real(1.0), new Real(0.0));
        Point p2 = new Point(new Real(0.0), new Real(1.0));

        // Creazione di vettori direttamente (se necessario)
        RealVector v1 = new RealVector(new Real(1.0), new Real(0.0));
        RealVector v2 = new RealVector(new Real(0.0), new Real(1.0));

        // --- Calcolo della Distanza ---
        // Possiamo usare il metodo distanceTo del punto (che è più intuitivo)
        Real distP1P2 = p1.distanceTo(p2);
        System.out.println("Distanza tra P1 e P2: " + distP1P2);

        // Oppure possiamo usare la logica dello Spazio Metrico (meno intuitivo per i punti, ma corretto)
        // L'interfaccia MetricSpace richiede elementi T, i tuoi Point non lo sono direttamente
        // ma i tuoi RealVector lo sono.
        Real distViaSpace = R2.distance(v1, v2); 
        System.out.println("Distanza tra V1 e V2 tramite Space: " + distViaSpace);


        // --- Calcolo del Prodotto Interno (per gli angoli) ---
        // Il prodotto interno è un'operazione definita sullo spazio InnerProductSpace.
        // Dobbiamo usare i RealVector qui, non i Point.
        
        Real dotProduct = R2.innerProduct(v1, v2);
        System.out.println("Prodotto interno V1.V2: " + dotProduct); 
        
        // Per calcolare l'angolo esatto, servirebbe una funzione trigonometrica che usi il prodotto interno:
        // cos(theta) = (v1 . v2) / (||v1|| * ||v2||)
        // double angleInRadians = Math.acos(dotProduct.getValue() / (v1.norm().getValue() * v2.norm().getValue()));
    }
}
