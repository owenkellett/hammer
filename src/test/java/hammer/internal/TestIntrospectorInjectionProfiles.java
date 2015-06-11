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
package hammer.internal;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import hammer.api.InjectionType;
import hammer.api.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.inject.Inject;

/**
 *
 */
public class TestIntrospectorInjectionProfiles {
    
    private Introspector.AccessProfile fullProfile;
    
    @Before
    public void before() throws Exception {
        fullProfile = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR,
                              InjectionType.NON_PUBLIC_CONSTRUCTOR,
                              InjectionType.PUBLIC_MEMBER_FIELD,
                              InjectionType.NON_PUBLIC_MEMBER_FIELD,
                              InjectionType.PUBLIC_MEMBER_METHOD,
                              InjectionType.NON_PUBLIC_MEMBER_METHOD,
                              InjectionType.PUBLIC_STATIC_FIELD,
                              InjectionType.NON_PUBLIC_STATIC_FIELD,
                              InjectionType.PUBLIC_STATIC_METHOD,
                              InjectionType.NON_PUBLIC_STATIC_METHOD));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInterface() throws Exception {
        Introspector.getInjectionProfile(new TypeToken<Interface>() {}, fullProfile);
    }
    public static interface Interface {}
    
    @Test(expected = IllegalArgumentException.class)
    public void testAbstractClass() throws Exception {
        Introspector.getInjectionProfile(new TypeToken<AbstractClass>() {}, fullProfile);
    }
    public static abstract class AbstractClass {}
    
    @Test(expected = IllegalArgumentException.class)
    public void testInnerClass() throws Exception {
        Introspector.getInjectionProfile(new TypeToken<InnerClass>() {}, fullProfile);
    }
    public class InnerClass {}
    
    @Test(expected = IllegalArgumentException.class)
    public void testLocalClass() throws Exception {
        class LocalClass {}
        Introspector.getInjectionProfile(new TypeToken<LocalClass>() {}, fullProfile);
    }
    
    @Test
    public void testDefaultConstructor() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<DefaultConstructor>() {}, fullProfile);
        
        Assert.assertNotNull(ip.getInjectableConstructor());
    }
    public static class DefaultConstructor {}
    
    @Test
    public void testPublicConstructor() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<PublicConstructor>() {}, fullProfile);
        
        Assert.assertNotNull(ip.getInjectableConstructor());
        Assert.assertEquals(1, ip.getInjectableConstructor().getParameterTypes().length);
    }
    public static class PublicConstructor { @Inject public PublicConstructor(Interface i) {}}
    
    @Test(expected = IllegalArgumentException.class)
    public void testPublicConstructorNotAllowed() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<PublicConstructor>() {}, 
                Introspector.getAccessProfile(Arrays.asList(InjectionType.NON_PUBLIC_CONSTRUCTOR)));
    }
    
    @Test
    public void testNonPublicConstructor() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<NonPublicConstructor>() {}, fullProfile);
        
        Assert.assertNotNull(ip.getInjectableConstructor());
        Assert.assertEquals(1, ip.getInjectableConstructor().getParameterTypes().length);
    }
    public static class NonPublicConstructor { @Inject NonPublicConstructor(Interface i) {}}
    
    @Test(expected = IllegalArgumentException.class)
    public void testNonPublicConstructorNotAllowed() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<NonPublicConstructor>() {}, 
                Introspector.getAccessProfile(Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInjectableConstructors() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<MultipleConstructors>() {}, fullProfile);
    }
    public static class MultipleConstructors {
        @Inject public MultipleConstructors() {}
        @Inject public MultipleConstructors(Interface i) {}
    }
    
    
    @Test
    public void testPublicMemberField() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<PublicMemberField>() {}, fullProfile);
        
        Assert.assertTrue(ip.getInjectableMembers().iterator().hasNext());
        Assert.assertEquals("member", ((Field)ip.getInjectableMembers().iterator().next()).getName());
    }
    public static class PublicMemberField {
        @Inject public Interface member;
    }
    
    @Test
    public void testPublicMemberFieldNotAllowed() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<PublicMemberField>() {}, 
                Introspector.getAccessProfile(Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR,
                                                            InjectionType.NON_PUBLIC_MEMBER_FIELD)));
        
        Assert.assertFalse(ip.getInjectableMembers().iterator().hasNext());
    }
    
    @Test
    public void testNonPublicMemberField() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<NonPublicMemberField>() {}, fullProfile);
        
        Assert.assertTrue(ip.getInjectableMembers().iterator().hasNext());
        Assert.assertEquals("member", ((Field)ip.getInjectableMembers().iterator().next()).getName());
    }
    public static class NonPublicMemberField {
        @Inject private Interface member;
    }
    
    @Test
    public void testNonPublicMemberFieldNotAllowed() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<NonPublicMemberField>() {}, 
                Introspector.getAccessProfile(Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR,
                                                            InjectionType.PUBLIC_MEMBER_FIELD)));
        
        Assert.assertFalse(ip.getInjectableMembers().iterator().hasNext());
    }
    
    @Test
    public void testFinalMemberField() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<FinalMemberField>() {}, fullProfile);
        
        Assert.assertFalse(ip.getInjectableMembers().iterator().hasNext());
    }
    public static class FinalMemberField {
        @Inject public final Interface member = null;
    }
    
    @Test
    public void testPublicStaticField() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<PublicStaticField>() {}, fullProfile);
        
        Assert.assertTrue(ip.getInjectableStatics().iterator().hasNext());
        Assert.assertEquals("member", ((Field)ip.getInjectableStatics().iterator().next()).getName());
    }
    public static class PublicStaticField {
        @Inject public static Interface member;
    }
    
    @Test
    public void testPublicStaticFieldNotAllowed() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<PublicStaticField>() {}, 
                Introspector.getAccessProfile(Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR,
                                                            InjectionType.NON_PUBLIC_STATIC_FIELD)));
        
        Assert.assertFalse(ip.getInjectableStatics().iterator().hasNext());
    }
    
    @Test
    public void testNonPublicStaticField() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<NonPublicStaticField>() {}, fullProfile);
        
        Assert.assertTrue(ip.getInjectableStatics().iterator().hasNext());
        Assert.assertEquals("member", ((Field)ip.getInjectableStatics().iterator().next()).getName());
    }
    public static class NonPublicStaticField {
        @Inject private static Interface member;
    }
    
    @Test
    public void testNonPublicStaticFieldNotAllowed() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<NonPublicStaticField>() {}, 
                Introspector.getAccessProfile(Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR,
                                                            InjectionType.PUBLIC_STATIC_FIELD)));
        
        Assert.assertFalse(ip.getInjectableStatics().iterator().hasNext());
    }
    
    @Test
    public void testFinalStaticField() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<FinalStaticField>() {}, fullProfile);
        
        Assert.assertFalse(ip.getInjectableStatics().iterator().hasNext());
    }
    public static class FinalStaticField {
        @Inject public static final Interface member = null;
    }
    
    @Test
    public void testPublicMethod() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<PublicMethod>() {}, fullProfile);
        
        Assert.assertTrue(ip.getInjectableMembers().iterator().hasNext());
        Assert.assertEquals("method", ((Method)ip.getInjectableMembers().iterator().next()).getName());
    }
    public static class PublicMethod {
        @Inject public void method() {}
    }
    
    @Test
    public void testPublicMethodNotAllowed() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<PublicMethod>() {}, 
                Introspector.getAccessProfile(Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR,
                                                            InjectionType.NON_PUBLIC_MEMBER_METHOD)));
        
        Assert.assertFalse(ip.getInjectableMembers().iterator().hasNext());
    }
    
    @Test
    public void testNonPublicMethod() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<NonPublicMethod>() {}, fullProfile);
        
        Assert.assertTrue(ip.getInjectableMembers().iterator().hasNext());
        Assert.assertEquals("method", ((Method)ip.getInjectableMembers().iterator().next()).getName());
    }
    public static class NonPublicMethod {
        @Inject private void method() {}
    }
    
    @Test
    public void testNonPublicMethodNotAllowed() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<NonPublicMethod>() {}, 
                Introspector.getAccessProfile(Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR,
                                                            InjectionType.PUBLIC_MEMBER_METHOD)));
        
        Assert.assertFalse(ip.getInjectableMembers().iterator().hasNext());
    }
    
    @Test
    public void testAbstractMethod() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<AbstractMethod>() {}, fullProfile);
        
        Assert.assertFalse(ip.getInjectableMembers().iterator().hasNext());
    }
    public static abstract class AbstractClass2 {
        @Inject public abstract void method();
    }
    public static class AbstractMethod extends AbstractClass {
        public void method() {}
    }
    
    @Test
    public void testTypeParameterMethod() throws Exception {
        Introspector.InjectionProfile ip = Introspector.getInjectionProfile(
                new TypeToken<TypeParameterMethod>() {}, fullProfile);
        
        Assert.assertFalse(ip.getInjectableMembers().iterator().hasNext());
    }
    public static class TypeParameterMethod {
        @Inject public <T> void method() {}
    }
    
}
