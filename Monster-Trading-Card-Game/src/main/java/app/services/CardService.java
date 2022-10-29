package app.services;

import app.models.CardModel;
import card.Enum.ELEMENT;
import card.Enum.TYPE;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.ArrayList;

public class CardService {
    @Setter(AccessLevel.PRIVATE)
    private ArrayList<CardModel> cardData;

    public CardService(){
        setCardData(new ArrayList<CardModel>());
        cardData.add(new CardModel(1, 1, "WaterSpell", 10, ELEMENT.WATER, TYPE.SPELL));
        cardData.add(new CardModel(2, 1, "FireGoblinMonster", 25, ELEMENT.FIRE, TYPE.MONSTER));
        cardData.add(new CardModel(3, 2, "NormalSpell", 15, ELEMENT.NORMAL, TYPE.SPELL));
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
    public void addCard(CardModel card){
        cardData.add(card);
    }
    public void deleteCard(int id){
        cardData.removeIf(cardModel -> id == cardModel.getCardID());
    }
}
