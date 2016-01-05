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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Assert;
import org.junit.Test;

import javax.inject.Named;
import javax.inject.Scope;
import javax.inject.Singleton;

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
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(Instance.class).asStrictBinding().forItself();
            }
        });
        
        InjectMemberHolder holder = new InjectMemberHolder();
        injector.injectMembers(holder);
        
        Assert.assertNotNull(holder.privateInjectedMember);
        Assert.assertNotNull(holder.publicInjectedMember);
        Assert.assertTrue(holder.privateInjectedMethodCalled);
        Assert.assertTrue(holder.publicInjectedMethodCalled);
    }
    public static class InjectMemberHolder {
        @Inject
        private Instance privateInjectedMember;
        
        @Inject
        public Instance publicInjectedMember;
        
        private boolean privateInjectedMethodCalled;
        @Inject private void privateInjectedMethod(Instance i) {
            privateInjectedMethodCalled = true;
        }
        
        private boolean publicInjectedMethodCalled;
        @Inject public void publicInjectedMethod(Instance i) {
            publicInjectedMethodCalled = true;
        }
    }
    
    @Test
    public void testInjectMembersPublicOnly() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(Instance.class).asStrictBinding().forItself();
                container.allowInjections(InjectionType.PUBLIC_MEMBER_METHOD);
                container.allowInjections(InjectionType.PUBLIC_MEMBER_FIELD);
                container.allowInjections(InjectionType.PUBLIC_CONSTRUCTOR);
            }
        });
        
        InjectMemberHolder holder = new InjectMemberHolder();
        injector.injectMembers(holder);
        
        Assert.assertNull(holder.privateInjectedMember);
        Assert.assertNotNull(holder.publicInjectedMember);
        Assert.assertFalse(holder.privateInjectedMethodCalled);
        Assert.assertTrue(holder.publicInjectedMethodCalled);
    }
    
    @Test
    public void testInjectMembersPrivateOnly() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(Instance.class).asStrictBinding().forItself();
                container.allowInjections(InjectionType.NON_PUBLIC_MEMBER_METHOD);
                container.allowInjections(InjectionType.NON_PUBLIC_MEMBER_FIELD);
                container.allowInjections(InjectionType.PUBLIC_CONSTRUCTOR);
            }
        });
        
        InjectMemberHolder holder = new InjectMemberHolder();
        injector.injectMembers(holder);
        
        Assert.assertNotNull(holder.privateInjectedMember);
        Assert.assertNull(holder.publicInjectedMember);
        Assert.assertTrue(holder.privateInjectedMethodCalled);
        Assert.assertFalse(holder.publicInjectedMethodCalled);
    }
    
    @Test
    public void testInjectMembersNoneConfigured() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(Instance.class).asStrictBinding().forItself();
                container.allowInjections(InjectionType.PUBLIC_CONSTRUCTOR);
            }
        });
        
        InjectMemberHolder holder = new InjectMemberHolder();
        injector.injectMembers(holder);
        
        Assert.assertNull(holder.privateInjectedMember);
        Assert.assertNull(holder.publicInjectedMember);
        Assert.assertFalse(holder.privateInjectedMethodCalled);
        Assert.assertFalse(holder.publicInjectedMethodCalled);
    }
    
    @Test
    public void testInjectMembersTypeUnknown() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
            }
        });
        
        try {
            InjectMemberHolder holder = new InjectMemberHolder();
            injector.injectMembers(holder);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }
    
    @Test
    public void testInjectStatics() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(Instance.class).asStrictBinding().forItself();
            }
        });
        
        InjectStaticsHolder.reset();
        injector.injectStatics(InjectStaticsHolder.class);
        
        Assert.assertNotNull(InjectStaticsHolder.privateInjectedMember);
        Assert.assertNotNull(InjectStaticsHolder.publicInjectedMember);
        Assert.assertTrue(InjectStaticsHolder.privateInjectedMethodCalled);
        Assert.assertTrue(InjectStaticsHolder.publicInjectedMethodCalled);
    }
    public static class InjectStaticsHolder {
        @Inject
        private static Instance privateInjectedMember;
        
        @Inject
        public static Instance publicInjectedMember;
        
        private static boolean privateInjectedMethodCalled;
        @Inject private static void privateInjectedMethod(Instance i) {
            privateInjectedMethodCalled = true;
        }
        
        private static boolean publicInjectedMethodCalled;
        @Inject public static void publicInjectedMethod(Instance i) {
            publicInjectedMethodCalled = true;
        }
        
        private static void reset() {
            privateInjectedMember = null;
            publicInjectedMember = null;
            privateInjectedMethodCalled = false;
            publicInjectedMethodCalled = false;
        }
    }
    
    @Test
    public void testInjectStaticsPublicOnly() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(Instance.class).asStrictBinding().forItself();
                container.allowInjections(InjectionType.PUBLIC_STATIC_FIELD);
                container.allowInjections(InjectionType.PUBLIC_STATIC_METHOD);
                container.allowInjections(InjectionType.PUBLIC_CONSTRUCTOR);
            }
        });
        
        InjectStaticsHolder.reset();
        injector.injectStatics(InjectStaticsHolder.class);
        
        Assert.assertNull(InjectStaticsHolder.privateInjectedMember);
        Assert.assertNotNull(InjectStaticsHolder.publicInjectedMember);
        Assert.assertFalse(InjectStaticsHolder.privateInjectedMethodCalled);
        Assert.assertTrue(InjectStaticsHolder.publicInjectedMethodCalled);
    }
    
    @Test
    public void testInjectStaticsPrivateOnly() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(Instance.class).asStrictBinding().forItself();
                container.allowInjections(InjectionType.NON_PUBLIC_STATIC_FIELD);
                container.allowInjections(InjectionType.NON_PUBLIC_STATIC_METHOD);
                container.allowInjections(InjectionType.PUBLIC_CONSTRUCTOR);
            }
        });
        
        InjectStaticsHolder.reset();
        injector.injectStatics(InjectStaticsHolder.class);
        
        Assert.assertNotNull(InjectStaticsHolder.privateInjectedMember);
        Assert.assertNull(InjectStaticsHolder.publicInjectedMember);
        Assert.assertTrue(InjectStaticsHolder.privateInjectedMethodCalled);
        Assert.assertFalse(InjectStaticsHolder.publicInjectedMethodCalled);
    }
    
    @Test
    public void testInjectStaticsNoneConfigured() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(Instance.class).asStrictBinding().forItself();
                container.allowInjections(InjectionType.PUBLIC_CONSTRUCTOR);
            }
        });
        
        InjectStaticsHolder.reset();
        injector.injectStatics(InjectStaticsHolder.class);
        
        Assert.assertNull(InjectStaticsHolder.privateInjectedMember);
        Assert.assertNull(InjectStaticsHolder.publicInjectedMember);
        Assert.assertFalse(InjectStaticsHolder.privateInjectedMethodCalled);
        Assert.assertFalse(InjectStaticsHolder.publicInjectedMethodCalled);
    }
    
    @Test
    public void testInjectStaticsTypeUnknown() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
            }
        });
        
        try {
            InjectStaticsHolder.reset();
            injector.injectStatics(InjectStaticsHolder.class);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }
    
    @Test
    public void testQualifiedSingleton() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(SingletonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("one"));
                container.addImplType(SingletonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("two"));
                container.addImplType(SingletonHolder.class).asStrictBinding().forItself();
            }
        });
        
        SingletonHolder sh1 = injector.getInstance(SingletonHolder.class);
        SingletonHolder sh2 = injector.getInstance(SingletonHolder.class);
        
        Assert.assertSame(sh1.one, sh1.two);
        Assert.assertSame(sh2.one, sh2.two);
        Assert.assertSame(sh1.one, sh2.one);
    }
    @Singleton public static class SingletonType {}
    public static class SingletonHolder {
        @Inject @Named("one") SingletonType one;
        @Inject @Named("two") SingletonType two;
    }
    
    @Target(value = {ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(value = RetentionPolicy.RUNTIME)
    @Documented
    @Scope
    public static @interface CustomScope {}
    @CustomScope public static class ScopedType {}

    @Test
    public void testCustomScope() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(ScopedType.class).asStrictBinding().forItself();
                container.activateScopes(CustomScope.class);
            }
        });
        
        ScopedType st1 = injector.getInstance(ScopedType.class);
        ScopedType st2 = injector.getInstance(ScopedType.class);
        
        Assert.assertSame(st1, st2);
    }
    
    @Test
    public void testCustomScopeNotActive() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(ScopedType.class).asStrictBinding().forItself();
            }
        });
        
        try {
            ScopedType st1 = injector.getInstance(ScopedType.class);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {}
    }
    
    @Test
    public void testCustomScopeChildInjector() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(ScopedType.class).asStrictBinding().forItself();
            }
        });
        
        Injector injector2 = injector.enterScope(CustomScope.class);
        
        ScopedType st1 = injector2.getInstance(ScopedType.class);
        ScopedType st2 = injector2.getInstance(ScopedType.class);
        
        Assert.assertSame(st1, st2);
        
        Injector injector3 = injector.enterScope(CustomScope.class);
        
        ScopedType st3 = injector3.getInstance(ScopedType.class);
        ScopedType st4 = injector3.getInstance(ScopedType.class);
        
        Assert.assertSame(st3, st4);
        Assert.assertNotSame(st1, st3);
        
        try {
            ScopedType st5 = injector.getInstance(ScopedType.class);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {}
    }
    
    @Test
    public void testCustomScopeNested() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(ScopedType.class).asStrictBinding().forItself();
            }
        });
        
        Injector injector2 = injector.enterScope(CustomScope.class);
        
        ScopedType st1 = injector2.getInstance(ScopedType.class);
        ScopedType st2 = injector2.getInstance(ScopedType.class);
        
        Assert.assertSame(st1, st2);
        
        Injector injector3 = injector2.enterScope(CustomScope.class);
        
        ScopedType st3 = injector3.getInstance(ScopedType.class);
        ScopedType st4 = injector3.getInstance(ScopedType.class);
        
        Assert.assertSame(st3, st4);
        Assert.assertNotSame(st1, st3);
        
        try {
            ScopedType st5 = injector.getInstance(ScopedType.class);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {}
    }
    
    @Target(value = {ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(value = RetentionPolicy.RUNTIME)
    @Documented
    @Multiton
    @Scope
    public static @interface CustomMultiton {}
    @CustomMultiton public static class CustomMultitonType {}
    public static class CustomMultitonInjector {
        @Inject
        @Named("one")
        public CustomMultitonType injectedOne;
        
        @Inject
        @Named("two")
        public CustomMultitonType injectedTwo;
        
        @Inject
        @Named("one")
        public CustomMultitonType injectedOneAgain;
    }
    
    @Multiton public static class StandardMultitonType {}
    public static class StandardMultitonInjector {
        @Inject
        @Named("one")
        public StandardMultitonType injectedOne;
        
        @Inject
        @Named("two")
        public StandardMultitonType injectedTwo;
        
        @Inject
        @Named("one")
        public StandardMultitonType injectedOneAgain;
    }
    
    @Test
    public void testStandardMultiton() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(StandardMultitonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("one"));
                container.addImplType(StandardMultitonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("two"));
                container.addImplType(StandardMultitonInjector.class).asStrictBinding().forItself();
            }
        });
        
        StandardMultitonInjector smi1 = injector.getInstance(StandardMultitonInjector.class);
        StandardMultitonInjector smi2 = injector.getInstance(StandardMultitonInjector.class);
        
        Assert.assertSame(smi1.injectedOne, smi1.injectedOneAgain);
        Assert.assertNotSame(smi1.injectedOne, smi1.injectedTwo);
        
        Assert.assertSame(smi2.injectedOne, smi2.injectedOneAgain);
        Assert.assertSame(smi1.injectedOne, smi2.injectedOne);
        Assert.assertNotSame(smi2.injectedOne, smi2.injectedTwo);
        Assert.assertSame(smi1.injectedTwo, smi2.injectedTwo);
    }
    
    @Test
    public void testCustomMultiton() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(CustomMultitonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("one"));
                container.addImplType(CustomMultitonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("two"));
                container.addImplType(CustomMultitonInjector.class).asStrictBinding().forItself();
                container.activateScopes(CustomMultiton.class);
            }
        });
        
        CustomMultitonInjector smi1 = injector.getInstance(CustomMultitonInjector.class);
        CustomMultitonInjector smi2 = injector.getInstance(CustomMultitonInjector.class);
        
        Assert.assertSame(smi1.injectedOne, smi1.injectedOneAgain);
        Assert.assertNotSame(smi1.injectedOne, smi1.injectedTwo);
        
        Assert.assertSame(smi2.injectedOne, smi2.injectedOneAgain);
        Assert.assertSame(smi1.injectedOne, smi2.injectedOne);
        Assert.assertNotSame(smi2.injectedOne, smi2.injectedTwo);
        Assert.assertSame(smi1.injectedTwo, smi2.injectedTwo);
    }
    
    @Test
    public void testCustomMultitonNotActive() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(CustomMultitonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("one"));
                container.addImplType(CustomMultitonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("two"));
                container.addImplType(CustomMultitonInjector.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(CustomMultitonInjector.class);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {}
    }
    
    @Test
    public void testCustomMultitonChildInjector() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(CustomMultitonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("one"));
                container.addImplType(CustomMultitonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("two"));
                container.addImplType(CustomMultitonInjector.class).asStrictBinding().forItself();
            }
        });
        
        Injector injector2 = injector.enterScope(CustomMultiton.class);
        
        CustomMultitonInjector smi1 = injector2.getInstance(CustomMultitonInjector.class);
        CustomMultitonInjector smi2 = injector2.getInstance(CustomMultitonInjector.class);
        
        Assert.assertSame(smi1.injectedOne, smi1.injectedOneAgain);
        Assert.assertNotSame(smi1.injectedOne, smi1.injectedTwo);
        
        Assert.assertSame(smi2.injectedOne, smi2.injectedOneAgain);
        Assert.assertSame(smi1.injectedOne, smi2.injectedOne);
        Assert.assertNotSame(smi2.injectedOne, smi2.injectedTwo);
        Assert.assertSame(smi1.injectedTwo, smi2.injectedTwo);
        
        Injector injector3 = injector.enterScope(CustomMultiton.class);
        
        CustomMultitonInjector smi3 = injector3.getInstance(CustomMultitonInjector.class);
        CustomMultitonInjector smi4 = injector3.getInstance(CustomMultitonInjector.class);
        
        Assert.assertSame(smi3.injectedOne, smi3.injectedOneAgain);
        Assert.assertNotSame(smi3.injectedOne, smi3.injectedTwo);
        
        Assert.assertSame(smi4.injectedOne, smi4.injectedOneAgain);
        Assert.assertSame(smi3.injectedOne, smi4.injectedOne);
        Assert.assertNotSame(smi4.injectedOne, smi4.injectedTwo);
        Assert.assertSame(smi3.injectedTwo, smi4.injectedTwo);
        
        Assert.assertNotSame(smi1.injectedOne, smi3.injectedOne);
        Assert.assertNotSame(smi1.injectedTwo, smi3.injectedTwo);
        
        try {
            injector.getInstance(CustomMultitonInjector.class);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {}
    }
    
    @Test
    public void testCustomMultitonNested() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(CustomMultitonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("one"));
                container.addImplType(CustomMultitonType.class).asStrictBinding().forItself().whenQualifiedWith(Qualifiers.named("two"));
                container.addImplType(CustomMultitonInjector.class).asStrictBinding().forItself();
            }
        });
        
        Injector injector2 = injector.enterScope(CustomMultiton.class);
        
        CustomMultitonInjector smi1 = injector2.getInstance(CustomMultitonInjector.class);
        CustomMultitonInjector smi2 = injector2.getInstance(CustomMultitonInjector.class);
        
        Assert.assertSame(smi1.injectedOne, smi1.injectedOneAgain);
        Assert.assertNotSame(smi1.injectedOne, smi1.injectedTwo);
        
        Assert.assertSame(smi2.injectedOne, smi2.injectedOneAgain);
        Assert.assertSame(smi1.injectedOne, smi2.injectedOne);
        Assert.assertNotSame(smi2.injectedOne, smi2.injectedTwo);
        Assert.assertSame(smi1.injectedTwo, smi2.injectedTwo);
        
        Injector injector3 = injector2.enterScope(CustomMultiton.class);
        
        CustomMultitonInjector smi3 = injector3.getInstance(CustomMultitonInjector.class);
        CustomMultitonInjector smi4 = injector3.getInstance(CustomMultitonInjector.class);
        
        Assert.assertSame(smi3.injectedOne, smi3.injectedOneAgain);
        Assert.assertNotSame(smi3.injectedOne, smi3.injectedTwo);
        
        Assert.assertSame(smi4.injectedOne, smi4.injectedOneAgain);
        Assert.assertSame(smi3.injectedOne, smi4.injectedOne);
        Assert.assertNotSame(smi4.injectedOne, smi4.injectedTwo);
        Assert.assertSame(smi3.injectedTwo, smi4.injectedTwo);
        
        Assert.assertNotSame(smi1.injectedOne, smi3.injectedOne);
        Assert.assertNotSame(smi1.injectedTwo, smi3.injectedTwo);
        
        try {
            injector.getInstance(CustomMultitonInjector.class);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {}
    }
    
    @Test
    public void testQualifiedTypeNotAutomatic() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(QualifiedType.class).asStrictBinding().forItself();
                container.addImplType(QualifiedTypeHolder.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(QualifiedTypeHolder.class);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }
    @Named("qualified") public static class QualifiedType {}
    public static class QualifiedTypeHolder {
        @Inject @Named("qualified") QualifiedType type;
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
