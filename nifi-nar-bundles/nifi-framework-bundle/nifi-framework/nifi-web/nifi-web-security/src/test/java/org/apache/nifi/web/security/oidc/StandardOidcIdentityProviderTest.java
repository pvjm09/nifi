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
package org.apache.nifi.web.security.oidc;

import com.nimbusds.oauth2.sdk.Scope;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StandardOidcIdentityProviderTest {

    @Test
    public void testValidateScopes() {
        final String additionalScope_profile = "profile";
        final String additionalScope_abc = "abc";

        final StandardOidcIdentityProvider provider = createOidcProviderWithAdditionalScopes(additionalScope_profile,
            additionalScope_abc);
        Scope scope = provider.getScope();

        // two additional scopes are set, two (openid, email) are hard-coded
        assertEquals(scope.toArray().length, 4);
        assertTrue(scope.contains("openid"));
        assertTrue(scope.contains("email"));
        assertTrue(scope.contains(additionalScope_profile));
        assertTrue(scope.contains(additionalScope_abc));
    }

    @Test
    public void testNoDuplicatedScopes() {
        final String additionalScopeDuplicate = "abc";

        final StandardOidcIdentityProvider provider = createOidcProviderWithAdditionalScopes(additionalScopeDuplicate,
                "def", additionalScopeDuplicate);
        Scope scope = provider.getScope();

        // three additional scopes are set but one is duplicated and mustn't be returned; note that there is
        // another one inserted in between the duplicated; two (openid, email) are hard-coded
        assertEquals(scope.toArray().length, 4);
    }

    private StandardOidcIdentityProvider createOidcProviderWithAdditionalScopes(String... additionalScopes) {
        final StandardOidcIdentityProvider provider = mock(StandardOidcIdentityProvider.class);
        NiFiProperties properties = createNiFiPropertiesMockWithAdditionalScopes(Arrays.asList(additionalScopes));
        Whitebox.setInternalState(provider, "properties", properties);

        when(provider.isOidcEnabled()).thenReturn(true);
        when(provider.getScope()).thenCallRealMethod();

        return provider;
    }

    private NiFiProperties createNiFiPropertiesMockWithAdditionalScopes(List<String> additionalScopes) {
        NiFiProperties properties = mock(NiFiProperties.class);
        when(properties.getOidcAdditionalScopes()).thenReturn(additionalScopes);
        return properties;
    }
}