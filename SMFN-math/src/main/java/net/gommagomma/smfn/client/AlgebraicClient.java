package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numeric.ZnElement;
import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.numeric.RationalFactory;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.numeric.SignedIntFactory;
import net.gommagomma.smfn.math.algebra.structures.ZnRing;

/**
 * Classe client di esempio per dimostrare l'uso delle diverse strutture 
 * algebriche implementate (Z, Q, Zn) e della capacità CreatableFromDouble.
 */
public class AlgebraicClient
{
    public static void main(String[] args) {
        
        System.out.println("--- Test 1: SignedInt (Z) ---");
        testSignedInt();
        
        System.out.println("\n--- Test 2: Rational (Q) - Field Operations ---");
        testRational();
        
        System.out.println("\n--- Test 3: Zn Ring (Z_5) - ModularInt ---");
        testZnRing();
    }

    private static void testSignedInt() {
        // Usa CreatableFromDouble per mostrare il truncamento (troncamento)
        SignedInt i1 = SignedIntFactory.getInstance().of(10.75); 
        SignedInt i2 = SignedIntFactory.getInstance().of(-3.14); 
        
        // Operazione base
        SignedInt sum = i1.add(i2); 
        
        System.out.println("i1 (valueOf 10.75): " + i1);
        System.out.println("i2 (valueOf -3.14): " + i2);
        System.out.println("Risultato somma (10 + (-3)): " + sum); 
    }

    private static void testRational() {
        // Uso del costruttore base
        Rational r1 = new Rational(1, 3);
        
        // Uso di CreatableFromDouble: converte 0.25 (frazione binaria esatta)
        Rational r2 = RationalFactory.getInstance().of(0.25); // 0.25 = 1/4

        // Uso di FieldElement (divisione)
        Rational div = r1.divide(r2);     // (1/3) / (1/4) = 4/3
        Rational inv = r2.inverse();      // inverso di 1/4 è 4/1

        System.out.println("r1: " + r1);
        System.out.println("r2 (valueOf 0.25): " + r2);
        System.out.println("Divisione (1/3 / 1/4): " + div); // Risultato atteso: 4/3
        System.out.println("Inverso di r2: " + inv);        // Risultato atteso: 4
    }

    private static void testZnRing() {
        // 1. Creazione della Struttura Z_5
        SignedInt modulus = new SignedInt(5);
        ZnRing Z5 = new ZnRing(modulus); // Utilizza il nome corretto
        
        System.out.println("Struttura creata: " + Z5.getName());
        
        // 2. Creazione degli Elementi ModularInt e riduzione modulo 5
        // a = [17] mod 5 = [2]
        ZnElement a = Z5.getElement(new SignedInt(17)); 
        
        // b = [4]
        ZnElement b = Z5.getElement(new SignedInt(4));

        // 3. Esecuzione delle Operazioni
        // Calcolo: [2] * [4] = [8] mod 5 = [3]
        ZnElement product = a.multiply(b);
        
        // Calcolo: [4] + [4] = [8] mod 5 = [3]
        ZnElement sum = b.add(b);

        System.out.println("a (17 mod 5): " + a);
        System.out.println("b (4 mod 5): " + b);
        System.out.println("Prodotto [2] * [4]: " + product); // Risultato atteso: 3
        System.out.println("Somma [4] + [4]: " + sum);       // Risultato atteso: 3
    }
}