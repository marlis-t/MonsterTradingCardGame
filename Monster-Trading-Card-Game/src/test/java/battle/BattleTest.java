package battle;

import app.controllers.BattleDirectController;
import app.models.Card;
import card.Enum.ELEMENT;
import card.Enum.TYPE;
import card.Package;
import card.StackOfCards;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import user.User;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class BattleTest {
    //add test mode w.o. db connection
    private User user1;
    private User user2;
    private BattleDirectController battleDirectController;
    private Battle testBattle;

    public User prepUser(){
        StackOfCards stack = new StackOfCards();
        User user = new User(1, "user", 20, 100, 2, "auth", stack);
        Package p = new Package();
        Package p2 = new Package();
        user.buyPackage(p);
        user.buyPackage(p2);
        user.assembleDeck();
        return user;
    }
    @BeforeEach
    public void setup(){
        user1 = prepUser();
        user2 = prepUser();
        battleDirectController = mock(BattleDirectController.class);
        testBattle = new Battle(user1, user2, battleDirectController);
    }

    @Test
    @DisplayName("Test: newBattle.shouldBattleContinue(); True")
    public void testShouldBattleContinue_expectTrue(){
            Boolean expectedResponse = true;

            Boolean realResponse = testBattle.shouldBattleContinue();

            assertEquals(expectedResponse, realResponse);
    }
    @Test
    @DisplayName("Test: Battle.shouldBattleContinue(); False because round==100")
    public void testShouldBattleContinue_expectFalseBecauseRound100(){
        Boolean expectedResponse = false;
        testBattle.setRound(100);

        Boolean realResponse = testBattle.shouldBattleContinue();

        assertEquals(expectedResponse, realResponse);
    }
    @Test
    @DisplayName("Test: Battle.shouldBattleContinue(); False because deck empty")
    public void testShouldBattleContinue_expectFalseBecauseDeckEmpty(){
        testBattle.getUser1().getMyDeck().emptyDeck();
        Boolean expectedResponse = false;

        Boolean realResponse = testBattle.shouldBattleContinue();

        assertEquals(expectedResponse, realResponse);
    }
    @Test
    @DisplayName("Test: getRandomCardFromUser(); no Exception thrown")
    public void testGetRandomCardFromUser_expectNoException(){
        assertDoesNotThrow(() -> {
            Card realResponse = testBattle.getRandomCardFromUser(user1);
        });
    }
    @Test
    @DisplayName("Test: getRandomCardFromUser() -> deck empty; IllegalArgumentException thrown")
    public void testGetRandomCardFromUserWithEmptyDeck_expectIllegalArgumentException(){
        testBattle.getUser1().getMyDeck().emptyDeck();

        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            testBattle.getRandomCardFromUser(user1);
        });
    }

    @Test
    @DisplayName("Test: getMutationCardFromUser(user, basisCard); ")
    public void testGetMutationCardFromUser_expectCertainCard(){
        user1.getMyStack().addCard(new Card("0", 1, "FireSpell", 99, false));
        user1.getMyStack().addCard(new Card("1", 1, "FireMonster", 99, false));
        user1.getMyStack().addCard(new Card("2", 1, "WaterSpell", 99, false));
        user1.getMyStack().addCard(new Card("3", 1, "FireSpell", 99, false));
        Card basisCard = user1.getMyDeck().getMyCards().get(0);
        int expectedDamage = 99*4 + basisCard.getDamage();
        TYPE expectedType = TYPE.SPELL;
        ELEMENT expectedElement = ELEMENT.FIRE;
        String expectedName = "FireMutationSpell";

        Card resultCard = testBattle.getMutationCardFromUser(user1, basisCard);

        assertEquals(expectedDamage, resultCard.getDamage());
        assertEquals(expectedType, resultCard.getType());
        assertEquals(expectedElement, resultCard.getElement());
        assertEquals(expectedName, resultCard.getName());
    }

    @Test
    @DisplayName("Test: addElementalDamage() -> different elements; expect changed damage")
    public void testAddElementalDamageWithDifferentElement_expectChangedDamage(){
        Card card1 = new Card("1", 1, "WaterSpell", 20, false);
        Card card2 = new Card("2", 1, "FireWizard", 20, false);
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
    @DisplayName("Test: addElementalDamage() -> same elements; expect unchanged damage")
    public void testAddElementalDamageWithSameElement_expectUnChangedDamage(){
        Card card1 = new Card("1", 1, "WaterSpell", 20, false);
        Card card2 = new Card("1", 1, "WaterWizard", 10, false);
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
    @DisplayName("Test: addMonsterSpecialities() -> have effect; expect damage to 0")
    public void testAddMonsterSpecialities_expectDamageTo0(){
        Card card1 = new Card("1", 1, "WaterGoblin", 20, false);
        Card card2 = new Card("2", 1, "WaterDragon", 10, false);
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
        Card card1 = new Card("1", 1, "WaterSpell", 20, false);
        Card card2 = new Card("2", 1, "FireDragon", 20, false);
        int expectedStrongerCard = 1;
        int realStrongerCard;

        realStrongerCard = testBattle.whichCardStronger(card1, card2);

        assertEquals(expectedStrongerCard, realStrongerCard);
    }
    @Test
    @DisplayName("Test: whichCardStronger(); expect Draw/3")
    public void testWhichCardStronger_expectDraw(){
        Card card1 = new Card("1", 1, "WaterSpell", 10, false);
        Card card2 = new Card("2", 1, "FireDragon", 40, false);
        int expectedResult = 3;
        int realResult;

        realResult = testBattle.whichCardStronger(card1, card2);

        assertEquals(expectedResult, realResult);
    }
    @Test
    @DisplayName("Test: changeCardOwner(); expect no Exception")
    public void testChangeCardOwnerOfOwnedCard_expectNoException(){
        Card card1 = user1.getMyDeck().getMyCards().get(0);

        assertDoesNotThrow(() -> {
            testBattle.changeCardOwner(card1, user2, user1);
        });
    }
    @Test
    @DisplayName("Test: changeCardOwner(); expect no Exception")
    public void testChangeCardOwnerOfOwnedCard_expectChangedDeckSize(){
        Card card1 = user1.getMyDeck().getMyCards().get(0);

        int expectedDeckSizeU1 = user1.getMyDeck().getMyCards().size() - 1;
        int expectedDeckSizeU2 = user2.getMyDeck().getMyCards().size() + 1;
        int realDeckSizeU1;
        int realDeckSizeU2;
        try {
            testBattle.changeCardOwner(card1, user2, user1);
        }catch(SQLException e){
            e.printStackTrace();
        }
        realDeckSizeU1 = user1.getMyDeck().getMyCards().size();
        realDeckSizeU2 = user2.getMyDeck().getMyCards().size();

        assertEquals(expectedDeckSizeU1, realDeckSizeU1);
        assertEquals(expectedDeckSizeU2, realDeckSizeU2);

    }
    @Test
    @DisplayName("Test: changeCardOwner() -> card not owned by user; expect IllegalArgumentException")
    public void testChangeCardOwnerOfUnOwnedCard_expectIllegalArgumentException(){
        Card card1 = new Card("1", 1, "NormalDragon", 99, false);

        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            testBattle.changeCardOwner(card1, user2, user1);
        });
    }
    @Test
    @DisplayName("Test: writeToLogfile() -> text; expect no Exception")
    public void testWriteTextToLogfile_expectNoException(){
        assertDoesNotThrow(() -> {
            testBattle.writeToLogfile("\ntest: log entry");
        });
    }
    @Test
    @DisplayName("Test: writeToLogfile() -> null; expect NullPointerException")
    public void testWriteNullToLogfile_expectNullPointerException(){

        Exception thrownException = assertThrows(NullPointerException.class, () -> {
            testBattle.writeToLogfile(null);
        });
    }
    @Test
    @DisplayName("Test: updatePlayerStats(); expect updated stats")
    public void testUpdatePlayerStats_expectUpdatedStats() throws SQLException {
        user1.getMyDeck().emptyDeck();
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
    public void testUpdatePlayerStats_expectUnchangedStats() throws SQLException {
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
        assertDoesNotThrow(testBattle::doBattle);
    }
}
