/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action.prequeryphase;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.TransportSearchAction;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

public class TransportQueryWithPrePhaseAction extends HandledTransportAction<QueryWithPrePhaseRequest, SearchResponse> {

    TransportSearchAction transportSearchAction;

    @Inject
    public TransportQueryWithPrePhaseAction(Settings settings, ThreadPool threadPool, TransportService transportService,
                                            ActionFilters actionFilters, TransportSearchAction transportSearchAction) {
        super(settings, QueryWithPrePhaseAction.NAME, threadPool, transportService, actionFilters);
        this.transportSearchAction = transportSearchAction;
    }

    @Override
    protected void doExecute(final QueryWithPrePhaseRequest request, final ActionListener<SearchResponse> listener) {
        SearchRequest searchRequest = request.searchRequest();
        /** here execute all pre query operations and construct the new search request
         * needs parsing etc.
         * for example: we could have something like this in the request.searchRequest() source:
         *
         * "query_template": {
         *      query with placeholders for function score parameters, the "query" part in http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/query-dsl-template-query.html
         * }
         * "pre_phase_params": {
         *  everything needed to execute whatever we need to fill the template parameters
         * }
         *
         * then get all values for the params in http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/query-dsl-template-query.html
         * then construct the actual query json like in http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/query-dsl-template-query.html
         *
         * ...
         * then execute the new search request:
         **/

        transportSearchAction.execute(searchRequest, new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                listener.onResponse(searchResponse);
            }

            @Override
            public void onFailure(Throwable e) {
                listener.onFailure(e);
            }
        });
    }

    @Override
    public QueryWithPrePhaseRequest newRequestInstance() {
        return new QueryWithPrePhaseRequest();
    }
}
