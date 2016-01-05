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

import hammer.api.InjectionType;
import hammer.api.Injector;
import hammer.api.Loader;
import hammer.api.Multiton;
import hammer.api.TypeToken;
import javax.inject.Provider;
import javax.inject.Qualifier;
import javax.inject.Scope;

/**
 * Internal injector state.
 */
class InjectionContext {

    private final Map<InjectionRequest, InjectionProvider> injectionRequests;
    private final Set<InjectionType> injectionTypes;
    private final Set<Annotation> activeScopes;
    private final Set<Annotation> localScopes;
    
    private final Map<TypeToken<?>, InjectionProvider> injectionProviders;
    private final Map<TypeToken<?>, Introspector.InjectionProfile> injectionProfiles;
    private final Introspector.AccessProfile accessProfile;

    // the parent context or null if this is the top level        
    private final InjectionContext parentContext;
    // thread local stack to detect injection loops
    private final ThreadLocal<Set<InjectionProvider>> loopDetector
            = new ThreadLocal<Set<InjectionProvider>>() {
        @Override
        protected Set<InjectionProvider> initialValue() {
            return new LinkedHashSet<>();
        }
    };

    InjectionContext(Iterable<? extends Loader> loaders) {
        this.parentContext = null;

        this.injectionProfiles = new HashMap<>();

        ContainerImpl container = new ContainerImpl();
        for (Loader loader : loaders) {
            loader.load(container);
        }
        ContainerImpl.Result result = container.unload();

        this.injectionTypes = result.getInjectionTypes();
        this.accessProfile = Introspector.getAccessProfile(injectionTypes);
        this.activeScopes = result.getActiveScopes();
        this.localScopes = new HashSet<>(activeScopes);
        
        this.injectionProviders = new HashMap<>();
        this.injectionRequests = new HashMap<>();

        // for each strictbinding, create injectionprovider and associate
        // with each injectionrequest
        for (StrictBinding<?> binding : result.getStrictBindings()) {
            InjectionProvider provider = getInjectionProvider(binding);
            
            for (InjectionRequest request : binding.getInjectionRequests()) {
                bindInjectionRequest(request, provider);
            }
        }
        
        // build injectionproviders for map bindings
        Map<InjectionRequest, MapInstantiator> mapInstantiators = new HashMap<>();
        for (MapBinding<?> binding : result.getMapBindings()) {
            InjectionRequest request = binding.getMapInjectionRequest();
            MapInstantiator mi = mapInstantiators.get(request);
            if (mi == null) {
                mi = new MapInstantiator(request.getType());
                mapInstantiators.put(request, mi);
                bindInjectionRequest(request, 
                                     getCollectionInjectionProvider(binding, mi));
            }
            mi.put(binding.getKey(), getInjectionProvider(binding));
        }
        
        // build injectionproviders for list bindings
        Map<InjectionRequest, ListInstantiator> listInstantiators = new HashMap<>();
        for (ListBinding<?> binding : result.getListBindings()) {
            InjectionRequest request = binding.getListInjectionRequest();
            ListInstantiator li = listInstantiators.get(request);
            if (li == null) {
                li = new ListInstantiator(request.getType());
                listInstantiators.put(request, li);
                bindInjectionRequest(request,
                                     getCollectionInjectionProvider(binding, li));
            }
            li.add(getInjectionProvider(binding));
        }
        
        // build injectionproviders for set bindings
        Map<InjectionRequest, SetInstantiator> setInstantiators = new HashMap<>();
        for (SetBinding<?> binding : result.getSetBindings()) {
            InjectionRequest request = binding.getSetInjectionRequest();
            SetInstantiator si = setInstantiators.get(request);
            if (si == null) {
                si = new SetInstantiator(request.getType());
                setInstantiators.put(request, si);
                bindInjectionRequest(request,
                                     getCollectionInjectionProvider(binding, si));
            }
            si.add(getInjectionProvider(binding));
        }

        // inject the requested statics
        for (Class<?> clss : result.getStaticInjectionsEnabled()) {
            injectStatics(clss);
        }
    }

    InjectionContext(InjectionContext parent, Annotation scope) {
        this.parentContext = parent;
        this.localScopes = new HashSet<>();
        this.localScopes.add(scope);
        this.activeScopes = new HashSet<>(parent.getActiveScopes());
        this.activeScopes.add(scope);

        this.injectionProfiles = parent.injectionProfiles;
        this.injectionTypes = parent.injectionTypes;
        this.accessProfile = parent.accessProfile;
        this.injectionProviders = parent.injectionProviders;
        this.injectionRequests = parent.injectionRequests;
    }
    
    /** === Package-private API methods === **/
    
    /**
     * Returns the set of {@link InjectionType}s that this {@code InjectionContext}
     * performs.
     * 
     * @return set of supported {@link InjectionType}s
     */
    Set<InjectionType> getInjectionTypes() {
        return injectionTypes;
    }

    /**
     * Returns the set of scopes that are active in this {@code InjectionContext}.
     * 
     * @return active scopes in this injection context
     */
    Set<Annotation> getActiveScopes() {
        return activeScopes;
    }
    
    /**
     * Performs an actual injection request, instantiating a new instance or returning a
     * scoped or pre-instantiated instance depending on the configuration of the injector.
     *
     * @param <T>       the type of the injected objects
     * @param type      the type of the injection request
     * @param qualifier the qualifier associated with the injection request or
     *                  {@code null} if there is no qualifier
     * @return an instantiated object that satisfies the injection request
     */
    <T> T injectionRequest(TypeToken<T> type, Annotation qualifier) {
        // special case requests for providers
        if (Objects.equals(type.getRawClass(), Provider.class)
            && type.getType() instanceof ParameterizedType) {
            TypeToken<?> providedType = TypeToken.forType(
                    ((ParameterizedType) type.getType()).getActualTypeArguments()[0]);

            return (T) providerRequest(providedType, qualifier);
        }

        InjectionRequest ir = new InjectionRequest(type, qualifier);

        InjectionProvider provider = injectionRequests.get(ir);
        if (provider == null) {
            throw new IllegalArgumentException(
                    "Injector cannot inject a request for type " + type
                    + " and qualifier " + qualifier);
        }
        
        return (T) safeProvide(provider, ir);
    }

    /**
     * Returns a {@code Provider} that will provide instances for injection requests of
     * the given type and qualifier.
     *
     * @param <T>       the type of the objects provided by the provider
     * @param type      represents the type of the injection request
     * @param qualifier the qualifier annotation for the injection request (or
     *                  {@code null} if there is no qualifier
     * @return a {@code Provider} that is configure to provide instances for the requests
     */
    <T> Provider<T> providerRequest(final TypeToken<T> type,
                                    final Annotation qualifier) {
        return new Provider<T>() {
            @Override
            public T get() {
                return injectionRequest(type, qualifier);
            }
        };
    }
    
    /**
     * Injects members on the given object in this injection context per
     * {@link Injector#injectMembers(java.lang.Object)}
     * 
     * @param target the target object to inject
     */
    final void injectMembers(Object target) {
        Introspector.InjectionProfile profile = getInjectionProfile(
                TypeToken.forClass(target.getClass()));

        for (AccessibleObject element : profile.getInjectableMembers()) {
            if (element instanceof Field) {
                injectField((Field) element, target);
            } else if (element instanceof Method) {
                injectMethod((Method) element, target);
            }
        }
    }

    /**
     * Injects static members on the given class in this injection context per
     * {@link Injector#injectStatics(java.lang.Object)}
     * 
     * @param target the target class to inject
     */
    final void injectStatics(Class<?> targetClass) {
        Introspector.InjectionProfile profile = getInjectionProfile(
                TypeToken.forClass(targetClass));

        for (AccessibleObject element : profile.getInjectableStatics()) {
            if (element instanceof Field) {
                injectField((Field) element, targetClass);
            } else if (element instanceof Method) {
                injectMethod((Method) element, targetClass);
            }
        }
    }
    
    
    
    
    /** === Private utility methods === **/
    
    private Object safeProvide(InjectionProvider provider, InjectionRequest ir) {
        if (loopDetector.get().contains(provider)) {
            throw new IllegalStateException(
                    "Loop detected while attempting to inject type " + ir.getType()
                    + " and qualifier " + ir.getQualifier());
        }

        try {
            loopDetector.get().add(provider);
            return provider.provide(ir, this);
        } finally {
            loopDetector.get().remove(provider);
        }
    }

    private InjectionContext getParentContext() {
        return parentContext;
    }

    private Set<Annotation> getLocalScopes() {
        return localScopes;
    }
    
    private InjectionProvider getInjectionProvider(AbstractBinding<?> binding) {
        if (binding.getInstance() == null) {
            return getInjectionProvider(binding.getImplementation());
        } else {
            return new InstanceInjectionProvider(binding.getInstance());
        }
    }

    private InjectionProvider getInjectionProvider(TypeToken<?> type) {
        InjectionProvider provider = injectionProviders.get(type);
        if (provider == null) {
            // scan for scope annotations
            Annotation found = null;
            for (Annotation annon : type.getRawClass().getAnnotations()) {
                if (annon.annotationType().getAnnotation(Scope.class) != null) {
                    if (found == null) {
                        found = annon;
                    } else {
                        throw new IllegalArgumentException(
                                "Type " + type
                                + " cannot be annotated with multiple @Scope annotations");
                    }
                }
            }

            if (found == null) {
                provider = new UnscopedInjectionProvider(
                        new StandardInstantiator(type));
            } else if (found.annotationType().getAnnotation(Multiton.class) != null) {
                provider = new MultitonScopedInjectionProvider(
                        new StandardInstantiator(type), found);
            } else {
                provider = new SingletonScopedInjectionProvider(
                        new StandardInstantiator(type), found);
            }
            injectionProviders.put(type, provider);
        }
        return provider;
    }
    
    private InjectionProvider getCollectionInjectionProvider(
            AbstractCollectionBinding<?> binding, InjectionInstantiator instantiator) {
        Annotation scope = binding.getScope();
        if (scope == null) {
            return new UnscopedInjectionProvider(instantiator);
        } else if (scope.annotationType().getAnnotation(Multiton.class) != null) {
            return new MultitonScopedInjectionProvider(instantiator, scope);
        } else {
            return new SingletonScopedInjectionProvider(instantiator, scope);
        }
    }
    
    private Introspector.InjectionProfile getInjectionProfile(TypeToken<?> type) {
        Introspector.InjectionProfile profile = injectionProfiles.get(type);
        if (profile == null) {
            profile = Introspector.getInjectionProfile(type, accessProfile);
            injectionProfiles.put(type, profile);
        }
        return profile;
    }
    
    private void bindInjectionRequest(InjectionRequest request, 
                                      InjectionProvider provider) {
        if (injectionRequests.put(request, provider) != null) {
            throw new IllegalStateException(
                    "Multiple ambiguous bindings detected for type "
                    + request.getType() + " and qualifier "
                    + request.getQualifier());
        }
    }

    

    /**
     * Performs an injection request for a {@code Field} and assigns the resulting value
     * to the given target object.
     *
     * @param field  the field to inject
     * @param target the target object to assign the field value to
     */
    private void injectField(Field field, Object target) {
        Annotation qualifier = null;
        for (Annotation annon : field.getAnnotations()) {
            if (annon.annotationType().getAnnotation(Qualifier.class) != null) {
                qualifier = annon;
            }
        }

        Reflector.setField(field, target, injectionRequest(
                           TypeToken.forType(field.getGenericType()), qualifier));
    }

    /**
     * Invokes the given {@code Method} on the given target object using results of
     * injection requests for each parameter of the method.
     *
     * @param method the method to inject
     * @param target the target object to invoke the method on
     */
    private void injectMethod(Method method, Object target) {
        List<Object> args = new ArrayList<>();
        Type[] paramTypes = method.getGenericParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
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

    /**
     * Invokes the given {@code Constructor} using results of injection requests for each
     * parameter of the constructor. The resulting instantiated object is returned.
     *
     * @param <T> the type of the object to instantiate
     * @param ctr the constructor to inject
     * @return the instantiated object resulting from invoking the given constructor
     */
    private <T> T injectConstructor(Constructor<T> ctr) {
        // prepare injection request for each constructor parameter
        List<Object> args = new ArrayList<>();
        Type[] paramTypes = ctr.getGenericParameterTypes();
        Annotation[][] paramAnnotations = ctr.getParameterAnnotations();
        for (int i = 0; i < paramTypes.length; i++) {
            Annotation qualifier = null;
            for (Annotation annon : paramAnnotations[i]) {
                if (annon.annotationType().getAnnotation(Qualifier.class) != null) {
                    qualifier = annon;
                }
            }

            args.add(injectionRequest(TypeToken.forType(paramTypes[i]), qualifier));
        }

        return Reflector.invokeConstructor(ctr, args.toArray());
    }

    
    
    /** === Private utility classes === **/
    
    private static class StandardInstantiator implements InjectionInstantiator {
        private final TypeToken<?> implementation;
        private StandardInstantiator(TypeToken<?> implementation) {
            this.implementation = implementation;
        }

        @Override
        public Object instantiate(InjectionContext context) {
            Constructor<?> constructor = context.getInjectionProfile(implementation).
                    getInjectableConstructor();
            Object value = context.injectConstructor(constructor);
            context.injectMembers(value);
            return value;
        }

        @Override
        public TypeToken<?> getType() {
            return implementation;
        }
        
    }
    
    private static class MapInstantiator implements InjectionInstantiator {
        private final Map<Object, InjectionProvider> config;
        private final TypeToken<?> mapType;
        private MapInstantiator(TypeToken<?> mapType) {
            this.config = new HashMap<>();
            this.mapType = mapType;
        }

        @Override
        public Object instantiate(InjectionContext context) {
            Map<Object, Object> map = new HashMap<>();
            for (Map.Entry<Object, InjectionProvider> entry : config.entrySet()) {
                map.put(entry.getKey(), 
                        context.safeProvide(entry.getValue(), 
                                            new InjectionRequest(TypeToken.forClass(
                                                    Object.class), null)));
            }
            return Collections.unmodifiableMap(map);
        }

        @Override
        public TypeToken<?> getType() {
            return mapType;
        }
        
        private void put(Object key, InjectionProvider value) {
            if (config.put(key, value) != null) {
                throw new IllegalStateException("Multiple bindings for the key " + key +
                                                " in the map binder of type " + mapType);
            }
        }
    }
    
    private static class ListInstantiator implements InjectionInstantiator {
        private final List<InjectionProvider> config;
        private final TypeToken<?> listType;
        private ListInstantiator(TypeToken<?> listType) {
            this.config = new ArrayList<>();
            this.listType = listType;
        }

        @Override
        public Object instantiate(InjectionContext context) {
            List<Object> list = new ArrayList<>();
            for (InjectionProvider provider : config) {
                list.add(context.safeProvide(provider,
                                             new InjectionRequest(TypeToken.forClass(
                                                     Object.class), null)));
            }
            return Collections.unmodifiableList(list);
        }

        @Override
        public TypeToken<?> getType() {
            return listType;
        }
        
        private void add(InjectionProvider value) {
            config.add(value);
        }
    }
    
    private static class SetInstantiator implements InjectionInstantiator {
        private final List<InjectionProvider> config;
        private final TypeToken<?> setType;
        private SetInstantiator(TypeToken<?> setType) {
            this.config = new ArrayList<>();
            this.setType = setType;
        }

        @Override
        public Object instantiate(InjectionContext context) {
            Set<Object> set = new HashSet<>();
            for (InjectionProvider provider : config) {
                set.add(context.safeProvide(provider,
                                            new InjectionRequest(TypeToken.forClass(
                                                    Object.class), null)));
            }
            return Collections.unmodifiableSet(set);
        }

        @Override
        public TypeToken<?> getType() {
            return setType;
        }
        
        private void add(InjectionProvider value) {
            config.add(value);
        }
    }
    
    /**
     * Provides the same instance which has been instantiated outside the scope of the
     * injector for every injection request.
     */
    private static class InstanceInjectionProvider implements InjectionProvider {
        
        private final Object instance;
        private InstanceInjectionProvider(Object instance) {
            this.instance = instance;
        }

        @Override
        public Object provide(InjectionRequest request, InjectionContext context) {
            return instance;
        }
        
    }
    
    /**
     * Instantiates a new instance of the configured type for every injection request.
     */
    private static class UnscopedInjectionProvider implements InjectionProvider {
        private final InjectionInstantiator instantiator;
        
        private UnscopedInjectionProvider(InjectionInstantiator instantiator) {
            this.instantiator = instantiator;
        }
        
        @Override
        public synchronized Object provide(InjectionRequest request, 
                                           InjectionContext context) {
            return instantiator.instantiate(context);
        }
    }
    
    /**
     * Always provides the same injector-instantiated instance for every injection request
     * in a particular scope.  Note that this provider serves injection requests for
     * multiple different injection contexts and thus may manage multiple instances
     * of the given type in order to satisfy the requirements of each independent
     * injector.
     */
    private static class SingletonScopedInjectionProvider implements InjectionProvider {
        private final InjectionInstantiator instantiator;
        private final Annotation scope;
        private final Map<InjectionContext, Object> instances;
        
        private SingletonScopedInjectionProvider(InjectionInstantiator instantiator,
                                                 Annotation scope) {
            this.instantiator = instantiator;
            this.scope = scope;
            this.instances = new HashMap<>();
        }

        @Override
        public synchronized Object provide(InjectionRequest request, 
                                           InjectionContext context) {
            while (context != null && !context.getLocalScopes().contains(scope)) { 
                context = context.getParentContext();
            }
            
            if (context == null) {
                throw new IllegalStateException(
                        "Cannot provide an instance of type " + instantiator.getType() + 
                        " since its Scope " + scope + " is not active");
            }
            
            // if we made it here, look for an existing instance in the determined context
            Object instance = instances.get(context);
            if (instance != null) {
                return instance;
            }
            
            // construct a new instance if needed
            Object value = instantiator.instantiate(context);
            instances.put(context, value);
            
            return value;
        }
        
    }
    
    /**
     * Always provides the same injector-instantiated instance for every injection request
     * in a particular scope with the same qualifier annotation.  Note that this provider
     * serves injection requests for multiple different injection contexts and thus may
     * manage multiple instances of the given type in order to satisfy the requirements
     * of each independent injector.
     */
    private static class MultitonScopedInjectionProvider implements InjectionProvider {
        private final InjectionInstantiator instantiator;
        private final Annotation scope;
        private final Map<InjectionContext, Map<Annotation, Object>> instances;
        
        private MultitonScopedInjectionProvider(InjectionInstantiator instantiator,
                                                Annotation scope) {
            this.instantiator = instantiator;
            this.scope = scope;
            this.instances = new HashMap<>();
        }

        @Override
        public synchronized Object provide(InjectionRequest request, 
                                           InjectionContext context) {
            while (context != null && !context.getLocalScopes().contains(scope)) { 
                context = context.getParentContext();
            }
            
            if (context == null) {
                throw new IllegalStateException(
                        "Cannot provide an instance of type " + instantiator.getType() + 
                        " since its Scope " + scope + " is not active");
            }
            
            Map<Annotation, Object> instanceContext = instances.get(context);
            if (instanceContext == null) {
                instanceContext = new HashMap<>();
                instances.put(context, instanceContext);
            }
            Object instance = instanceContext.get(request.getQualifier());
            if (instance != null) {
                return instance;
            }
            
            Object value = instantiator.instantiate(context);
            instanceContext.put(request.getQualifier(), value);
            
            return value;
        }
        
    }
}
