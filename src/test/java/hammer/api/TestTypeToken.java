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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class TestTypeToken {
    
    @Test
    public void testRawToken() throws Exception {
        try {
            TypeToken t = new TypeToken() {};
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Missing type parameter", iae.getMessage());
        }
    }
    
    @Test(expected=IllegalArgumentException.class)
    public <T> void testVariableToken() throws Exception {
        TypeToken<T> t = new TypeToken<T>() {};
    }
    
    @Test(expected=IllegalArgumentException.class)
    public <T> void testParameterizedVariableToken() throws Exception {
        TypeToken<Set<T>> t = new TypeToken<Set<T>>() {};
    }
    
    @Test(expected=IllegalArgumentException.class)
    public <T> void testNestedParameterizedVariableToken() throws Exception {
        TypeToken<Set<Set<T>>> t = new TypeToken<Set<Set<T>>>() {};
    }
    
    @Test(expected=IllegalArgumentException.class)
    public <T> void testArrayVariableToken() throws Exception {
        TypeToken<T[]> t = new TypeToken<T[]>() {};
    }
    
    @Test(expected=IllegalArgumentException.class)
    public <T> void testInnerClassToken() throws Exception {
        TypeToken<InnerClass> t = new TypeToken<InnerClass>() {};
    }
    
    @Test(expected=IllegalArgumentException.class)
    public <T> void testLocalClassToken() throws Exception {
        class LocalClass {}
        TypeToken<LocalClass> t = new TypeToken<LocalClass>() {};
    }
    
    @Test(expected=IllegalArgumentException.class)
    public <T> void testAnonymousClassToken() throws Exception {
        TypeToken<?> t = TypeToken.forClass((new Object() {}).getClass());
    }
    
    @Test
    public void testNestedClassToken() throws Exception {
        TypeToken<NestedClass> t = new TypeToken<NestedClass>() {};
        
        Assert.assertEquals("class hammer.api.TestTypeToken$NestedClass", t.getType().toString());
        Assert.assertEquals(NestedClass.class, t.getType());
        Assert.assertEquals(NestedClass.class, t.getRawClass());
    }
    
    @Test
    public void testParameterizedWildcardToken() throws Exception {
        TypeToken<Set<?>> t = new TypeToken<Set<?>>() {};
        
        Assert.assertEquals("java.util.Set<?>", t.getType().toString());
        Assert.assertTrue(t.getType() instanceof ParameterizedType);
        Assert.assertEquals(Set.class, ((ParameterizedType)t.getType()).getRawType());
        Assert.assertEquals(1, ((ParameterizedType)t.getType()).getActualTypeArguments().length);
        Assert.assertTrue(((ParameterizedType) t.getType()).getActualTypeArguments()[0] instanceof WildcardType);
        Assert.assertEquals(Set.class, t.getRawClass());
    }
    
    @Test
    public void testParameterizedTypeToken() throws Exception {
        TypeToken<Set<Integer>> t = new TypeToken<Set<Integer>>() {};
        
        Assert.assertEquals("java.util.Set<java.lang.Integer>", t.getType().toString());
        Assert.assertTrue(t.getType() instanceof ParameterizedType);
        Assert.assertEquals(Set.class, ((ParameterizedType)t.getType()).getRawType());
        Assert.assertEquals(1, ((ParameterizedType)t.getType()).getActualTypeArguments().length);
        Assert.assertEquals(Integer.class, ((ParameterizedType)t.getType()).getActualTypeArguments()[0]);
        Assert.assertEquals(Set.class, t.getRawClass());
    }
    
    @Test
    public void testNonParameterizedToken() throws Exception {
        TypeToken<String> t = new TypeToken<String>() {};
        
        Assert.assertEquals("class java.lang.String", t.getType().toString());
        Assert.assertEquals(String.class, t.getType());
        Assert.assertEquals(String.class, t.getRawClass());
    }
    
    @Test
    public void testPrimitiveArrayToken() throws Exception {
        TypeToken<int[]> t = new TypeToken<int[]>() {};
        
        Assert.assertEquals("class [I", t.getType().toString());
        Assert.assertEquals(int[].class, t.getType());
        Assert.assertEquals(int[].class, t.getRawClass());
    }
    
    @Test
    public void testGenericArrayToken() throws Exception {
        TypeToken<Set<Integer>[]> t = new TypeToken<Set<Integer>[]>() {};
        
        Assert.assertEquals("java.util.Set<java.lang.Integer>[]", t.getType().toString());
        Assert.assertTrue(t.getType() instanceof GenericArrayType);
        Assert.assertTrue(((GenericArrayType)t.getType()).getGenericComponentType() instanceof ParameterizedType);
    }
    
    @Test
    public void testClassTypeToken() throws Exception {
        TypeToken<String> t = TypeToken.forClass(String.class);
        
        Assert.assertEquals("class java.lang.String", t.getType().toString());
        Assert.assertEquals(String.class, t.getType());
        Assert.assertEquals(String.class, t.getRawClass());
    }
    
    
    public static class NestedClass {}
    public class InnerClass {}

}
