package it.redhat.hacep.playground.rules.reward.catalog;

import it.redhat.hacep.playground.rules.model.outcome.PlayerPointLevel;

import org.kie.api.runtime.Channel;

public class PointsChannel implements Channel {

    public static final String CHANNEL_ID = "playerPointsLevel";

    @Override
    public void send(Object object) {
        if (object instanceof PlayerPointLevel){
            PlayerPointLevel ppl = (PlayerPointLevel) object;
            System.out.println("Player: " + ppl.getPlayerId() + " level: " + ppl.getLevel() + " points: " + ppl.getPoints());
        } else {
            System.out.println(object.toString());
        }
    }

}