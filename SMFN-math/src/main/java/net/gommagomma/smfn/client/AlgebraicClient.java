package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.numerics.ZnElement;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.algebra.structures.ZnRing;

/**
 * Classe client di esempio per dimostrare l'uso delle diverse strutture 
 * algebriche implementate (Z, Q, Zn) e della capacità CreatableFromDouble.
 */
public class AlgebraicClient
{
	static IntegerRing Z = IntegerRing.INSTANCE;
	static RationalField Q = RationalField.INSTANCE;
	static ZnRing Z5 = ZnRing.of(Z.of(5));

    public static void main(String[] args) {
        
        System.out.println("--- Test 1: SignedInt (Z) ---");
        testSignedInt();
        
        System.out.println("\n--- Test 2: Rational (Q) - Field Operations ---");
        testRational();
        
        System.out.println("\n--- Test 3: Zn Ring (Z_5) - ModularInt ---");
        testZnRing();
    }

    private static void testSignedInt() {
        SignedInt i1 = Z.of(10); 
        SignedInt i2 = Z.of(-3); 
        
        // Operazione base
        SignedInt sum = Z.add(i1, i2); 
        
        System.out.println("i1 (valueOf 10): " + i1);
        System.out.println("i2 (valueOf -3): " + i2);
        System.out.println("Risultato somma (10 + (-3)): " + sum); 
    }

    private static void testRational() {
        Rational r1 = Q.of(1, 3);
        Rational r2 =  Q.of(0.25); // 0.25 = 1/4

        // Uso di FieldElement (divisione)
        Rational div = Q.divide(r1, r2);     // (1/3) / (1/4) = 4/3
        Rational inv = Q.inverse(r2);      // inverso di 1/4 è 4/1

        System.out.println("r1: " + r1);
        System.out.println("r2 (valueOf 0.25): " + r2);
        System.out.println("Divisione (1/3 / 1/4): " + div); // Risultato atteso: 4/3
        System.out.println("Inverso di r2: " + inv);        // Risultato atteso: 4

        System.out.println("Q(0.1) = " + Q.of(0.1));
        System.out.println("Q(0.9) = " + Q.of(0.9));
        System.out.println("Q(0.75) = " + Q.of(0.75));
        System.out.println("Q(2.5) = " + Q.of(2.5));
        System.out.println("Q(1.5) = " + Q.of(1.5));
    }

    private static void testZnRing() {
        System.out.println("Struttura creata: " + Z5.getName());
        ZnElement a = Z5.getElement(new SignedInt(17)); // a = [17] mod 5 = [2]
        ZnElement b = Z5.getElement(new SignedInt(4)); // b = [4]

        // Esecuzione delle Operazioni
        ZnElement product = Z5.multiply(a, b); // Calcolo: [2] * [4] = [8] mod 5 = [3]
        ZnElement sum = Z5.add(b, b); // Calcolo: [4] + [4] = [8] mod 5 = [3]

        System.out.println("a (17 mod 5): " + a);
        System.out.println("b (4 mod 5): " + b);
        System.out.println("Prodotto [2] * [4]: " + product); // Risultato atteso: 3
        System.out.println("Somma [4] + [4]: " + sum);       // Risultato atteso: 3
    }
}