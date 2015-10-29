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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;
import javax.inject.Qualifier;
import javax.inject.Scope;

import hammer.api.Scopes;
import hammer.api.InjectionType;
import hammer.api.Injector;
import hammer.api.Loader;
import hammer.api.TypeToken;

/**
 *
 */
class InjectorImpl implements Injector {
    
    private final Map<InjectionRequest, InjectionProvider> injectionRequests;
    private final Set<InjectionType> injectionTypes;
    private final Set<Annotation> activeScopes;
    
    private final Map<TypeToken<?>, InjectionProvider> injectionProviders;
    private final Map<TypeToken<?>, Introspector.InjectionProfile> injectionProfiles;
    private final Introspector.AccessProfile accessProfile;
    
    // thread local stack to detect injection loops
    private final ThreadLocal<Set<InjectionProvider>> loopDetector;
    
    InjectorImpl(Iterable<? extends Loader> loaders) {
        this.loopDetector = new ThreadLocal<Set<InjectionProvider>>() {
            @Override
            protected Set<InjectionProvider> initialValue() {
                return new LinkedHashSet<>();
            }
        };
        
        this.injectionProfiles = new HashMap<>();
        
        ContainerImpl container = new ContainerImpl();
        for (Loader loader : loaders) {
            loader.load(container);
        }
        ContainerImpl.Result result = container.unload();
        
        this.injectionTypes = result.getInjectionTypes();
        this.accessProfile = Introspector.getAccessProfile(injectionTypes);
        this.activeScopes = new HashSet<>();
        this.activeScopes.add(Scopes.SINGLETON);

        // for each strictbinding, create injectionprovider and associate
        // with each injectionrequest
        this.injectionProviders = new HashMap<>();
        this.injectionRequests = new HashMap<>();
        for (StrictBinding<?> binding : result.getStrictBindings()) {
            
            InjectionProvider provider;
            if (binding.getInstance() == null) {
                provider = getInjectionProvider(binding.getImplementation());
            } else {
                provider = new InstanceInjectionProvider(binding.getInstance());
            }
            
            for (InjectionRequest request : binding.getInjectionRequests()) {
                if (injectionRequests.put(request, provider) != null) {
                    throw new IllegalStateException(
                            "Multiple ambiguous bindings detected for type " + 
                                    request.getType() + " and qualifier " + 
                                    request.getQualifier());
                }
            }
        }

        // inject the requested statics
        for (Class<?> clss : result.getStaticInjectionsEnabled()) {
            injectStatics(clss);
        }
    }
    
    private InjectionProvider getInjectionProvider(TypeToken<?> type) {
        InjectionProvider provider = injectionProviders.get(type);
        if (provider == null) {
            provider = new ScopedInjectionProvider(type);
            injectionProviders.put(type, provider);
        }
        return provider;
    }
    
    private Introspector.InjectionProfile getInjectionProfile(TypeToken<?> type) {
        Introspector.InjectionProfile profile = injectionProfiles.get(type);
        if (profile == null) {
            profile = Introspector.getInjectionProfile(type, accessProfile);
            injectionProfiles.put(type, profile);
        }
        return profile;
    }
    
    private <T> T injectionRequest(TypeToken<T> type, Annotation qualifier) {
        // special case requests for providers
        if (Objects.equals(type.getRawClass(), Provider.class) &&
            type.getType() instanceof ParameterizedType) {
            TypeToken<?> providedType = TypeToken.forType(
                    ((ParameterizedType) type.getType()).getActualTypeArguments()[0]);
            
            return (T) providerRequest(providedType, qualifier);
        }
        
        InjectionRequest ir = new InjectionRequest(type, qualifier);
        
        InjectionProvider provider = injectionRequests.get(ir);
        if (provider == null) {
            throw new IllegalArgumentException(
                    "Injector cannot inject a request for type " + type +
                            " and qualifier " + qualifier);
        }
        
        if (loopDetector.get().contains(provider)) {
            throw new IllegalStateException(
                    "Loop detected while attempting to inject type " + type +
                            " and qualifier " + qualifier);
        }
        
        try {
            loopDetector.get().add(provider);
            return (T) provider.provide(activeScopes);
        } finally {
            loopDetector.get().remove(provider);
        }
    }
    
    private <T> Provider<T> providerRequest(final TypeToken<T> type, 
                                            final Annotation qualifier) {
        return new Provider<T>() {
            @Override
            public T get() {
                return injectionRequest(type, qualifier);
            }
        };
    }

    @Override
    public <T> T getInstance(Class<T> target) {
        return injectionRequest(TypeToken.forClass(target), null);
    }
    
    @Override
    public <T> T getInstance(TypeToken<T> target) {
        return injectionRequest(target, null);
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> target) {
        return providerRequest(TypeToken.forClass(target), null);
    }
    
    @Override
    public <T> Provider<T> getProvider(TypeToken<T> target) {
        return providerRequest(target, null);
    }

    @Override
    public Set<Annotation> getActiveScopes() {
        return Collections.unmodifiableSet(activeScopes);
    }

    @Override
    public void injectMembers(Object target) {
        Introspector.InjectionProfile profile = getInjectionProfile(
                TypeToken.forClass(target.getClass()));
        
        for (AccessibleObject element : profile.getInjectableMembers()) {
            if (element instanceof Field) {
                Field field = (Field) element;
                Annotation qualifier = null;
                for (Annotation annon : field.getAnnotations()) {
                    if (annon.annotationType().getAnnotation(Qualifier.class) != null) {
                        qualifier = annon;
                    }
                }
                
                Reflector.setField(field, target, injectionRequest(
                        TypeToken.forType(field.getGenericType()), qualifier));
            } else if (element instanceof Method) {
                Method method = (Method) element;
                List<Object> args = new ArrayList<>();
                Type[] paramTypes = method.getGenericParameterTypes();
                Annotation [][] paramAnnotations = method.getParameterAnnotations();
                for (int i = 0; i < paramTypes.length; i++) {
                    Annotation qualifier = null;
                    for (Annotation annon : paramAnnotations[i]) {
                        if (annon.annotationType().getAnnotation(Qualifier.class) != null) {
                            qualifier = annon;
                        }
                    }
                    
                    args.add(injectionRequest(TypeToken.forType(paramTypes[i]), qualifier));
                }
            
                Reflector.invokeMethod(method, target, args.toArray());
            }
        }
    }

    @Override
    public void injectStatics(Class<?> targetClass) {
        Introspector.InjectionProfile profile = getInjectionProfile(
                TypeToken.forClass(targetClass));
        
        for (AccessibleObject element : profile.getInjectableStatics()) {
            if (element instanceof Field) {
                Field field = (Field) element;
                Annotation qualifier = null;
                for (Annotation annon : field.getAnnotations()) {
                    if (annon.annotationType().getAnnotation(Qualifier.class) != null) {
                        qualifier = annon;
                    }
                }
                
                Reflector.setField(field, targetClass, injectionRequest(
                        TypeToken.forType(field.getGenericType()), qualifier));
            } else if (element instanceof Method) {
                Method method = (Method) element;
                List<Object> args = new ArrayList<>();
                Type[] paramTypes = method.getGenericParameterTypes();
                Annotation [][] paramAnnotations = method.getParameterAnnotations();
                for (int i = 0; i < paramTypes.length; i++) {
                    Annotation qualifier = null;
                    for (Annotation annon : paramAnnotations[i]) {
                        if (annon.annotationType().getAnnotation(Qualifier.class) != null) {
                            qualifier = annon;
                        }
                    }
                    
                    args.add(injectionRequest(TypeToken.forType(paramTypes[i]), qualifier));
                }
            
                Reflector.invokeMethod(method, targetClass, args.toArray());
            }
        }
    }

    @Override
    public boolean isSupported(InjectionType type) {
        return injectionTypes.contains(type);
    }

    

    private class InstanceInjectionProvider implements InjectionProvider {
        
        private final Object instance;
        private InstanceInjectionProvider(Object instance) {
            this.instance = instance;
        }

        @Override
        public Object provide(Set<Annotation> activeScopes) {
            return instance;
        }
        
    }
    
    private class ScopedInjectionProvider implements InjectionProvider {
        
        private final TypeToken<?> implementation;
        private final Annotation scope;
        private Object instance;
        
        private ScopedInjectionProvider(TypeToken<?> implementation) {
            this.implementation = implementation;
            
            // scan for scope annotations
            Annotation found = null;
            for (Annotation annon : implementation.getRawClass().getAnnotations()) {
                if (annon.annotationType().getAnnotation(Scope.class) != null) {
                    if (found == null) {
                        found = annon;
                    } else {
                        throw new IllegalArgumentException(
                                "Type " + implementation + 
                                " cannot be annotated with multiple @Scope annotations");
                    }
                }
            }
            
            this.scope = found;
        }

        @Override
        public synchronized Object provide(Set<Annotation> activeScopes) {
            boolean inScope = (scope != null && activeScopes.contains(scope));
            
            if (inScope && instance != null) {
                return instance;
            }
            
            Introspector.InjectionProfile profile = getInjectionProfile(implementation);
            Constructor<?> ctr = profile.getInjectableConstructor();
            
            // prepare injection request for each constructor parameter
            List<Object> args = new ArrayList<>();
            Type[] paramTypes = ctr.getGenericParameterTypes();
            Annotation [][] paramAnnotations = ctr.getParameterAnnotations();
            for (int i = 0; i < paramTypes.length; i++) {
                Annotation qualifier = null;
                for (Annotation annon : paramAnnotations[i]) {
                    if (annon.annotationType().getAnnotation(Qualifier.class) != null) {
                        qualifier = annon;
                    }
                }
                
                args.add(injectionRequest(TypeToken.forType(paramTypes[i]), qualifier));
            }
            
            Object value = Reflector.invokeConstructor(ctr, args.toArray());
            injectMembers(value);
            if (inScope) {
                instance = value;
            }
            
            return value;
        }
        
    }
    
    
}
