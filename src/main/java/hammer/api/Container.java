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
     * Configure this container to allow multiple implementation types or instance types
     * to be bound to the same injection type via strict bindings.
     * By default, calling this method will allow
     * multi bindings for any injection type, all of which will be configured with a
     * decision policy of {@link DecisionPolicy#RANDOM} in order to determine what to
     * inject at injection time.  Use the returned {@link MultiBinder} to change which
     * types to configure and which policy to use.
     * 
     * @return a {@link MultiBinder} to configure the allowable bindings
     */
    MultiBinder allowMultiBindings();
    
    /**
     * Configure this container to allow an implementation type or instance to be bound to
     * a non-specific category of types.  This specifically enables the container to
     * make attempts to satisfy injection requests for injection types that it has not
     * been made explicitly aware of via a strict binding.  It also allows a container
     * to be configured with "default" free bindings that can be overridden with strict
     * bindings.
     */
    void allowFreeBindings();
    
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
        
        /**
         * Configure this type as a non-specific free type binding.
         * 
         * @return a {@link FreeBinder} used to further configure the binding
         */
        FreeBinder<T> asFreeBinding();
        
        /**
         * Configure this type as a binding to a member of an injectable {@code Map}.
         * 
         * @return a {@link MapMemberBinder} used to further configure the binding
         */
        MapMemberBinder<T> asMapMemberBinding();
        
        /**
         * Configure this type as a binding to a member of an injectable {@code List}.
         * 
         * @return a {@link ListMemberBinder} used to further configure the binding
         */
        ListMemberBinder<T> asListMemberBinding();
        
        /**
         * Configure this type as a binding to a member of an injectable {@code Set}.
         * 
         * @return a {@link SetMemberBinder} used to further configure the binding
         */
        SetMemberBinder<T> asSetMemberBinding();
    }
    
    /**
     * A {@link MultiBinder} is a configuration entity used to configure one or more types
     * as capable of being bound to multiple implementation types or instances.
     */
    public interface MultiBinder {

        /**
         * Configure this binding to be allowed for any type.
         * 
         * @return a {@link DecisionPolicyBinder} used to further configure the binding
         */
        DecisionPolicyBinder forAnyType();
        
        /**
         * Configure this binding to be allowed for any sub type of the given class
         * 
         * @param type the type to configure
         * @return a {@link DecisionPolicyBinder} used to further configure the binding
         */
        DecisionPolicyBinder forAnySubtype(Class<?> type);
        
        /**
         * Configure this binding to be allowed for any sub type of the given type
         * 
         * @param type the type to configure
         * @return a {@link DecisionPolicyBinder} used to further configure the binding
         */
        DecisionPolicyBinder forAnySubtype(TypeToken<?> type);
        
        /**
         * Configure this binding to be allowed for one or more specific classes
         * 
         * @param types the types to configure
         * @return a {@link DecisionPolicyBinder} used to further configure the binding
         */
        DecisionPolicyBinder forSpecificTypes(Class<?>... types);
        
        /**
         * Configure this binding to be allowed for one or more specific types
         * 
         * @param types the types to configure
         * @return a {@link DecisionPolicyBinder} used to further configure the binding
         */
        DecisionPolicyBinder forSpecificTypes(TypeToken<?>... types);
    }
    
    /**
     * A {@link DecisionPolicyBinder} is a configuration entity used to configure how
     * to decide between multiple bound types or implementations if an injectable entity
     * has multiple choices.
     */
    public interface DecisionPolicyBinder {

        /**
         * Configure this binding to use the given decision policy.
         * 
         * @param policy the policy to use on injections for the configured type(s)
         */
        void withDecisionPolicy(DecisionPolicy policy);
        
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
     * A {@link FreeBinder} is a configuration entity used to used to configure the
     * binding of an implementation type or instance that has been added to a
     * {@link Container} and bind it to a non-specific set of injectable types.
     * 
     * @param <T> the type to configure
     */
    public interface FreeBinder<T> {
        
        /**
         * Binds the configured implementation type or instance to injection requests for
         * any assignable injection type.  Note that use of this method will make the
         * configured type or instance eligible to be injected for injection requests of
         * any type that either does NOT have any specific bindings OR is allowed to be
         * configured with multi bindings.
         * 
         * @return an {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forAnyAssignableType();
        
        /**
         * Binds the configured implementation type or instance to injection requests for
         * any assignable injection type that is the same type or a sub type of the given
         * type.  Note that use of this method will make the
         * configured type or instance eligible to be injected for injection requests of
         * any matching type that either does NOT have any specific bindings OR is allowed
         * to be configured with multi bindings.
         * 
         * @param type the type to bind
         * @return an {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forAnyAssignableSubtype(Class<? super T> type);
        
        /**
         * Binds the configured implementation type or instance to injection requests for
         * any assignable injection type that is the same type or a sub type of the given
         * type.  Note that use of this method will make the
         * configured type or instance eligible to be injected for injection requests of
         * any matching type that either does NOT have any specific bindings OR is allowed
         * to be configured with multi bindings.
         * 
         * @param type the type to bind
         * @return an {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forAnyAssignableSubtype(TypeToken<? super T> type);

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
         * @return an {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder withKey(K key);
        
    }
    
    /**
     * A {@link ListMemberBinder} is a configuration entity used to used to configure the
     * binding of an implementation type or instance that has been added to a
     * {@link Container} and bind it as a value in an injectable {@code List}.
     * 
     * @param <T> the type to configure
     */
    public interface ListMemberBinder<T> {
        
        /**
         * Configures the injectable list type to use for the binding.
         * 
         * @param elementType the type of the elements
         * @return an {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forElementType(Class<? super T> elementType);
        
        /**
         * Configures the injectable list type to use for the binding.
         * 
         * @param elementType the type of the elements
         * @return an {@link QualifierBinder} used to further configure the binding
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
         * @return an {@link QualifierBinder} used to further configure the binding
         */
        QualifierBinder forElementType(Class<? super T> elementType);
        
        /**
         * Configures the injectable set type to use for the binding.
         * 
         * @param elementType the type of the elements
         * @return an {@link QualifierBinder} used to further configure the binding
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
