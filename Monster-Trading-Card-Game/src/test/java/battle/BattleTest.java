package battle;

import card.Card;
import card.Package;
import card.StackOfCards;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

public class BattleTest {
    public User prepUser(){
        StackOfCards stack = new StackOfCards();
        User user = new User(1, "user", 20, 100, 2, stack, null);
        Package p = new Package();
        user.buyPackage(p);
        user.getMyDeck().addCards(user.getMyStack().best4Cards());
        return user;
    }
    @Test
    @DisplayName("Test: newBattle.shouldBattleContinue(); True")
    public void testShouldBattleContinue_expectTrue(){
            User user1 = prepUser();
            User user2 = prepUser();

            Battle testBattle = new Battle(user1, user2);
            Boolean expectedResponse = true;

            Boolean realResponse = testBattle.shouldBattleContinue();

            assertEquals(expectedResponse, realResponse);
    }
    @Test
    @DisplayName("Test: Battle.shouldBattleContinue(); False because round==100")
    public void testShouldBattleContinue_expectFalseBecauseRound100(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);
        Boolean expectedResponse = false;
        testBattle.setRound(100);

        Boolean realResponse = testBattle.shouldBattleContinue();

        assertEquals(expectedResponse, realResponse);
    }
    @Test
    @DisplayName("Test: Battle.shouldBattleContinue(); False because deck empty")
    public void testShouldBattleContinue_expectFalseBecauseDeckEmpty(){
        User user1 = prepUser();
        User user2 = prepUser();
        user1.getMyDeck().emptyDeck();
        Battle testBattle = new Battle(user1, user2);
        Boolean expectedResponse = false;

        Boolean realResponse = testBattle.shouldBattleContinue();

        assertEquals(expectedResponse, realResponse);
    }
    @Test
    @DisplayName("Test: getRandomCardFromUser(); no Exception thrown")
    public void testGetRandomCardFromUser_expectNoException(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);

        assertDoesNotThrow(() -> {
            Card realResponse = testBattle.getRandomCardFromUser(user1);
        });
    }
    @Test
    @DisplayName("Test: getRandomCardFromUser(); IllegalArgumentException thrown")
    public void testGetRandomCardFromUserWithEmptyDeck_expectIllegalArgumentException(){
        User user1 = prepUser();
        User user2 = prepUser();
        user1.getMyDeck().emptyDeck();
        Battle testBattle = new Battle(user1, user2);

        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            testBattle.getRandomCardFromUser(user1);
        });
    }

    @Test
    @DisplayName("Test: addElementalDamage(); expect changed damage")
    public void testAddElementalDamageWithDifferentElement_expectChangedDamage(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);
        Card card1 = new Card("WaterMagicSpell", 20, "1", 1);
        Card card2 = new Card("FireWizardMonster", 20, "2", 1);
        int expectedDamageCard1 = 40;
        int expectedDamageCard2 = 10;
        int realDamageCard1;
        int realDamageCard2;

        testBattle.setCard1Damage(card1.getDamage());
        testBattle.setCard2Damage(card2.getDamage());
        testBattle.addElementalDamage(card1, card2);

        realDamageCard1 = testBattle.getCard1Damage();
        realDamageCard2 = testBattle.getCard2Damage();

        assertEquals(expectedDamageCard1, realDamageCard1);
        assertEquals(expectedDamageCard2, realDamageCard2);
    }
    @Test
    @DisplayName("Test: addElementalDamage(); expect unchanged damage")
    public void testAddElementalDamageWithSameElement_expectUnChangedDamage(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);
        Card card1 = new Card("WaterMagicSpell", 20, "1", 1);
        Card card2 = new Card("WaterWizardMonster", 10, "2", 1);
        int expectedDamageCard1 = 20;
        int expectedDamageCard2 = 10;
        int realDamageCard1;
        int realDamageCard2;

        testBattle.setCard1Damage(card1.getDamage());
        testBattle.setCard2Damage(card2.getDamage());
        testBattle.addElementalDamage(card1, card2);

        realDamageCard1 = testBattle.getCard1Damage();
        realDamageCard2 = testBattle.getCard2Damage();

        assertEquals(expectedDamageCard1, realDamageCard1);
        assertEquals(expectedDamageCard2, realDamageCard2);
    }
    @Test
    @DisplayName("Test: addMonsterSpecialities(); expect damage to 0")
    public void testAddMonsterSpecialities_expectDamageTo0(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);
        Card card1 = new Card("WaterGoblinMonster", 20, "1", 1);
        Card card2 = new Card("WaterDragonMonster", 10, "2", 1);
        int expectedDamageCard1 = 0;
        int realDamageCard1;

        testBattle.setCard1Damage(card1.getDamage());
        testBattle.setCard2Damage(card2.getDamage());
        testBattle.addMonsterSpecialities(card1, card2);
        realDamageCard1 = testBattle.getCard1Damage();

        assertEquals(expectedDamageCard1, realDamageCard1);
    }
    @Test
    @DisplayName("Test: whichCardStronger(); expect Card 1")
    public void testWhichCardStronger_expectCard1(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);
        Card card1 = new Card("WaterMagicSpell", 20, "1", 1);
        Card card2 = new Card("FireDragonMonster", 20, "2", 1);
        int expectedStrongerCard = 1;
        int realStrongerCard;

        realStrongerCard = testBattle.whichCardStronger(card1, card2);

        assertEquals(expectedStrongerCard, realStrongerCard);
    }
    @Test
    @DisplayName("Test: whichCardStronger(); expect Draw/3")
    public void testWhichCardStronger_expectDraw(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);
        Card card1 = new Card("WaterMagicSpell", 10, "1", 1);
        Card card2 = new Card("FireDragonMonster", 40, "2", 1);
        int expectedResult = 3;
        int realResult;

        realResult = testBattle.whichCardStronger(card1, card2);

        assertEquals(expectedResult, realResult);
    }
    @Test
    @DisplayName("Test: changeCardOwner(); expect no Exception")
    public void testChangeCardOwnerOfOwnedCard_expectNoException(){
        User user1 = prepUser();
        Card card1 = new Card("WaterMagicSpell", 10, "1", 1);
        user1.getMyDeck().addCard(card1);
        user1.getMyStack().addCard(card1);
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);

        assertDoesNotThrow(() -> {
            testBattle.changeCardOwner(card1, user2, user1);
        });
    }
    @Test
    @DisplayName("Test: changeCardOwner(); expect no Exception")
    public void testChangeCardOwnerOfOwnedCard_expectChangedDeckSize(){
        User user1 = prepUser();
        Card card1 = new Card("WaterMagicSpell", 10, "1", 1);
        user1.getMyDeck().addCard(card1);
        user1.getMyStack().addCard(card1);
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);
        int expectedDeckSizeU1 = user1.getMyDeck().getMyCards().size() - 1;
        int expectedDeckSizeU2 = user2.getMyDeck().getMyCards().size() + 1;
        int realDeckSizeU1;
        int realDeckSizeU2;

        testBattle.changeCardOwner(card1, user2, user1);
        realDeckSizeU1 = user1.getMyDeck().getMyCards().size();
        realDeckSizeU2 = user2.getMyDeck().getMyCards().size();

        assertEquals(expectedDeckSizeU1, realDeckSizeU1);
        assertEquals(expectedDeckSizeU2, realDeckSizeU2);

    }
    @Test
    @DisplayName("Test: changeCardOwner(); expect IllegalArgumentException")
    public void testChangeCardOwnerOfUnOwnedCard_expectIllegalArgumentException(){
        User user1 = prepUser();
        Card card1 = new Card("WaterMagicSpell", 10, "1", 1);
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);

        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            testBattle.changeCardOwner(card1, user2, user1);
        });
    }
    @Test
    @DisplayName("Test: writeToLogfile(); expect no Exception")
    public void testWriteToLogfile_expectNoException(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);

        assertDoesNotThrow(() -> {
            testBattle.writeToLogfile("\ntest: log entry");
        });
    }
    @Test
    @DisplayName("Test: writeToLogfile(); expect NullPointerException")
    public void testWriteToLogfile_expectNullPointerException(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);

        Exception thrownException = assertThrows(NullPointerException.class, () -> {
            testBattle.writeToLogfile(null);
        });
    }
    @Test
    @DisplayName("Test: updatePlayerStats(); expect updated stats")
    public void testUpdatePlayerStats_expectUpdatedStats(){
        User user1 = prepUser();
        user1.getMyDeck().emptyDeck();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);
        int expectedElo1 = user1.getScore() - 5;
        int expectedElo2 = user2.getScore() + 3;
        int realElo1;
        int realElo2;

        testBattle.updatePlayerStats();
        realElo1 = user1.getScore();
        realElo2 = user2.getScore();
        assertEquals(expectedElo1, realElo1);
        assertEquals(expectedElo2, realElo2);
    }
    @Test
    @DisplayName("Test: updatePlayerStats(); expect unchanged stats")
    public void testUpdatePlayerStats_expectUnchangedStats(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);
        int expectedElo1 = user1.getScore();
        int expectedElo2 = user2.getScore();
        int realElo1;
        int realElo2;

        testBattle.updatePlayerStats();
        realElo1 = user1.getScore();
        realElo2 = user2.getScore();
        assertEquals(expectedElo1, realElo1);
        assertEquals(expectedElo2, realElo2);
    }
    @Test
    @DisplayName("Test: doBattle(); expect no Exception")
    public void testDoBattle_expectNoException(){
        User user1 = prepUser();
        User user2 = prepUser();
        Battle testBattle = new Battle(user1, user2);

        assertDoesNotThrow(testBattle::doBattle);
    }
}
