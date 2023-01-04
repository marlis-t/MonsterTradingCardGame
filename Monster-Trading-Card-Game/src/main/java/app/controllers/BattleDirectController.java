package app.controllers;

import app.daos.CardDao;
import app.daos.DeckDao;
import app.daos.UserDao;
import card.Card;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import user.User;
import app.models.UserModel;

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
        UserModel user1Model = getUserDao().read(user1.getUsername());
        UserModel user2Model = getUserDao().read(user2.getUsername());
        user1Model.setScore(user1.getScore());
        user2Model.setScore(user2.getScore());
        user1Model.setGamesPlayed(user1.getGamesPlayed());
        user2Model.setGamesPlayed(user2.getGamesPlayed());

        getUserDao().update(user1Model);
        getUserDao().update(user2Model);
    }


}
