package net.gommagomma.smfn.math.algebra.structures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.gommagomma.smfn.math.core.algebra.numeric.Rational;

/**
 * Test suite per verificare che la classe Rational soddisfi gli assiomi di un Campo (Field).
 */
class RationalFieldAxiomTest
{

    private final RationalField field = RationalField.INSTANCE;

    // Metodo helper per fornire un set diversificato di numeri razionali per i test parametrici
    static Stream<Rational> rationalProvider() {
        return Stream.of(
            new Rational(1, 2),
            new Rational(-3, 4),
            new Rational(5, 1),
            new Rational(0, 1),
            new Rational(10, 20), // Normalizzazione test
            new Rational(-1, -1)
        );
    }

    // --- Assiomi di base (Identità) ---

    @Test
    void additiveIdentityAxiom() {
        Rational a = new Rational(42, 1);
        Rational zero = field.additiveIdentity(); // Accedi allo zero tramite la struttura (o RationalField.ZERO)

        assertThat(a.add(zero)).isEqualTo(a);
        assertThat(zero.add(a)).isEqualTo(a);
    }

    @Test
    void multiplicativeIdentityAxiom() {
        Rational a = new Rational(42, 1);
        Rational one = field.multiplicativeIdentity(); // Accedi all'uno

        assertThat(a.multiply(one)).isEqualTo(a);
        assertThat(one.multiply(a)).isEqualTo(a);
    }
    
    // --- Assiomi di Inverso (Additivo e Moltiplicativo) ---

    @ParameterizedTest
    @MethodSource("rationalProvider")
    void additiveInverseAxiom(Rational a) {
        Rational inverseA = a.negate();
        Rational sum = a.add(inverseA);
        
        // a + (-a) deve essere uguale a zero
        assertThat(sum).isEqualTo(field.additiveIdentity());
    }

    @ParameterizedTest
    @MethodSource("rationalProvider")
    void multiplicativeInverseAxiom(Rational a) {
        if (a.isEqual(field.additiveIdentity())) {
            // L'assioma dell'inverso moltiplicativo non si applica allo zero.
            // Ci assicuriamo che inverse() lanci l'eccezione corretta per zero.
        	assertThatExceptionOfType(ArithmeticException.class).isThrownBy(a::inverse);
            return; 
        }
        
        Rational inverseA = a.inverse();
        Rational product = a.multiply(inverseA);
        
        // a * (a^-1) deve essere uguale a uno
        assertThat(product).isEqualTo(field.multiplicativeIdentity());
    }

    // --- Assiomi di Associatività e Commutatività (Ripresi e migliorati dai test precedenti) ---
    // Questi test erano già validi e ora sono organizzati qui.

    static Stream<Rational[]> associativityProvider() {
        return Stream.of(
            new Rational[] {new Rational(1, 2), new Rational(1, 3), new Rational(1, 4)},
            new Rational[] {new Rational(0, 1), new Rational(5, 1), new Rational(-2, 1)},
            new Rational[] {new Rational(10, 3), new Rational(-5, 2), new Rational(1, 1)}
        );
    }

    @ParameterizedTest
    @MethodSource("associativityProvider")
    void additionIsAssociative(Rational a, Rational b, Rational c) {
        // (a + b) + c == a + (b + c)
        Rational left = a.add(b).add(c);
        Rational right = a.add(b.add(c));
        assertThat(left).isEqualTo(right);
    }

    // Aggiungi test simili per Commutatività Additiva, Associatività Moltiplicativa, etc.
}