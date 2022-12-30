package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BattleModel {
    @JsonAlias({"battleID"})
    private int battleID;
    @JsonAlias({"requester"})
    private String requester;
    @JsonAlias({"acceptor"})
    private String acceptor;
    @JsonAlias({"ended"})
    private boolean ended;

    public BattleModel (){}
}
