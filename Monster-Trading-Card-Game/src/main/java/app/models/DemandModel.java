package app.models;

import card.Enum.ELEMENT;
import card.Enum.TYPE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DemandModel {
    private int userID;
    private int minDamage;
    private ELEMENT element;
    private TYPE type;

    public DemandModel(){}
}
