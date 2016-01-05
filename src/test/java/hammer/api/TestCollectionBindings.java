
package hammer.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 *
 */
public class TestCollectionBindings {
    
    public static class PlainType {}
    public static class ParameterizedType<T> {}
    public static class InstanceType {}
    
    @Singleton
    public static class SingletonPlainType {}
    @Singleton
    public static class SingletonParameterizedType<T> {}
    
    public static class QualifiedPlainTypeInjection {
        @Inject
        @Named("test")
        public Map<String, PlainType> injected;
    }
    
    public static class QualifiedParameterizedTypeInjection {
        @Inject
        @Named("test")
        public Map<String, ParameterizedType<String>> injected;
    }
    
    public static class QualifiedInstanceTypeInjection {
        @Inject
        @Named("test")
        public Map<String, InstanceType> injected;
    }
    
    public static class QualifiedPlainTypeInjection2 {
        @Inject
        @Named("test2")
        public Map<String, PlainType> injected;
    }
    
    public static class QualifiedParameterizedTypeInjection2 {
        @Inject
        @Named("test2")
        public Map<String, ParameterizedType<String>> injected;
    }
    
    public static class QualifiedInstanceTypeInjection2 {
        @Inject
        @Named("test2")
        public Map<String, InstanceType> injected;
    }
    
    public static class QualifiedPlainTypeListInjection {
        @Inject
        @Named("test")
        public List<PlainType> injected;
    }
    
    public static class QualifiedParameterizedTypeListInjection {
        @Inject
        @Named("test")
        public List<ParameterizedType<String>> injected;
    }
    
    public static class QualifiedInstanceTypeListInjection {
        @Inject
        @Named("test")
        public List<InstanceType> injected;
    }
    
    public static class QualifiedPlainTypeListInjection2 {
        @Inject
        @Named("test2")
        public List<PlainType> injected;
    }
    
    public static class QualifiedParameterizedTypeListInjection2 {
        @Inject
        @Named("test2")
        public List<ParameterizedType<String>> injected;
    }
    
    public static class QualifiedInstanceTypeListInjection2 {
        @Inject
        @Named("test2")
        public List<InstanceType> injected;
    }
    
    public static class QualifiedPlainTypeSetInjection {
        @Inject
        @Named("test")
        public Set<PlainType> injected;
    }
    
    public static class QualifiedParameterizedTypeSetInjection {
        @Inject
        @Named("test")
        public Set<ParameterizedType<String>> injected;
    }
    
    public static class QualifiedInstanceTypeSetInjection {
        @Inject
        @Named("test")
        public Set<InstanceType> injected;
    }
    
    public static class QualifiedPlainTypeSetInjection2 {
        @Inject
        @Named("test2")
        public Set<PlainType> injected;
    }
    
    public static class QualifiedParameterizedTypeSetInjection2 {
        @Inject
        @Named("test2")
        public Set<ParameterizedType<String>> injected;
    }
    
    public static class QualifiedInstanceTypeSetInjection2 {
        @Inject
        @Named("test2")
        public Set<InstanceType> injected;
    }
    
    @Test
    public void testMapBindingWithClassSingleValue() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asMapMemberBinding()
                        .forMapType(String.class, PlainType.class)
                        .withKey("test");
            }
        });
        
        Map<String, PlainType> map = injector.getInstance(new TypeToken<Map<String, PlainType>>() {});
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.containsKey("test"));
    }
    
    @Test
    public void testMapBindingWithTypeTokenSingleValue() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test");
            }
        });
        
        Map<String, ParameterizedType<String>> map = injector.getInstance(new TypeToken<Map<String, ParameterizedType<String>>>() {});
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.containsKey("test"));
    }
    
    @Test
    public void testMapBindingWithInstanceSingleValue() throws Exception {
        final ParameterizedType<String> test = new ParameterizedType<>();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(test)
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test");
            }
        });
        
        Map<String, ParameterizedType<String>> map = injector.getInstance(new TypeToken<Map<String, ParameterizedType<String>>>() {});
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.containsKey("test"));
        Assert.assertSame(test, map.get("test"));
    }
    
    @Test
    public void testMapBindingWithClassMultipleValues() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asMapMemberBinding()
                        .forMapType(String.class, PlainType.class)
                        .withKey("test");
                container.addImplType(PlainType.class)
                        .asMapMemberBinding()
                        .forMapType(String.class, PlainType.class)
                        .withKey("test2");
            }
        });
        
        Map<String, PlainType> map = injector.getInstance(new TypeToken<Map<String, PlainType>>() {});
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("test"));
        Assert.assertTrue(map.containsKey("test2"));
        Assert.assertNotSame(map.get("test"), map.get("test2"));
    }
    
    @Test
    public void testMapBindingWithTypeTokenMultipleValues() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test");
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test2");
            }
        });
        
        Map<String, ParameterizedType<String>> map = injector.getInstance(new TypeToken<Map<String, ParameterizedType<String>>>() {});
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("test"));
        Assert.assertTrue(map.containsKey("test2"));
        Assert.assertNotSame(map.get("test"), map.get("test2"));
    }
    
    @Test
    public void testMapBindingWithInstanceMultipleValues() throws Exception {
        final ParameterizedType<String> test = new ParameterizedType<>();
        final ParameterizedType<String> test2 = new ParameterizedType<>();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(test)
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test");
                container.addInstance(test2)
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test2");
            }
        });
        
        Map<String, ParameterizedType<String>> map = injector.getInstance(new TypeToken<Map<String, ParameterizedType<String>>>() {});
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("test"));
        Assert.assertTrue(map.containsKey("test2"));
        Assert.assertSame(test, map.get("test"));
        Assert.assertSame(test2, map.get("test2"));
    }
    
    @Test
    public void testMapBindingWithSameClassMultipleKeyTypes() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asMapMemberBinding()
                        .forMapType(String.class, PlainType.class)
                        .withKey("test");
                container.addImplType(PlainType.class)
                        .asMapMemberBinding()
                        .forMapType(Integer.class, PlainType.class)
                        .withKey(1);
            }
        });
        
        Map<String, PlainType> map = injector.getInstance(new TypeToken<Map<String, PlainType>>() {});
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.containsKey("test"));
        
        Map<Integer, PlainType> map2 = injector.getInstance(new TypeToken<Map<Integer, PlainType>>() {});
        Assert.assertEquals(1, map2.size());
        Assert.assertTrue(map2.containsKey(1));
    }
    
    @Test
    public void testMapBindingWithSameTypeTokenMultipleKeyTypes() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test");
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<Integer>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey(1);
            }
        });
        
        Map<String, ParameterizedType<String>> map = injector.getInstance(new TypeToken<Map<String, ParameterizedType<String>>>() {});
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.containsKey("test"));
        
        Map<Integer, ParameterizedType<String>> map2 = injector.getInstance(new TypeToken<Map<Integer, ParameterizedType<String>>>() {});
        Assert.assertEquals(1, map2.size());
        Assert.assertTrue(map2.containsKey(1));
    }
    
    @Test
    public void testMapBindingWithSameInstanceMultipleKeyTypes() throws Exception {
        final ParameterizedType<String> test = new ParameterizedType<>();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(test)
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test");
                container.addInstance(test)
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<Integer>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey(1);
            }
        });
        
        Map<String, ParameterizedType<String>> map = injector.getInstance(new TypeToken<Map<String, ParameterizedType<String>>>() {});
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.containsKey("test"));
        Assert.assertSame(test, map.get("test"));
        
        Map<Integer, ParameterizedType<String>> map2 = injector.getInstance(new TypeToken<Map<Integer, ParameterizedType<String>>>() {});
        Assert.assertEquals(1, map2.size());
        Assert.assertTrue(map2.containsKey(1));
        Assert.assertSame(test, map2.get(1));
    }
    
    @Test
    public void testMapBindingWithSameClassSingletonMultipleTimes() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(SingletonPlainType.class)
                        .asMapMemberBinding()
                        .forMapType(String.class, SingletonPlainType.class)
                        .withKey("test");
                container.addImplType(SingletonPlainType.class)
                        .asMapMemberBinding()
                        .forMapType(String.class, SingletonPlainType.class)
                        .withKey("test2");
            }
        });
        
        Map<String, SingletonPlainType> map = injector.getInstance(new TypeToken<Map<String, SingletonPlainType>>() {});
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("test"));
        Assert.assertTrue(map.containsKey("test2"));
        Assert.assertSame(map.get("test"), map.get("test2"));
    }
    
    @Test
    public void testMapBindingWithSameTypeTokenSingletonMultipleTimes() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<SingletonParameterizedType<String>>() {})
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<SingletonParameterizedType<String>>() {})
                        .withKey("test");
                container.addImplType(new TypeToken<SingletonParameterizedType<String>>() {})
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<SingletonParameterizedType<String>>() {})
                        .withKey("test2");
            }
        });
        
        Map<String, SingletonParameterizedType<String>> map = injector.getInstance(new TypeToken<Map<String, SingletonParameterizedType<String>>>() {});
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("test"));
        Assert.assertTrue(map.containsKey("test2"));
        Assert.assertSame(map.get("test"), map.get("test2"));
    }
    
    @Test
    public void testMapBindingWithClassSingleValueAndQualifier() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asMapMemberBinding()
                        .forMapType(String.class, PlainType.class)
                        .withKey("test")
                        .whenQualifiedWith(Qualifiers.named("test"));
                
                container.addImplType(QualifiedPlainTypeInjection.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Map<String, PlainType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedPlainTypeInjection obj = injector.getInstance(QualifiedPlainTypeInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        Assert.assertTrue(obj.injected.containsKey("test"));
    }
    
    @Test
    public void testMapBindingWithTypeTokenSingleValueAndQualifier() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test")
                        .whenQualifiedWith(Qualifiers.named("test"));
                
                container.addImplType(QualifiedParameterizedTypeInjection.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Map<String, ParameterizedType<String>>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedParameterizedTypeInjection obj = injector.getInstance(QualifiedParameterizedTypeInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        Assert.assertTrue(obj.injected.containsKey("test"));
    }
    
    @Test
    public void testMapBindingWithInstanceSingleValueAndQualifier() throws Exception {
        final InstanceType instance = new InstanceType();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(instance)
                        .asMapMemberBinding()
                        .forMapType(String.class, InstanceType.class)
                        .withKey("test")
                        .whenQualifiedWith(Qualifiers.named("test"));
                
                container.addImplType(QualifiedInstanceTypeInjection.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Map<String, InstanceType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedInstanceTypeInjection obj = injector.getInstance(QualifiedInstanceTypeInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        Assert.assertTrue(obj.injected.containsKey("test"));
        Assert.assertSame(instance, obj.injected.get("test"));
    }
    
    @Test
    public void testMapBindingWithClassSingleValueAndMultipleQualifiers() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asMapMemberBinding()
                        .forMapType(String.class, PlainType.class)
                        .withKey("test")
                        .whenQualifiedWith(Qualifiers.named("test"));
                container.addImplType(PlainType.class)
                        .asMapMemberBinding()
                        .forMapType(String.class, PlainType.class)
                        .withKey("test")
                        .whenQualifiedWith(Qualifiers.named("test2"));
                
                container.addImplType(QualifiedPlainTypeInjection.class).asStrictBinding().forItself();
                container.addImplType(QualifiedPlainTypeInjection2.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Map<String, PlainType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedPlainTypeInjection obj = injector.getInstance(QualifiedPlainTypeInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        Assert.assertTrue(obj.injected.containsKey("test"));
        
        QualifiedPlainTypeInjection2 obj2 = injector.getInstance(QualifiedPlainTypeInjection2.class);
        Assert.assertEquals(1, obj2.injected.size());
        Assert.assertTrue(obj2.injected.containsKey("test"));
    }
    
    @Test
    public void testMapBindingWithTypeTokenSingleValueAndMultipleQualifiers() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test")
                        .whenQualifiedWith(Qualifiers.named("test"));
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asMapMemberBinding()
                        .forMapType(new TypeToken<String>() {}, new TypeToken<ParameterizedType<String>>() {})
                        .withKey("test")
                        .whenQualifiedWith(Qualifiers.named("test2"));
                
                container.addImplType(QualifiedParameterizedTypeInjection.class).asStrictBinding().forItself();
                container.addImplType(QualifiedParameterizedTypeInjection2.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Map<String, ParameterizedType<String>>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedParameterizedTypeInjection obj = injector.getInstance(QualifiedParameterizedTypeInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        Assert.assertTrue(obj.injected.containsKey("test"));
        
        QualifiedParameterizedTypeInjection2 obj2 = injector.getInstance(QualifiedParameterizedTypeInjection2.class);
        Assert.assertEquals(1, obj2.injected.size());
        Assert.assertTrue(obj2.injected.containsKey("test"));
    }
    
    @Test
    public void testMapBindingWithInstanceSingleValueAndMultipleQualifiers() throws Exception {
        final InstanceType instance = new InstanceType();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(instance)
                        .asMapMemberBinding()
                        .forMapType(String.class, InstanceType.class)
                        .withKey("test")
                        .whenQualifiedWith(Qualifiers.named("test"));
                container.addInstance(instance)
                        .asMapMemberBinding()
                        .forMapType(String.class, InstanceType.class)
                        .withKey("test")
                        .whenQualifiedWith(Qualifiers.named("test2"));
                
                container.addImplType(QualifiedInstanceTypeInjection.class).asStrictBinding().forItself();
                container.addImplType(QualifiedInstanceTypeInjection2.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Map<String, InstanceType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedInstanceTypeInjection obj = injector.getInstance(QualifiedInstanceTypeInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        Assert.assertTrue(obj.injected.containsKey("test"));
        Assert.assertSame(instance, obj.injected.get("test"));
        
        QualifiedInstanceTypeInjection2 obj2 = injector.getInstance(QualifiedInstanceTypeInjection2.class);
        Assert.assertEquals(1, obj2.injected.size());
        Assert.assertTrue(obj2.injected.containsKey("test"));
        Assert.assertSame(instance, obj2.injected.get("test"));
    }
    
    @Test
    public void testListBindingWithClassSingleValue() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asListMemberBinding()
                        .forElementType(PlainType.class);
            }
        });
        
        List<PlainType> list = injector.getInstance(new TypeToken<List<PlainType>>() {});
        Assert.assertEquals(1, list.size());
    }
    
    @Test
    public void testListBindingWithTypeTokenSingleValue() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asListMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
            }
        });
        
        List<ParameterizedType<String>> list = injector.getInstance(new TypeToken<List<ParameterizedType<String>>>() {});
        Assert.assertEquals(1, list.size());
    }
    
    @Test
    public void testListBindingWithInstanceSingleValue() throws Exception {
        final ParameterizedType<String> test = new ParameterizedType<>();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(test)
                        .asListMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
            }
        });
        
        List<ParameterizedType<String>> list = injector.getInstance(new TypeToken<List<ParameterizedType<String>>>() {});
        Assert.assertEquals(1, list.size());
        Assert.assertSame(test, list.get(0));
    }
    
    @Test
    public void testListBindingWithClassMultipleValues() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asListMemberBinding()
                        .forElementType(PlainType.class);
                container.addImplType(PlainType.class)
                        .asListMemberBinding()
                        .forElementType(PlainType.class);
            }
        });
        
        List<PlainType> list = injector.getInstance(new TypeToken<List<PlainType>>() {});
        Assert.assertEquals(2, list.size());
        Assert.assertNotSame(list.get(0), list.get(1));
    }
    
    @Test
    public void testListBindingWithTypeTokenMultipleValues() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asListMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asListMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
            }
        });
        
        List<ParameterizedType<String>> list = injector.getInstance(new TypeToken<List<ParameterizedType<String>>>() {});
        Assert.assertEquals(2, list.size());
        Assert.assertNotSame(list.get(0), list.get(1));
    }
    
    @Test
    public void testListBindingWithInstanceMultipleValues() throws Exception {
        final ParameterizedType<String> test = new ParameterizedType<>();
        final ParameterizedType<String> test2 = new ParameterizedType<>();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(test)
                        .asListMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
                container.addInstance(test2)
                        .asListMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
            }
        });
        
        List<ParameterizedType<String>> list = injector.getInstance(new TypeToken<List<ParameterizedType<String>>>() {});
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(test));
        Assert.assertTrue(list.contains(test2));
    }
    
    @Test
    public void testListBindingWithSameClassSingletonMultipleTimes() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(SingletonPlainType.class)
                        .asListMemberBinding()
                        .forElementType(SingletonPlainType.class);
                container.addImplType(SingletonPlainType.class)
                        .asListMemberBinding()
                        .forElementType(SingletonPlainType.class);
            }
        });
        
        List<SingletonPlainType> list = injector.getInstance(new TypeToken<List<SingletonPlainType>>() {});
        Assert.assertEquals(2, list.size());
        Assert.assertSame(list.get(0), list.get(1));
    }
    
    @Test
    public void testListBindingWithSameTypeTokenSingletonMultipleTimes() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<SingletonParameterizedType<String>>() {})
                        .asListMemberBinding()
                        .forElementType(new TypeToken<SingletonParameterizedType<String>>() {});
                container.addImplType(new TypeToken<SingletonParameterizedType<String>>() {})
                        .asListMemberBinding()
                        .forElementType(new TypeToken<SingletonParameterizedType<String>>() {});
            }
        });
        
        List<SingletonParameterizedType<String>> list = injector.getInstance(new TypeToken<List<SingletonParameterizedType<String>>>() {});
        Assert.assertEquals(2, list.size());
        Assert.assertSame(list.get(0), list.get(1));
    }
    
    @Test
    public void testListBindingWithClassSingleValueAndQualifier() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asListMemberBinding()
                        .forElementType(PlainType.class)
                        .whenQualifiedWith(Qualifiers.named("test"));
                
                container.addImplType(QualifiedPlainTypeListInjection.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<List<PlainType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedPlainTypeListInjection obj = injector.getInstance(QualifiedPlainTypeListInjection.class);
        Assert.assertEquals(1, obj.injected.size());
    }
    
    @Test
    public void testListBindingWithTypeTokenSingleValueAndQualifier() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asListMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {})
                        .whenQualifiedWith(Qualifiers.named("test"));
                
                container.addImplType(QualifiedParameterizedTypeListInjection.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<List<ParameterizedType<String>>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedParameterizedTypeListInjection obj = injector.getInstance(QualifiedParameterizedTypeListInjection.class);
        Assert.assertEquals(1, obj.injected.size());
    }
    
    @Test
    public void testListBindingWithInstanceSingleValueAndQualifier() throws Exception {
        final InstanceType instance = new InstanceType();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(instance)
                        .asListMemberBinding()
                        .forElementType(InstanceType.class)
                        .whenQualifiedWith(Qualifiers.named("test"));
                
                container.addImplType(QualifiedInstanceTypeListInjection.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<List<InstanceType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedInstanceTypeListInjection obj = injector.getInstance(QualifiedInstanceTypeListInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        Assert.assertSame(instance, obj.injected.get(0));
    }
    
    @Test
    public void testListBindingWithClassSingleValueAndMultipleQualifiers() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asListMemberBinding()
                        .forElementType(PlainType.class)
                        .whenQualifiedWith(Qualifiers.named("test"));
                container.addImplType(PlainType.class)
                        .asListMemberBinding()
                        .forElementType(PlainType.class)
                        .whenQualifiedWith(Qualifiers.named("test2"));
                
                container.addImplType(QualifiedPlainTypeListInjection.class).asStrictBinding().forItself();
                container.addImplType(QualifiedPlainTypeListInjection2.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<List<PlainType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedPlainTypeListInjection obj = injector.getInstance(QualifiedPlainTypeListInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        
        QualifiedPlainTypeListInjection2 obj2 = injector.getInstance(QualifiedPlainTypeListInjection2.class);
        Assert.assertEquals(1, obj2.injected.size());
    }
    
    @Test
    public void testListBindingWithTypeTokenSingleValueAndMultipleQualifiers() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asListMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {})
                        .whenQualifiedWith(Qualifiers.named("test"));
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asListMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {})
                        .whenQualifiedWith(Qualifiers.named("test2"));
                
                container.addImplType(QualifiedParameterizedTypeListInjection.class).asStrictBinding().forItself();
                container.addImplType(QualifiedParameterizedTypeListInjection2.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<List<ParameterizedType<String>>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedParameterizedTypeListInjection obj = injector.getInstance(QualifiedParameterizedTypeListInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        
        QualifiedParameterizedTypeListInjection2 obj2 = injector.getInstance(QualifiedParameterizedTypeListInjection2.class);
        Assert.assertEquals(1, obj2.injected.size());
    }
    
    @Test
    public void testListBindingWithInstanceSingleValueAndMultipleQualifiers() throws Exception {
        final InstanceType instance = new InstanceType();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(instance)
                        .asListMemberBinding()
                        .forElementType(InstanceType.class)
                        .whenQualifiedWith(Qualifiers.named("test"));
                container.addInstance(instance)
                        .asListMemberBinding()
                        .forElementType(InstanceType.class)
                        .whenQualifiedWith(Qualifiers.named("test2"));
                
                container.addImplType(QualifiedInstanceTypeListInjection.class).asStrictBinding().forItself();
                container.addImplType(QualifiedInstanceTypeListInjection2.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<List<InstanceType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedInstanceTypeListInjection obj = injector.getInstance(QualifiedInstanceTypeListInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        Assert.assertTrue(obj.injected.contains(instance));
        
        QualifiedInstanceTypeListInjection2 obj2 = injector.getInstance(QualifiedInstanceTypeListInjection2.class);
        Assert.assertEquals(1, obj2.injected.size());
        Assert.assertTrue(obj2.injected.contains(instance));
    }
    
    @Test
    public void testSetBindingWithClassSingleValue() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asSetMemberBinding()
                        .forElementType(PlainType.class);
            }
        });
        
        Set<PlainType> set = injector.getInstance(new TypeToken<Set<PlainType>>() {});
        Assert.assertEquals(1, set.size());
    }
    
    @Test
    public void testSetBindingWithTypeTokenSingleValue() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
            }
        });
        
        Set<ParameterizedType<String>> set = injector.getInstance(new TypeToken<Set<ParameterizedType<String>>>() {});
        Assert.assertEquals(1, set.size());
    }
    
    @Test
    public void testSetBindingWithInstanceSingleValue() throws Exception {
        final ParameterizedType<String> test = new ParameterizedType<>();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(test)
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
            }
        });
        
        Set<ParameterizedType<String>> set = injector.getInstance(new TypeToken<Set<ParameterizedType<String>>>() {});
        Assert.assertEquals(1, set.size());
        Assert.assertSame(test, set.iterator().next());
    }
    
    @Test
    public void testSetBindingWithClassMultipleValues() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asSetMemberBinding()
                        .forElementType(PlainType.class);
                container.addImplType(PlainType.class)
                        .asSetMemberBinding()
                        .forElementType(PlainType.class);
            }
        });
        
        Set<PlainType> set = injector.getInstance(new TypeToken<Set<PlainType>>() {});
        Assert.assertEquals(2, set.size());
    }
    
    @Test
    public void testSetBindingWithTypeTokenMultipleValues() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
            }
        });
        
        Set<ParameterizedType<String>> set = injector.getInstance(new TypeToken<Set<ParameterizedType<String>>>() {});
        Assert.assertEquals(2, set.size());
    }
    
    @Test
    public void testSetBindingWithInstanceMultipleValues() throws Exception {
        final ParameterizedType<String> test = new ParameterizedType<>();
        final ParameterizedType<String> test2 = new ParameterizedType<>();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(test)
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
                container.addInstance(test2)
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {});
            }
        });
        
        Set<ParameterizedType<String>> set = injector.getInstance(new TypeToken<Set<ParameterizedType<String>>>() {});
        Assert.assertEquals(2, set.size());
        Assert.assertTrue(set.contains(test));
        Assert.assertTrue(set.contains(test2));
    }
    
    @Test
    public void testSetBindingWithSameClassSingletonMultipleTimes() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(SingletonPlainType.class)
                        .asSetMemberBinding()
                        .forElementType(SingletonPlainType.class);
                container.addImplType(SingletonPlainType.class)
                        .asSetMemberBinding()
                        .forElementType(SingletonPlainType.class);
            }
        });
        
        Set<SingletonPlainType> set = injector.getInstance(new TypeToken<Set<SingletonPlainType>>() {});
        Assert.assertEquals(1, set.size()); // element added twice but is the same element in set
    }
    
    @Test
    public void testSetBindingWithSameTypeTokenSingletonMultipleTimes() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<SingletonParameterizedType<String>>() {})
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<SingletonParameterizedType<String>>() {});
                container.addImplType(new TypeToken<SingletonParameterizedType<String>>() {})
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<SingletonParameterizedType<String>>() {});
            }
        });
        
        Set<SingletonParameterizedType<String>> set = injector.getInstance(new TypeToken<Set<SingletonParameterizedType<String>>>() {});
        Assert.assertEquals(1, set.size()); // element added twice but is the same element in set
    }
    
    @Test
    public void testSetBindingWithClassSingleValueAndQualifier() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asSetMemberBinding()
                        .forElementType(PlainType.class)
                        .whenQualifiedWith(Qualifiers.named("test"));
                
                container.addImplType(QualifiedPlainTypeSetInjection.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Set<PlainType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedPlainTypeSetInjection obj = injector.getInstance(QualifiedPlainTypeSetInjection.class);
        Assert.assertEquals(1, obj.injected.size());
    }
    
    @Test
    public void testSetBindingWithTypeTokenSingleValueAndQualifier() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {})
                        .whenQualifiedWith(Qualifiers.named("test"));
                
                container.addImplType(QualifiedParameterizedTypeSetInjection.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Set<ParameterizedType<String>>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedParameterizedTypeSetInjection obj = injector.getInstance(QualifiedParameterizedTypeSetInjection.class);
        Assert.assertEquals(1, obj.injected.size());
    }
    
    @Test
    public void testSetBindingWithInstanceSingleValueAndQualifier() throws Exception {
        final InstanceType instance = new InstanceType();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(instance)
                        .asSetMemberBinding()
                        .forElementType(InstanceType.class)
                        .whenQualifiedWith(Qualifiers.named("test"));
                
                container.addImplType(QualifiedInstanceTypeSetInjection.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Set<InstanceType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedInstanceTypeSetInjection obj = injector.getInstance(QualifiedInstanceTypeSetInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        Assert.assertSame(instance, obj.injected.iterator().next());
    }
    
    @Test
    public void testSetBindingWithClassSingleValueAndMultipleQualifiers() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(PlainType.class)
                        .asSetMemberBinding()
                        .forElementType(PlainType.class)
                        .whenQualifiedWith(Qualifiers.named("test"));
                container.addImplType(PlainType.class)
                        .asSetMemberBinding()
                        .forElementType(PlainType.class)
                        .whenQualifiedWith(Qualifiers.named("test2"));
                
                container.addImplType(QualifiedPlainTypeSetInjection.class).asStrictBinding().forItself();
                container.addImplType(QualifiedPlainTypeSetInjection2.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Set<PlainType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedPlainTypeSetInjection obj = injector.getInstance(QualifiedPlainTypeSetInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        
        QualifiedPlainTypeSetInjection2 obj2 = injector.getInstance(QualifiedPlainTypeSetInjection2.class);
        Assert.assertEquals(1, obj2.injected.size());
    }
    
    @Test
    public void testSetBindingWithTypeTokenSingleValueAndMultipleQualifiers() throws Exception {
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {})
                        .whenQualifiedWith(Qualifiers.named("test"));
                container.addImplType(new TypeToken<ParameterizedType<String>>() {})
                        .asSetMemberBinding()
                        .forElementType(new TypeToken<ParameterizedType<String>>() {})
                        .whenQualifiedWith(Qualifiers.named("test2"));
                
                container.addImplType(QualifiedParameterizedTypeSetInjection.class).asStrictBinding().forItself();
                container.addImplType(QualifiedParameterizedTypeSetInjection2.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Set<ParameterizedType<String>>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedParameterizedTypeSetInjection obj = injector.getInstance(QualifiedParameterizedTypeSetInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        
        QualifiedParameterizedTypeSetInjection2 obj2 = injector.getInstance(QualifiedParameterizedTypeSetInjection2.class);
        Assert.assertEquals(1, obj2.injected.size());
    }
    
    @Test
    public void testSetBindingWithInstanceSingleValueAndMultipleQualifiers() throws Exception {
        final InstanceType instance = new InstanceType();
        Injector injector = Hammer.createInjector(new Loader() {
            @Override
            public void load(Container container) {
                container.addInstance(instance)
                        .asSetMemberBinding()
                        .forElementType(InstanceType.class)
                        .whenQualifiedWith(Qualifiers.named("test"));
                container.addInstance(instance)
                        .asSetMemberBinding()
                        .forElementType(InstanceType.class)
                        .whenQualifiedWith(Qualifiers.named("test2"));
                
                container.addImplType(QualifiedInstanceTypeSetInjection.class).asStrictBinding().forItself();
                container.addImplType(QualifiedInstanceTypeSetInjection2.class).asStrictBinding().forItself();
            }
        });
        
        try {
            injector.getInstance(new TypeToken<Set<InstanceType>>() {});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
        
        QualifiedInstanceTypeSetInjection obj = injector.getInstance(QualifiedInstanceTypeSetInjection.class);
        Assert.assertEquals(1, obj.injected.size());
        Assert.assertTrue(obj.injected.contains(instance));
        
        QualifiedInstanceTypeSetInjection2 obj2 = injector.getInstance(QualifiedInstanceTypeSetInjection2.class);
        Assert.assertEquals(1, obj2.injected.size());
        Assert.assertTrue(obj2.injected.contains(instance));
    }

}
