package card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardGeneratorTest {
    @Test
    @DisplayName("Test: chooseElement(null); NullPointerException thrown")
    public void testChooseElementWithRandomizerNull_expectNullPointerException(){
        CardGenerator generator = new CardGenerator();

        Exception thrownException = assertThrows(NullPointerException.class, () -> {
            String testString = generator.chooseElement(null);
        });
    }
    @Test
    @DisplayName("Test: chooseElement(randomizer); no Exception thrown")
    public void testChooseElementWithValidRandomizer_expectNoException(){
        CardGenerator generator = new CardGenerator();
        Random randomizer = new Random();

        assertDoesNotThrow(() -> {
            String testString = generator.chooseElement(randomizer);
            System.out.println(testString);
        });
    }
    @Test
    @DisplayName("Test: chooseDamage(null); NullPointerException thrown")
    public void testChooseDamageWithRandomizerNull_expectNullPointerException(){
        CardGenerator generator = new CardGenerator();

        Exception thrownException = assertThrows(NullPointerException.class, () -> {
            int testInt = generator.chooseDamage(null);
        });
    }
    @Test
    @DisplayName("Test: chooseElement(randomizer); no Exception thrown")
    public void testChooseDamageWithValidRandomizer_expectNoException(){
        CardGenerator generator = new CardGenerator();
        Random randomizer = new Random();

        assertDoesNotThrow(() -> {
            int testInt = generator.chooseDamage(randomizer);
        });
    }
}
