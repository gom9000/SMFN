package net.gommagomma.smfn.math.utils;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


class MathUtilsTest
{
    @ParameterizedTest(name = "GCD({0}, {1}) should be {2}")
    @CsvSource({
        "12, 18, 6",
        "48, 18, 6",
        "100, 10, 10",
        "17, 5, 1",
        "42, 0, 42",
        "0, 42, 42",
        "1, 1, 1"
    })
    void gcdShouldWorkForPositiveNumbersAndZero(long a, long b, long expectedGcd)
    {
        long result = MathUtils.greatestCommonDivisor(a, b);
        assertThat(result).isEqualTo(expectedGcd);
    }

    @ParameterizedTest(name = "GCD({0}, {1}) should be {2}")
    @CsvSource({
        "-12, 18, 6",
        "12, -18, 6",
        "-12, -18, 6",
        "-48, -18, 6",
        "0, -25, 25"
    })
    void gcdShouldWorkForNegativeNumbers(long a, long b, long expectedGcd)
    {
        long result = MathUtils.greatestCommonDivisor(a, b);
        assertThat(result).isEqualTo(expectedGcd);
    }

    @Test
    void gcdShouldWorkForLargeNumbers()
    {
        long a = 99999999999999L;
        long b = 11111111111111L;
        long expectedGcd = 11111111111111L; // a/b = 8
        long result = MathUtils.greatestCommonDivisor(a, b);
        assertThat(result).isEqualTo(expectedGcd);
    }

    @Test
    void gcdRejectsLongMinValue()
    {
        // Math.abs(Long.MIN_VALUE) va in overflow e resta negativo: senza guardia
        // esplicita, il ciclo dell'algoritmo di Euclide lavorerebbe su un valore
        // ancora negativo, producendo un "gcd" scorretto (potenzialmente negativo,
        // contro il contratto dichiarato dal metodo stesso).
        assertThrows(ArithmeticException.class, () -> MathUtils.greatestCommonDivisor(Long.MIN_VALUE, 5));
        assertThrows(ArithmeticException.class, () -> MathUtils.greatestCommonDivisor(5, Long.MIN_VALUE));
    }
}
