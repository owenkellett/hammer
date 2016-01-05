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
import java.util.Set;

import javax.inject.Provider;

/**
 * An {@link Injector} is a JSR-330 compliant dependency injection tool.
 * {@link Injector}s support all standard JSR-330 dependency injection
 * techniques.
 */
public interface Injector {

    /**
     * Retrieve an instance of the target type from the container.  This method may
     * create a new instance or return an existing instance of the requested type
     * depending on the configured {@code Scope} of the type as well as the contextual
     * active {@code Scope}s of this {@link Injector} instance.
     * 
     * @param <T> the type of the returned object
     * @param target the target class type of the returned object
     * @return a fully instantiated instance of the requested type with all configured
     *         dependencies injected according to the rules of JSR-330 annotations
     */
    <T> T getInstance(Class<T> target);
    
    /**
     * Retrieve an instance of the target type from the container.  This method may
     * create a new instance or return an existing instance of the requested type
     * depending on the configured {@code Scope} of the type as well as the contextual
     * active {@code Scope}s of this {@link Injector} instance.
     * 
     * @param <T> the type of the returned object
     * @param target the target class type of the returned object
     * @return a fully instantiated instance of the requested type with all configured
     *         dependencies injected according to the rules of JSR-330 annotations
     */
    <T> T getInstance(TypeToken<T> target);
    
    /**
     * Retrieve an implementation of a {@code Provider} that will retrieve instances of
     * the target type from the container.  A call to {@code get} on the returned
     * {@code Provider} is equivalent to a call to {@code getInstance} on this
     * {@link Injector} instance.
     * 
     * @param <T> the type of the provided objects
     * @param target the target class type of the objects returned by the provider
     * @return a JSR-330 compliant {@code Provider} of instances of the requested type
     */
    <T> Provider<T> getProvider(Class<T> target);
    
    /**
     * Retrieve an implementation of a {@code Provider} that will retrieve instances of
     * the target type from the container.  A call to {@code get} on the returned
     * {@code Provider} is equivalent to a call to {@code getInstance} on this
     * {@link Injector} instance.
     * 
     * @param <T> the type of the provided objects
     * @param target the target class type of the objects returned by the provider
     * @return a JSR-330 compliant {@code Provider} of instances of the requested type
     */
    <T> Provider<T> getProvider(TypeToken<T> target);
    
    /**
     * The set of {@code @Scope}s that this {@link Injector} instance honors.  For every
     * {@code @Scope} annotation in this set, all injections by this {@link Injector} for
     * a type with said annotation will have one of two behaviors:
     * <ol>
     * <li>If the {@code Scope} annotation is also annotated with {@link Multiton}, all
     *     injections by this injector for a type annotated will return a unique instance
     *     <em>per {@code Qualifier}</em> annotation associated with the injection
     *     request.  Thus two requests for the type with the same {@code Qualifier} will
     *     inject the same instance, but two requests for the type with different
     *     {@code Qualifiers} will return different instances.</li>
     * <li>If the {@code Scope} annotation is NOT also annotated with {@link Multiton},
     *     all injections by this injector for a type annotated with the scope will
     *     inject the same instance (behaves as a {@code @Singleton}).</li>
     * </ol>
     * As a rule, this method always returns a set of at least size two where
     * {@code @Singleton} and {@link Multiton} are always included.
     * 
     * @return the {@code Scope}s active for this {@link Injector}
     */
    Set<Annotation> getActiveScopes();
    
    /**
     * Injects injectable member fields and methods of the given object according to the
     * injection rules as specified by the JSR-330 specification.  This method 
     * will always override any existing value for any injectable member field.  This 
     * method will also always call every injectable method on the given target object 
     * with the appropriate parameters.  This method will be
     * called automatically for any instance created by this {@link Injector} if member 
     * {@link InjectionType}s are configured.  Thus in most cases, it is only necessary to 
     * use for objects that you instantiate yourself.
     * <p>
     * Note: If this {@link Injector} is not configured for either public or non public
     * member {@link InjectionType}s, this method will silently have no effect.
     * 
     * @param target the object to inject
     * @throws IllegalArgumentException if the target object is of a type unknown to the
     *                                  injector
     */
    void injectMembers(Object target);
    
    /**
     * Injects injectable static fields and methods of the given class according to the
     * injection rules as specified by the JSR-330 specification.  This method will always
     * override any existing value for any injectable static field.  This 
     * method will also always call every injectable method on the given target object 
     * with the appropriate parameters.  This method will be
     * called automatically for any class that had static injections requested during
     * {@link Injector} creation time if static field {@link InjectionType}s are
     * configured.  Thus in most cases, it is only necessary to use for objects that you
     * instantiate yourself.
     * <p>
     * Note: If this {@link Injector} is not configured for either public or non public
     * static {@link InjectionType}s, this method will silently have no effect.
     * 
     * @param targetClass the class type to inject
     * @throws IllegalArgumentException if the target object is of a type unknown to the
     *                                  injector
     */
    void injectStatics(Class<?> targetClass);
    
    /**
     * Indicates whether or not the given injection type is supported by this
     * {@link Injector} instance.
     * 
     * @param type the type of injection
     * @return {@code true} if this Injector supports the given injection type
     */
    boolean isSupported(InjectionType type);
    
    /**
     * Create a new child {@link Injector} with the given scope activated.  The
     * resulting child {@link Injector} will delegate all injection requests to the
     * parent injector with the exception of requests with the given scope.
     * 
     * @param scope the new scope to activate in the new {@link Injector}
     * @return a new {@link Injector} with the given scope activated
     * @throws IllegalArgumentException if the requested scope annotation is not annotated
     *                                  with {@code @Scope}
     */
    Injector enterScope(Class<? extends Annotation> scope);
    
}
