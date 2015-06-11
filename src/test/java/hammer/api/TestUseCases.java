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

import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class TestUseCases {
    
    @Test
    public void testInstanceBinding() throws Exception {
        final Instance i = new Instance();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(i).asStrictBinding().forItself();
                container.addImplType(InstanceHolder.class).asStrictBinding().forItself();
            }
        });
        InstanceHolder ih = injector.getInstance(InstanceHolder.class);
        Assert.assertEquals(i, ih.i);
    }
    public static class Instance {}
    public static class InstanceHolder { @Inject public Instance i; }
    
    @Test
    public void testMultibindingNotAllowed() throws Exception {
        
    }
    
    @Test
    public void testMultibindingAllowedRoundRobin() throws Exception {
        
    }
    
    @Test
    public void testFreeBinding() throws Exception {
        
    }
    
    @Test
    public void testInjectMembers() throws Exception {
        
    }
    
    @Test
    public void testInjectMembersNotAllowed() throws Exception {
        
    }
    
    @Test
    public void testInjectMembersTypeUnknown() throws Exception {
        
    }
    
    @Test
    public void testInjectStatics() throws Exception {
        
    }
    
    @Test
    public void testInjectStaticsNotAllowed() throws Exception {
        
    }
    
    @Test
    public void testInjectStaticsTypeUnknown() throws Exception {
        
    }
    
    @Test
    public void testCustomScope() throws Exception {
        
    }
    
    @Test
    public void testCustomScopeNotActive() throws Exception {
        
    }
    
    @Test
    public void testCustomScopeActiveThenNotActive() throws Exception {
        
    }
    
    @Test
    public void testCustomQualifier() throws Exception {
        
    }
    
    @Test
    public void testStandardMultiton() throws Exception {
        
    }
    
    @Test
    public void testCustomMultiton() throws Exception {
        
    }
    
    @Test
    public void testCustomMultitonNotActive() throws Exception {
        
    }
    
    @Test(expected = IllegalStateException.class)
    public void testCircularDependencies() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(A.class).asStrictBinding().forItself();
                container.addImplType(B.class).asStrictBinding().forItself();
            }
        });
        injector.getInstance(A.class);
    }
    public static class A { @Inject B b; }
    public static class B { @Inject A a; }
    
    @Test
    public void testCircularDependenciesProvider() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(A1.class).asStrictBinding().forItself();
                container.addImplType(B1.class).asStrictBinding().forItself();
            }
        });
        injector.getInstance(A1.class);
    }
    public static class A1 { @Inject B1 b; }
    public static class B1 { @Inject Provider<A1> a; }
    
}
