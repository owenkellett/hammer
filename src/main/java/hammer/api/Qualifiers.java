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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import javax.inject.Named;
import javax.inject.Qualifier;

/**
 * Utility class used to work with {@link Qualifier} annotations.
 */
public class Qualifiers {
    
    /**
     * Do not allow instantiation.
     */
    private Qualifiers() {}
    
    /**
     * Creates an instance of a {@link Named} annotation with the given name as the value.
     * 
     * @param name the value to use for the instance
     * @return an instance of {@link Named} that has the given name as a value
     */
    public static Named named(final String name) {
        return new Named() {

            @Override
            public String value() {
                return name;
            }
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Named.class;
            }
      
            @Override
            public String toString() {
                return "@" + Named.class.getName() + "(value=" + value() + ")";
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null || !(obj instanceof Named)) {
                    return false;
                }
                
                return value().equals(((Named)obj).value());
            }

            @Override
            public int hashCode() {
                return (127 * "value".hashCode()) ^ value().hashCode();
            }
            
        };
    }
    
    /**
     * Creates an instance of an annotation that is annotated with {@link Qualifier}.
     * This is a convenience method and can only be used for simple annotations that do
     * not declare any elements.
     * 
     * @param <Q> the qualifier annotation
     * @param qualifier the class of the qualifier annotation
     * @return an instance of the specified qualifier annotation
     */
    public static <Q extends Annotation> Q qualifier(final Class<Q> qualifier) {
        if (qualifier.getAnnotation(Qualifier.class) == null) {
            throw new IllegalArgumentException(
                    "qualifier must be annotated with @Qualifier");
        }
        
        final Scopes.SimpleAnnotation simple = new Scopes.SimpleAnnotation(qualifier);
        
        return (Q) Proxy.newProxyInstance(
                qualifier.getClassLoader(),
                new Class[] { qualifier },
                new InvocationHandler() {
                    @Override public Object invoke(
                            Object proxy, Method method, Object[] args) throws Exception {
                        return method.invoke(simple, args);
                    }
                });
    }
}
