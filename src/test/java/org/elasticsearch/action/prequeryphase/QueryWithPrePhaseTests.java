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

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugins.PluginsService;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertHitCount;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertSearchResponse;


/**
 *
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, transportClientRatio = 0)
public class QueryWithPrePhaseTests extends ElasticsearchIntegrationTest {

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.builder()
                .put(super.nodeSettings(nodeOrdinal))
                .put("plugins." + PluginsService.LOAD_PLUGIN_FROM_CLASSPATH, true)
                .build();
    }

    @Test
    public void testSimpleTestOneDoc() throws Exception {
        client().prepareIndex("test", "type", "1").setSource("field", "I am sam").execute().actionGet();
        client().prepareIndex("test", "type", "2").setSource("field", "green eggs and ham").execute().actionGet();
        refresh();
        QueryWithPrePhaseBuilder builder = new QueryWithPrePhaseBuilder(client());
        SearchRequest searchRequest = new SearchRequest();
        XContentBuilder queryBuilder = jsonBuilder();
        queryBuilder.startObject().startObject("query");
        QueryBuilders.matchAllQuery().doXContent(queryBuilder, null);
        queryBuilder.endObject().endObject();
        searchRequest.source(queryBuilder.bytes().toBytes());
        builder.searchRequest(searchRequest);
        SearchResponse searchResponse = builder.get();
        assertSearchResponse(searchResponse);
        assertHitCount(searchResponse, 2);
    }
}
