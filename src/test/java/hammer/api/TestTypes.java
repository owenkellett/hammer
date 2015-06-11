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

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.junit.Test;

/**
 *
 */
public class TestTypes {
    
    @Test
    public void printTypes() throws Exception {
        DummyOne<String> dummy = new DummyOne<>();
        
        Class<?> c = dummy.getClass();
        
        /*printFieldType(c, "primitive");
        printFieldType(c, "arrayPrimitive");
        printFieldType(c, "plainClass");
        printFieldType(c, "arrayPlainClass");
        printFieldType(c, "typeVariable");
        printFieldType(c, "parameterizedTypeVariable");
        printFieldType(c, "parameterizedPlainClass");
        printFieldType(c, "parameterizedWildcard");
        printFieldType(c, "parameterizedBoundedWildcard");
        printFieldType(c, "parameterizedMultiples");*/
    }
    
    private static void printFieldType(Class<?> c, String name) throws Exception {
        Field field = c.getDeclaredField(name);
        Type type = field.getGenericType();
        System.out.println(name + " - " + type + " (" + type.getClass().getName() + ")");
    }

    private static class DummyOne<T> {
        
        public int primitive;
        
        public int[] arrayPrimitive;
        
        public String plainClass;
        
        public String[] arrayPlainClass;
        
        public T typeVariable;
        
        public DummyOne<T> parameterizedTypeVariable;
        
        public DummyOne<String> parameterizedPlainClass;
        
        public DummyOne<?> parameterizedWildcard;
        
        public DummyOne<? extends String> parameterizedBoundedWildcard;
        
        public DummyTwo<String, Integer> parameterizedMultiples;
        
    }
    
    private static class DummyTwo<A, B> {
        
    }
}
