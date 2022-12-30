package app.controllers;

import app.daos.BattleDao;
import app.daos.CardDao;
import app.daos.DeckDao;
import app.daos.UserDao;
import app.http.HttpStatus;
import app.models.BattleModel;
import app.server.Response;
import battle.Battle;
import card.Card;
import card.Deck;
import card.StackOfCards;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import user.User;

import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class BattleController extends Controller{
    private CardDao cardDao;
    private DeckDao deckDao;
    private BattleDao battleDao;
    private BattleDirectController battleDirectController;

    public BattleController(UserDao userDao, CardDao cardDao, DeckDao deckDao, BattleDao battleDao){
        super(userDao);
        setCardDao(cardDao);
        setDeckDao(deckDao);
        setBattleDao(battleDao);
        setBattleDirectController(new BattleDirectController(cardDao, deckDao, userDao));
    }

    public Response haveBattle(String username){
        try{
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
            }
            //check if a battlereq. is in db
            ArrayList<BattleModel> battles = getBattleDao().readAll();
            if(!battles.isEmpty()){
                //check if request was made by oneself
                BattleModel battleReq = getBattleDao().read(username);
                if(battleReq != null){
                    return  sendResponse("null", "Request was made already", HttpStatus.BAD_REQUEST);
                }
                //check for unaccepted battlereq.
                for(BattleModel tempReq : battles){
                    if(tempReq.getAcceptor() == null){
                        battleReq = tempReq;
                        break;
                    }
                }
                //if no unaccepted reqs., make new one
                if(battleReq == null){
                    return makeBattleRequest(username);
                }else{
                    //req. made by other player, update req. as accepted
                    battleReq.setAcceptor(username);
                    getBattleDao().update(battleReq);
                    return startBattle(2, battleReq);
                }
            }else{
                return makeBattleRequest(username);
            }
        }catch(SQLException | InterruptedException e){
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private Response makeBattleRequest(String username) throws SQLException, InterruptedException {
        getBattleDao().create(username);
        BattleModel battle;
        //waits until req. has been accepted
        while((battle = getBattleDao().read(username)).getAcceptor() == null){
            //sleep(500);
            wait(500);
        }
        return startBattle(1, battle);
    }
    private Response startBattle(int role, BattleModel battle) throws SQLException, InterruptedException {
        //carry out fight only in role 2 (Acceptor)
        if(role == 1){
            //Requester
            BattleModel tempBattle;
            //waits until battle has ended
            while(!(tempBattle = getBattleDao().read(battle.getRequester())).isEnded()){
                //sleep(500);
                wait(500);
            }
        }else{
            //Acceptor
            //prepare everything for battle
                //check if users exist
            User requester = getUserDao().read(battle.getRequester());
            User acceptor = getUserDao().read(battle.getAcceptor());
            if(requester == null || acceptor == null){
                return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
            }
                //prepare Users for battle
                    //get their stacks
            ArrayList<Card> stackRequesterContent = getCardDao().readAllCardsFromUser(requester.getUserID());
            ArrayList<Card> stackAcceptorContent = getCardDao().readAllCardsFromUser(acceptor.getUserID());
            if(stackAcceptorContent.isEmpty() || stackRequesterContent.isEmpty()){
                return sendResponse("null", "Users without Cards", HttpStatus.CONFLICT);
            }
            StackOfCards stackRequester = new StackOfCards();
            StackOfCards stackAcceptor = new StackOfCards();
            stackRequester.addCards(stackRequesterContent);
            stackAcceptor.addCards(stackAcceptorContent);
            requester.setMyStack(stackRequester);
            requester.setMyStack(stackAcceptor);

                    //get their decks
            ArrayList<Card> deckRequesterContent = getDeckDao().readDeck(requester.getUserID());
            ArrayList<Card> deckAcceptorContent = getDeckDao().readDeck(acceptor.getUserID());
            if(deckRequesterContent.isEmpty() || deckAcceptorContent.isEmpty()){
                return sendResponse("null", "Users without set Deck", HttpStatus.CONFLICT);
            }
            Deck deckRequester = new Deck();
            Deck deckAcceptor = new Deck();
            deckRequester.addCards(deckRequesterContent);
            deckAcceptor.addCards(deckAcceptorContent);
            requester.setMyDeck(deckRequester);
            acceptor.setMyDeck(deckAcceptor);

            //start the battle
            Battle newBattle = new Battle(requester, acceptor, getBattleDirectController());
            newBattle.doBattle();
            battle.setEnded(true);
            getBattleDao().update(battle);
            sleep(500);
            getBattleDao().delete(battle);
        }
        return sendResponse("Battle ended successfully", "null", HttpStatus.OK);
    }
}
