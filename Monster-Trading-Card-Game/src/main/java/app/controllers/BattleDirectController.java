package app.controllers;

import app.daos.CardDao;
import app.daos.DeckDao;
import app.daos.UserDao;
import card.Card;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import user.User;

import java.sql.SQLException;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class BattleDirectController extends Controller{
    private CardDao cardDao;
    private DeckDao deckDao;

    public BattleDirectController(CardDao cardDao, DeckDao deckDao, UserDao userDao){
        super(userDao);
        setCardDao(cardDao);
        setDeckDao(deckDao);
    }

    public void changeCardOwner (Card card, int newOwnerID) throws SQLException {
        //update decks
        card.setUserID(newOwnerID);
        getDeckDao().updateUID(card);

        //update cards
        getCardDao().update(card);
    }

    public void updatePlayerStats (User user1, User user2) throws SQLException {
        getUserDao().update(user1);
        getUserDao().update(user2);
    }


}
