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
     * Configure the given scopes to be activate in the booted {@link Injector}.  Note
     * that this method is additive - the resulting {@link Injector}'s active scopes will
     * be the union of all scopes provided in all calls to this method in addition to
     * both the {@code Singleton} and {@link Multiton} scopes.
     * 
     * @param scopes the scopes to activate
     */
    void activateScopes(Class<? extends Annotation>... scopes);
    
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
        
        /**
         * Configure this type as a binding to a member of an injectable {@code Map}
         * that is not bound to any scope.  The resulting injectable {@code Map} type
         * will be treated as if the instantiated type is not annotated with any scope.
         * <p>
         * This call is equivalent to the call 
         * {@link #asMapMemberBinding(java.lang.annotation.Annotation)
         * asMapMemberBinding(null)}
         * 
         * @return a {@link MapMemberBinder} used to further configure the binding
         */
        MapMemberBinder<T> asMapMemberBinding();
        
        /**
         * Configure this type as a binding to a member of an injectable {@code Map}
         * that is bound to the given scope.  The will be treated as if  the instantiated
         * type is annotated with the given scope.
         * 
         * @param scope the scope to bind the map type to
         * @return a {@link MapMemberBinder} used to further configure the binding
         */
        MapMemberBinder<T> asMapMemberBinding(Annotation scope);
        
        /**
         * Configure this type as a binding to a member of an injectable {@code List}
         * that is not bound to any scope.  The resulting injectable {@code List} type
         * will be treated as if the instantiated type is not annotated with any scope.
         * <p>
         * This call is equivalent to the call 
         * {@link #asListMemberBinding(java.lang.annotation.Annotation)
         * asListMemberBinding(null)}
         * 
         * @return a {@link ListMemberBinder} used to further configure the binding
         */
        ListMemberBinder<T> asListMemberBinding();
        
        /**
         * Configure this type as a binding to a member of an injectable {@code List}
         * that is bound to the given scope.  The will be treated as if  the instantiated
         * type is annotated with the given scope.
         * 
         * @param scope the scope to bind the map type to
         * @return a {@link ListMemberBinder} used to further configure the binding
         */
        ListMemberBinder<T> asListMemberBinding(Annotation scope);
        
        /**
         * Configure this type as a binding to a member of an injectable {@code Set}
         * that is not bound to any scope.  The resulting injectable {@code Set} type
         * will be treated as if the instantiated type is not annotated with any scope.
         * <p>
         * This call is equivalent to the call 
         * {@link #asSetMemberBinding(java.lang.annotation.Annotation)
         * asSetMemberBinding(null)}
         * 
         * @return a {@link SetMemberBinder} used to further configure the binding
         */
        SetMemberBinder<T> asSetMemberBinding();
        
        /**
         * Configure this type as a binding to a member of an injectable {@code Set}
         * that is bound to the given scope.  The will be treated as if  the instantiated
         * type is annotated with the given scope.
         * 
         * @param scope the scope to bind the map type to
         * @return a {@link SetMemberBinder} used to further configure the binding
         */
        SetMemberBinder<T> asSetMemberBinding(Annotation scope);
        
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
         * @return a {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forItself();
        
        /**
         * Binds the configured implementation type or instance to injection requests for
         * the given specific injection types.
         * 
         * @param types the types to bind
         * @return a {@link QualifierBinder} used to further configure the binding
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
         * @return a {@link QualifierBinder} used to further configure the binding
         * @throws IllegalArgumentException if a supplied type conflicts with a previously
         *                                  bound type and multi bindings are NOT
         *                                  configured for that type
         */
        QualifierBinder forSpecificTypes(TypeToken<? super T>... types);

    }
    
    /**
     * A {@link MapMemberBinder} is a configuration entity used to used to configure the
     * binding of an implementation type or instance that has been added to a
     * {@link Container} and bind it as a value in an injectable {@code Map}.
     * 
     * @param <V> the type to configure
     */
    public interface MapMemberBinder<V> {
        
        /**
         * Configures the injectable map type to use for the binding.
         * 
         * @param <K> the type of the key
         * @param keyType the type of the key
         * @param valueType the type of the value
         * @return a {@link MapMemberKeyBinder} used to further configure the binding
         */
        <K> MapMemberKeyBinder<K> forMapType(Class<K> keyType, 
                                             Class<? super V> valueType);
        
        /**
         * Configures the injectable map type to use for the binding.
         * 
         * @param <K> the type of the key
         * @param keyType the type of the key
         * @param valueType the type of the value
         * @return a {@link MapMemberKeyBinder} used to further configure the binding
         */
        <K> MapMemberKeyBinder<K> forMapType(TypeToken<K> keyType, 
                                             TypeToken<? super V> valueType);
        
    }
    
    /**
     * A {@link MapMemberKeyBinder} is a configuration entity used to used to configure
     * the key to use for a specific implementation type or instance that has been added
     * to a {@link Container} as a value in an injectable {@code Map}.
     * 
     * @param <K> the key type to configure
     */
    public interface MapMemberKeyBinder<K> {
        
        /**
         * Binds the configured implementation type or instance to the map with the
         * specific key value.
         * 
         * @param key the key value to use
         * @return a {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder withKey(K key);
        
    }
    
    /**
     * A {@link ListMemberBinder} is a configuration entity used to used to configure the
     * binding of an implementation type or instance that has been added to a
     * {@link Container} and bind it as a value in an injectable {@code List}.  The
     * injected list will include elements in the order that they are added to this
     * binder.  Note that this means if multiple {@link Loader}s are used to contribute to
     * the list, the order in which the loaders are provided to the framework is
     * important.
     * 
     * @param <T> the type to configure
     */
    public interface ListMemberBinder<T> {
        
        /**
         * Configures the injectable list type to use for the binding.
         * 
         * @param elementType the type of the elements
         * @return a {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forElementType(Class<? super T> elementType);
        
        /**
         * Configures the injectable list type to use for the binding.
         * 
         * @param elementType the type of the elements
         * @return a {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forElementType(TypeToken<? super T> elementType);
        
    }
    
    /**
     * A {@link SetMemberBinder} is a configuration entity used to used to configure the
     * binding of an implementation type or instance that has been added to a
     * {@link Container} and bind it as a value in an injectable {@code Set}.
     * 
     * @param <T> the type to configure
     */
    public interface SetMemberBinder<T> {
        
        /**
         * Configures the injectable set type to use for the binding.
         * 
         * @param elementType the type of the elements
         * @return a {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forElementType(Class<? super T> elementType);
        
        /**
         * Configures the injectable set type to use for the binding.
         * 
         * @param elementType the type of the elements
         * @return a {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forElementType(TypeToken<? super T> elementType);
        
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
         * Note that this method binds the configured entity to a specific
         * <em>instance</em> of a specific Qualifier annotation.
         * 
         * @param <Q> the qualifier annotation type
         * @param qualifier the qualifier value
         */
        <Q extends Annotation> void whenQualifiedWith(Q qualifier);
        
    }

}
