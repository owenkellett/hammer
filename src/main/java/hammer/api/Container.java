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

/**
 * A {@link Container} represents a partially constructed configuration that will
 * eventually be used to build a new {@link Injector}.  A {@link Container} is loaded by a
 * collection of {@link Loader}s that are pulled together during {@link Injector} creation
 * time.
 */
public interface Container {
    
    /**
     * Add an implementation type to be used as an injectable and constructable class type
     * for this container.
     * 
     * @param <T> the type
     * @param type the type represented as a Class
     * @return a {@link BindingInvocation} handle that can be used to further
     *         configure the bound type
     */
    <T> BindingInvocation<T> addImplType(Class<T> type);

    /**
     * Add an implementation type to be used as an injectable and constructable class
     * type for this container.
     * 
     * @param <T> the type
     * @param type the type represented as a TypeToken
     * @return a {@link BindingInvocation} handle that can be used to further
     *         configure the bound type
     */
    <T> BindingInvocation<T> addImplType(TypeToken<T> type);
    
    /**
     * Add a fully constructed instance to be used as an injectable entity for this
     * container.
     * 
     * @param <T> the type
     * @param instance the object of the above type
     * @return a {@link BindingInvocation} handle that can be used to further configure
     *         the bound instance
     */
    <T> BindingInvocation<T> addInstance(T instance);
    
    /**
     * Restrict the booted {@link Injector} to only support the given types of
     * {@link InjectionType}.  By default, if this method is not called, all injection
     * types are supported.  However, if this method is called at least once or multiple
     * times, the supported collection of {@link InjectionType}s are equal to the union
     * of all types provided in all calls.
     * 
     * @param injections the injection types to support
     */
    void allowInjections(InjectionType... injections);
    
    /**
     * Request that static field injection be performed on the given type upon
     * construction of the {@link Injector} container.
     * 
     * @param type the type to inject static fields for
     */
    void configureStaticInjections(Class<?> type);
    
    /**
     * A {@link BindingInvocation} is a configuration entity used to configure the
     * binding of an implementation type or instance that has been added to a
     * {@link Container}.
     * 
     * @param <T> the type that is being configured
     */
    public interface BindingInvocation<T> {
        
        /**
         * Configure this type as a standard strict type binding.
         * 
         * @return a {@link StrictBinder} used to further configure the binding
         */
        StrictBinder<T> asStrictBinding();
        
    }

    /**
     * A {@link StrictBinder} is a configuration entity used to used to configure the
     * binding of an implementation type or instance that has been added to a
     * {@link Container} and bind it to a specific injectable type or set of injectable
     * types.
     * 
     * @param <T> the type to configure
     */
    public interface StrictBinder<T> {
        
        /**
         * Binds the configured implementation type or instance to injection requests for
         * its own concrete type.  This is the default binding.
         * 
         * @return an {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forItself();
        
        /**
         * Binds the configured implementation type or instance to injection requests for
         * the given specific injection types.
         * 
         * @param types the types to bind
         * @return an {@link QualifierBinder} used to further configure the binding
         * @throws IllegalArgumentException if a supplied type conflicts with a previously
         *                                  bound type and multi bindings are NOT
         *                                  configured for that type
         */
        QualifierBinder forSpecificTypes(Class<? super T>... types);
        
        /**
         * Binds the configured implementation type or instance to injection requests for
         * the given specific injection types.
         * 
         * @param types the types to bind
         * @return an {@link QualifierBinder} used to further configure the binding
         * @throws IllegalArgumentException if a supplied type conflicts with a previously
         *                                  bound type and multi bindings are NOT
         *                                  configured for that type
         */
        QualifierBinder forSpecificTypes(TypeToken<? super T>... types);

    }
        
    /**
     * An {@link QualifierBinder} is a configuration entity used to used to configure the
     * binding of an implementation type or instance that has been added to a
     * {@link Container} and bind only to injection requests annotated with a
     * specific {@code Qualifier} annotation.
     */
    public interface QualifierBinder {
        
        /**
         * Binds the configured implementation type or instance to a specific qualifier.
         * By default a type or instance is automatically bound to a qualifier that its
         * class is annotated with.  This method can be used if it is desirable to
         * override that value or set one if there is none.  Note that this method binds
         * the configured entity to a specific <em>instance</em> of a specific Qualifier
         * annotation.
         * 
         * @param <Q> the qualifier annotation type
         * @param qualifier the qualifier value
         */
        <Q extends Annotation> void whenQualifiedWith(Q qualifier);
        
    }

}
