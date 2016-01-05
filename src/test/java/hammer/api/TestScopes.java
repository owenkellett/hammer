/**
 * Copyright 2015 hammer Contributors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hammer.api;

import javax.inject.Scope;
import javax.inject.Singleton;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class TestScopes {
    
    @Test
    public void testSingletonEquality() throws Exception {
        Singleton singleton = Annotated.class.getAnnotation(Singleton.class);

        Assert.assertEquals(singleton, Scopes.SINGLETON);
        Assert.assertEquals(singleton, Scopes.scope(Singleton.class));
        Assert.assertEquals(Scopes.SINGLETON, Scopes.scope(Singleton.class));
        Assert.assertEquals(Scopes.scope(Singleton.class), Scopes.SINGLETON);
    }
    
    @Test
    public void testMultitonEquality() throws Exception {
        Multiton multiton = MultiAnnotated.class.getAnnotation(Multiton.class);

        Assert.assertEquals(multiton, Scopes.MULTITON);
        Assert.assertEquals(multiton, Scopes.scope(Multiton.class));
        Assert.assertEquals(Scopes.MULTITON, Scopes.scope(Multiton.class));
        Assert.assertEquals(Scopes.scope(Multiton.class), Scopes.MULTITON);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNoScope() throws Exception {
        Scopes.scope(Scope.class);
    }
    
    @Singleton
    public static class Annotated {}
    
    @Multiton
    public static class MultiAnnotated {}
}
