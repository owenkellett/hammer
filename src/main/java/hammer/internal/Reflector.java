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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import hammer.api.InjectionException;

/**
 * Utility methods used to perform the actual injection of fields, methods, and
 * constructors via reflection.
 */
class Reflector {

    static <T> T invokeConstructor(Constructor<T> ctr, Object... args) {
        try {
            return ctr.newInstance(args);
        } catch (IllegalAccessException|InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException ite) {
            throw new InjectionException("Exception while creating object", ite);
        }
    }
    
    static Object invokeMethod(Method method, Object obj, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        } catch (InvocationTargetException ex) {
            throw new InjectionException("Exception while injecting method", ex);
        }
    }
    
    static void setField(Field field, Object obj, Object value) {
        try {
            field.set(obj, value);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
