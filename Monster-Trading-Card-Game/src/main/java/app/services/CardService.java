package app.services;

import app.models.CardModel;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.ArrayList;

public class CardService {
    @Setter(AccessLevel.PRIVATE)
    private ArrayList<CardModel> cardData;

    public CardService(){
        setCardData(new ArrayList<CardModel>());
        cardData.add(new CardModel(1, 1, "WaterSpell", 10, true));
        cardData.add(new CardModel(2, 1, "FireGoblinMonster", 25, false));
        cardData.add(new CardModel(3, 2, "NormalSpell", 15, true));
    }
    public CardModel getCardByID(int id){
        CardModel foundCardModel = cardData.stream()
                .filter(cardModel -> id == cardModel.getCardID())
                .findAny()
                .orElse(null);

        return foundCardModel;
    }
    public ArrayList<CardModel> getCardsFromUserID(int Uid){
        ArrayList<CardModel> foundCards = new ArrayList<CardModel>();
        for(CardModel card : cardData){
            if(card.getUserID() == Uid){
                foundCards.add(card);
            }
        }
        return foundCards;
    }
    public ArrayList<CardModel> getAllOfferedCards(int UId){
        ArrayList<CardModel> foundCards = new ArrayList<CardModel>();
        for(CardModel card: cardData){
            if(card.isPaused() && card.getUserID() != UId){
                foundCards.add(card);
            }
        }
        return foundCards;
    }
    public void addCard(CardModel card){
        cardData.add(card);
    }
    public void deleteCard(int id){
        cardData.removeIf(cardModel -> id == cardModel.getCardID());
    }
    public void updateCard(int id, CardModel card){
        cardData.removeIf(cardModel -> id == cardModel.getCardID());
        cardData.add(card);
    }
}
