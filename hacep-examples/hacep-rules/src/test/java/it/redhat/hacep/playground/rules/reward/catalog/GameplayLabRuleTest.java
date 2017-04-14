/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.redhat.hacep.playground.rules.reward.catalog;

import it.redhat.hacep.playground.rules.model.Gameplay;
import it.redhat.hacep.playground.rules.model.util.GameplayBetGenerator;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class GameplayLabRuleTest {

    private static KieBase kieBase;

    private KieSession session;

    private SessionPseudoClock clock;

    private RulesFiredAgendaEventListener rulesFired;

    @BeforeClass
    public static void init() throws Exception {
        kieBase = KieAPITestUtils.setupKieBase("rules/reward-catalogue.drl", "rules/reward-point-level-demo.drl");
    }

    @Before
    public void setupTest() throws Exception {
        this.session = KieAPITestUtils.buildKieSession(kieBase);
        this.session.registerChannel(AuditChannel.CHANNEL_ID, new AuditChannel());
        this.session.registerChannel(SysoutChannel.CHANNEL_ID, new SysoutChannel());
        this.session.registerChannel(PointsChannel.CHANNEL_ID, new PointsChannel());

        this.rulesFired = new RulesFiredAgendaEventListener();
        this.session.addEventListener(rulesFired);
        this.clock = this.session.getSessionClock();
    }

    @Test
    public void testUserPlaysGame4TimesIn1Minute() {
        int gameGenerated = 4;
        int duration = 1;

        assertEquals("======: 4 consequences expected", 4, simulateGames(gameGenerated,duration));
    }

    @Test
    public void testUserPlaysGame6TimesIn1Minute() {
        int gameGenerated = 6;
        int duration = 1;

        assertEquals("======: 7 consequences expected", 7, simulateGames(gameGenerated,duration));
    }

    @Test
    public void testUserPlaysGame6TimesIn2Minutes() {
        int gameGenerated = 6;
        int duration = 2;

        assertEquals("======: 6 consequence expected", 6, simulateGames(gameGenerated, duration));
    }

    @Test
    public void testUserPlaysGame6TimesIn2MinutesAndBets() {
        int gameGenerated = 6;
        int duration = 2;
        long amount = 2000;

        assertEquals("======: 10 consequences expected", 10, simulateGames(10101l, gameGenerated, duration, amount));
    }

    @Test
    public void testUserPlaysGame6TimesIn1MinuteAndBets() {
        int gameGenerated = 6;
        int duration = 1;
        long amount = 1000;

        assertEquals("======: 9 consequences expected", 9, simulateGames(10101l, gameGenerated,duration,amount));
    }

    @Test
    public void testTwoUsersPlayGamesAndBets() {
        assertEquals("======: 7 consequence expected", 7, simulateGames(30303l, 5,1,1000l));
        assertEquals("======: 10 consequences expected", 10, simulateGames(20202l, 6,2,2000l));
    }

    private int simulateGames(int gameGenerated, int duration) {
        return this.simulateGames(10101l, gameGenerated, duration, 0l);
    }

    private int simulateGames(long playerId, int gameGenerated, int duration, long amount) {
        long now = System.currentTimeMillis();
        clock.advanceTime(now, TimeUnit.MILLISECONDS);

        GameplayBetGenerator generator = new GameplayBetGenerator();
        generator.playerId(playerId).amount(amount).timestamp(System.currentTimeMillis(), duration, TimeUnit.MINUTES).count(gameGenerated);

        int consequences = 0;

        for (Gameplay g : generator.generate()){
            consequences += insertGame(g);
        }

        return consequences;
    }

    private int insertGame(Gameplay g ) {
        long gts = g.getTimestamp().getTime();
        long current = clock.getCurrentTime();
        clock.advanceTime(gts - current, TimeUnit.MILLISECONDS);
        session.insert(g);
        session.fireAllRules();
        int firedEventsCount = this.rulesFired.getAfterMatchFiredEvents().size();
        this.rulesFired.clear();
        return firedEventsCount;
    }
}