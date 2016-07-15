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

import org.kie.api.event.rule.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RulesFiredAgendaEventListener implements AgendaEventListener {

    private List<AfterMatchFiredEvent> afterMatchFiredEvents = new ArrayList<>();

    public void clear() {
        afterMatchFiredEvents.clear();
    }

    public List<AfterMatchFiredEvent> getAfterMatchFiredEvents() {
        return Collections.unmodifiableList(afterMatchFiredEvents);
    }

    @Override
    public void matchCreated(MatchCreatedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void matchCancelled(MatchCancelledEvent event) {
        // TODO Auto-generated method stub
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        // TODO Auto-generated method stub
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        afterMatchFiredEvents.add(event);
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        // TODO Auto-generated method stub
    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        // TODO Auto-generated method stub

    }

}
