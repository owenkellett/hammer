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

import javax.inject.Scope;
import javax.inject.Singleton;

/**
 * Utility class used to work with {@link Scope} annotations.
 */
public class Scopes {

    /**
     * Do not allow instantiation.
     */
    private Scopes() {}
    
    /**
     * An instance of the {@link Singleton} scope.
     */
    public static Singleton SINGLETON = new SingletonImpl();
    
    /**
     * Creates an instance of an annotation that is annotated with {@link Scope}.
     * This is a convenience method and can only be used for simple annotations that do
     * not declare any elements.
     * 
     * @param <S> the scope annotation
     * @param scope the class of the scope annotation
     * @return an instance of the specified scope annotation
     */
    public static <S extends Annotation> S scope(final Class<S> scope) {
        if (scope.getAnnotation(Scope.class) == null) {
            throw new IllegalArgumentException("scope must be annotated with @Scope");
        }
        
        final SimpleAnnotation simple = new SimpleAnnotation(scope);
        
        return (S) Proxy.newProxyInstance(
                scope.getClassLoader(),
                new Class[] { scope },
                new InvocationHandler() {
                    @Override public Object invoke(
                            Object proxy, Method method, Object[] args) throws Exception {
                        return method.invoke(simple, args);
                    }
                });
    }
    
    static class SimpleAnnotation implements Annotation {
        private final Class<? extends Annotation> annotationType;
        
        SimpleAnnotation(Class<? extends Annotation> annotationType) {
            // ensure that the annotation type has no methods
            if (annotationType.getDeclaredMethods().length > 0) {
                throw new IllegalArgumentException(
                        "annotation type must be a simple annotation with no elements");
            }
            this.annotationType = annotationType;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return annotationType;
        }

        @Override
        public String toString() {
            return "@" + annotationType.getName();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Annotation)) {
                return false;
            }
            
            return annotationType().equals(((Annotation)obj).annotationType());
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
    
    private static class SingletonImpl extends SimpleAnnotation implements Singleton {
        private SingletonImpl() {
            super(Singleton.class);
        }
    }
}
