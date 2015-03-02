/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticsearch.plugin.prequeryphase;

import org.elasticsearch.action.ActionModule;
import org.elasticsearch.action.prequeryphase.QueryWithPrePhaseAction;
import org.elasticsearch.action.prequeryphase.TransportQueryWithPrePhaseAction;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.rest.RestModule;
import org.elasticsearch.rest.action.prequeryphase.RestQueryWithPrePhaseAction;

/**
 *
 */
public class PreQueryPhasePlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "query-with-pre-phase";
    }

    @Override
    public String description() {
        return "a stub to explain how to add a pre phase to a query if one is really desperate";
    }

    @Override
    public void processModule(Module module) {
        if (module instanceof ActionModule) {
            ActionModule actionModule = (ActionModule) module;
            actionModule.registerAction(QueryWithPrePhaseAction.INSTANCE, TransportQueryWithPrePhaseAction.class);
        } else if (module instanceof RestModule) {
            RestModule restModule = (RestModule) module;
            restModule.addRestAction(RestQueryWithPrePhaseAction.class);
            ESLoggerFactory.getRootLogger().info("registered rest handler for query pre phase");
        }
    }
}
