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
import java.util.Collections;
import java.util.Set;

import javax.inject.Provider;

import hammer.api.Scopes;
import hammer.api.InjectionType;
import hammer.api.Injector;
import hammer.api.Loader;
import hammer.api.TypeToken;

/**
 * Implementation of an {@link Injector}.
 */
class InjectorImpl implements Injector {
    
    private final InjectionContext context;
    
    InjectorImpl(Iterable<? extends Loader> loaders) {
        this.context = new InjectionContext(loaders);
    }
    
    private InjectorImpl(InjectionContext parent, Annotation scope) {
        this.context = new InjectionContext(parent, scope);
    }
    
    
    
    /** === Implementation of public interface methods === **/    

    @Override
    public <T> T getInstance(Class<T> target) {
        return context.injectionRequest(TypeToken.forClass(target), null);
    }
    
    @Override
    public <T> T getInstance(TypeToken<T> target) {
        return context.injectionRequest(target, null);
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> target) {
        return context.providerRequest(TypeToken.forClass(target), null);
    }
    
    @Override
    public <T> Provider<T> getProvider(TypeToken<T> target) {
        return context.providerRequest(target, null);
    }

    @Override
    public Set<Annotation> getActiveScopes() {
        return Collections.unmodifiableSet(context.getActiveScopes());
    }

    @Override
    public void injectMembers(Object target) {
        context.injectMembers(target);
    }

    @Override
    public void injectStatics(Class<?> targetClass) {
        context.injectStatics(targetClass);
    }

    @Override
    public boolean isSupported(InjectionType type) {
        return context.getInjectionTypes().contains(type);
    }

    @Override
    public Injector enterScope(Class<? extends Annotation> scope) {
        return new InjectorImpl(context, Scopes.scope(scope));
    }
}
