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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import hammer.api.Container;
import hammer.api.InjectionType;
import hammer.api.Scopes;
import hammer.api.TypeToken;
import javax.inject.Scope;

/**
 *
 */
class ContainerImpl implements Container {
    
    /**
     * True if this container is still active and available for loading.
     */
    private boolean active;
    
    /**
     * True if multi bindings are enabled for this container.
     */
    private boolean multiBindingsEnabled;
    
    /**
     * True if free bindings are enabled for this container.
     */
    private boolean freeBindingsEnabled;
    
    /**
     * True if injection types have been set at least once.
     */
    private boolean injectionTypesReset;
    
    /**
     * The set of injection types that are honored for this container.
     */
    private final Set<InjectionType> injectionTypes;
    
    /**
     * The set of scopes that are configured as active for this container.
     */
    private final Set<Annotation> activeScopes;
    
    /**
     * The set of classes for which static injections will be performed.
     */
    private final Set<Class<?>> staticInjectionsEnabled;
    
    /**
     * The history of type binding invocations made on this container.
     */
    private final List<TypeBindingInvocationImpl<?>> typeBindingInvocations;
    
    /**
     * The history of instance binding invocations made on this container.
     */
    private final List<InstanceBindingInvocationImpl<?>> instanceBindingInvocations;
    
    /**
     * The list of strict bindings added to this container.
     */
    private final List<StrictBinding<?>> strictBindings;
    
    /**
     * The list of map bindings added to this container.
     */
    private final List<MapBinding<?>> mapBindings;
    
    /**
     * The list of list bindings added to this container.
     */
    private final List<ListBinding<?>> listBindings;
    
    /**
     * The list of set bindings added to this container.
     */
    private final List<SetBinding<?>> setBindings;
    
    ContainerImpl() {
        active = true;
        multiBindingsEnabled = false;
        freeBindingsEnabled = false;
        injectionTypesReset = false;
        injectionTypes = new HashSet<>();
        injectionTypes.addAll(Arrays.asList(InjectionType.values()));
        activeScopes = new HashSet<>();
        activeScopes.add(Scopes.SINGLETON);
        activeScopes.add(Scopes.MULTITON);
        staticInjectionsEnabled = new HashSet<>();
        typeBindingInvocations = new ArrayList<>();
        instanceBindingInvocations = new ArrayList<>();
        strictBindings = new ArrayList<>();
        mapBindings = new ArrayList<>();
        listBindings = new ArrayList<>();
        setBindings = new ArrayList<>();
    }
    
    /** === Container implementation === **/

    @Override
    public <T> BindingInvocation<T> addImplType(Class<T> type) {
        verifyActive();
        
        Objects.requireNonNull(type, "type cannot be null");
        return addImplType(TypeToken.forClass(type));
    }

    @Override
    public <T> BindingInvocation<T> addImplType(TypeToken<T> type) {
        verifyActive();
        
        Objects.requireNonNull(type, "type cannot be null");
        TypeBindingInvocationImpl<T> invocation = new TypeBindingInvocationImpl<>(type);
        typeBindingInvocations.add(invocation);
        return invocation;
    }

    @Override
    public <T> BindingInvocation<T> addInstance(T instance) {
        verifyActive();
        
        Objects.requireNonNull(instance, "instance cannot be null");
        InstanceBindingInvocationImpl<T> invocation = 
                new InstanceBindingInvocationImpl<>(instance);
        instanceBindingInvocations.add(invocation);
        return invocation;
    }

    @Override
    public void allowInjections(InjectionType... injections) {
        verifyActive();
        
        if (!this.injectionTypesReset) {
            injectionTypes.clear();
        }
        injectionTypes.addAll(Arrays.asList(injections));
        this.injectionTypesReset = true;
    }

    @Override
    public void activateScopes(Class<? extends Annotation>... scopes) {
        for (Class<? extends Annotation> s : scopes) {
            if (s.getAnnotation(Scope.class) == null) {
                throw new IllegalArgumentException("Scope annotation " + s + 
                                                   " must be annotated with @Scope");
            }
            
            activeScopes.add(Scopes.scope(s));
        }
    }

    @Override
    public void configureStaticInjections(Class<?> type) {
        verifyActive();
        
        // ensure that if multiple implementations from the same class hierarchy are
        // configured for static injection, only the youngest ancestor is included so as
        // to avoid multiple injections
        Iterator<Class<?>> enabled = staticInjectionsEnabled.iterator();
        boolean addType = true;
        while (enabled.hasNext()) {
            Class<?> enabledType = enabled.next();
            if (enabledType.isAssignableFrom(type)) {
                enabled.remove();
            }
            
            if (type.isAssignableFrom(enabledType)) {
                addType = false;
            }
        }
        
        if (addType) {
            staticInjectionsEnabled.add(type);
        }
    }
    
    Result unload() {
        verifyActive();
        for (TypeBindingInvocationImpl t : typeBindingInvocations) {
            t.verifyComplete();
        }
        for (InstanceBindingInvocationImpl i : instanceBindingInvocations) {
            i.verifyComplete();
        }
        active = false;
        return new Result();
    }
    
    /**
     * Ensures that the container is still active for loading.
     */
    private void verifyActive() {
        if (!active) {
            throw new IllegalStateException("Container is no longer active for loading");
        }
    }
    
    
    /** === Utility classes === **/
    
    private class TypeBindingInvocationImpl<T> implements BindingInvocation<T> {
        
        private final TypeToken<T> type;
        private boolean complete = false;
        
        private TypeBindingInvocationImpl(TypeToken<T> type) {
            this.type = type;
        }
        
        @Override
        public StrictBinder<T> asStrictBinding() {
            verifyNotComplete();
            StrictBinding<T> binding = new StrictBinding<>(type);
            strictBindings.add(binding);
            complete = true;
            return binding;
        }

        @Override
        public MapMemberBinder<T> asMapMemberBinding() {
            return asMapMemberBinding(null);
        }

        @Override
        public MapMemberBinder<T> asMapMemberBinding(Annotation scope) {
            verifyNotComplete();
            Annotations.requireScope(scope);
            MapBinding<T> binding = new MapBinding<>(type, scope);
            mapBindings.add(binding);
            complete = true;
            return binding;
        }

        @Override
        public ListMemberBinder<T> asListMemberBinding() {
            return asListMemberBinding(null);
        }

        @Override
        public ListMemberBinder<T> asListMemberBinding(Annotation scope) {
            verifyNotComplete();
            Annotations.requireScope(scope);
            ListBinding<T> binding = new ListBinding<>(type, scope);
            listBindings.add(binding);
            complete = true;
            return binding;
        }

        @Override
        public SetMemberBinder<T> asSetMemberBinding() {
            return asSetMemberBinding(null);
        }

        @Override
        public SetMemberBinder<T> asSetMemberBinding(Annotation scope) {
            verifyNotComplete();
            Annotations.requireScope(scope);
            SetBinding<T> binding = new SetBinding<>(type, scope);
            setBindings.add(binding);
            complete = true;
            return binding;
        }

        
        
        private void verifyNotComplete() {
            if (complete) {
                throw new IllegalStateException("BindingInvocation already complete");
            }
        }
        
        private void verifyComplete() {
            if (!complete) {
                throw new IllegalStateException("BindingInvocation for type " + type + 
                                                " not complete");
            }
        }
        
    }
    
    private class InstanceBindingInvocationImpl<T> implements BindingInvocation<T> {
        
        private final T instance;
        private boolean complete = false;
        
        private InstanceBindingInvocationImpl(T instance) {
            this.instance = instance;
        }

        @Override
        public StrictBinder<T> asStrictBinding() {
            verifyNotComplete();
            StrictBinding<T> binding = new StrictBinding<>(instance);
            strictBindings.add(binding);
            complete = true;
            return binding;
        }

        @Override
        public MapMemberBinder<T> asMapMemberBinding() {
            return asMapMemberBinding(null);
        }

        @Override
        public MapMemberBinder<T> asMapMemberBinding(Annotation scope) {
            verifyNotComplete();
            Annotations.requireScope(scope);
            MapBinding<T> binding = new MapBinding<>(instance, scope);
            mapBindings.add(binding);
            complete = true;
            return binding;
        }

        @Override
        public ListMemberBinder<T> asListMemberBinding() {
            return asListMemberBinding(null);
        }

        @Override
        public ListMemberBinder<T> asListMemberBinding(Annotation scope) {
            verifyNotComplete();
            Annotations.requireScope(scope);
            ListBinding<T> binding = new ListBinding<>(instance, scope);
            listBindings.add(binding);
            complete = true;
            return binding;
        }

        @Override
        public SetMemberBinder<T> asSetMemberBinding() {
            return asSetMemberBinding(null);
        }

        @Override
        public SetMemberBinder<T> asSetMemberBinding(Annotation scope) {
            verifyNotComplete();
            Annotations.requireScope(scope);
            SetBinding<T> binding = new SetBinding<>(instance, scope);
            setBindings.add(binding);
            complete = true;
            return binding;
        }

        
        
        private void verifyNotComplete() {
            if (complete) {
                throw new IllegalStateException("BindingInvocation already complete");
            }
        }
        
        private void verifyComplete() {
            if (!complete) {
                throw new IllegalStateException(
                        "BindingInvocation for instance of type " + 
                        instance.getClass() + 
                        " not complete");
            }
        }
        
    }
    
    class Result {
        
        Set<InjectionType> getInjectionTypes() {
            return injectionTypes;
        }
        
        Set<Annotation> getActiveScopes() {
            return activeScopes;
        }
        
        Set<Class<?>> getStaticInjectionsEnabled() {
            return staticInjectionsEnabled;
        }
        
        List<StrictBinding<?>> getStrictBindings() {
            return strictBindings;
        }
        
        List<MapBinding<?>> getMapBindings() {
            return mapBindings;
        }
        
        List<ListBinding<?>> getListBindings() {
            return listBindings;
        }
        
        List<SetBinding<?>> getSetBindings() {
            return setBindings;
        }
    }

}
