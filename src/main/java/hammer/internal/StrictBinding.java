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
import java.util.List;
import java.util.Objects;

import hammer.api.Container.QualifierBinder;
import hammer.api.Container.StrictBinder;
import hammer.api.TypeToken;


/**
 * A {@link StrictBinding} defines the exact implementation class and {@code Scope} OR 
 * instance to be used for a specific declared type and optional {@code Qualifier}.
 */
class StrictBinding<T> implements StrictBinder<T> {

    private final TypeToken<T> implementation;
    private final T instance;
    
    private List<TypeToken<?>> types = new ArrayList<>();
    private Annotation qualifier;
    
    private boolean bound = false;
    
    StrictBinding (TypeToken<T> implementation) {
        Objects.requireNonNull(implementation);
        this.implementation = implementation;
        this.instance = null;
    }
    
    StrictBinding (T instance) {
        Objects.requireNonNull(instance);
        this.implementation = null;
        this.instance = instance;
    }

    @Override
    public QualifierBinder forItself() {
        verifyNotBound();
        if (implementation != null) {
            types.add(implementation);
        } else {
            types.add(TypeToken.forClass(instance.getClass()));
        }
        bound = true;
        return new QualifierBindingImpl();
    }

    @Override
    public QualifierBinder forSpecificTypes(Class<? super T>... types) {
        verifyNotBound();
        for (Class<? super T> t : types) {
            this.types.add(TypeToken.forClass(t));
        }
        bound = true;
        return new QualifierBindingImpl();
    }

    @Override
    public QualifierBinder forSpecificTypes(TypeToken<? super T>... types) {
        verifyNotBound();
        this.types.addAll(Arrays.asList(types));
        bound = true;
        return new QualifierBindingImpl();
    }

    TypeToken<T> getImplementation() {
        return implementation;
    }

    T getInstance() {
        return instance;
    }
    
    
    
    List<InjectionRequest> getInjectionRequests() {
        verifyBound();
        List<InjectionRequest> requests = new ArrayList<>();
        for (TypeToken<?> t : types) {
            requests.add(new InjectionRequest(t, qualifier));
        }

        return requests;
    }
    
    private void verifyNotBound() {
        if (bound) {
            throw new IllegalStateException("Binding already set");
        }
    }
    
    private void verifyBound() {
        if (!bound) {
            if (instance != null) {
                throw new IllegalStateException(
                        "Binding not complete for instance of type " + 
                        instance.getClass());
            } else {
                throw new IllegalStateException(
                        "Binding not complete for impl type " + implementation);
            }
        }
    }

    
    private class QualifierBindingImpl implements QualifierBinder {
        
        private boolean qualified = false;

        @Override
        public <Q extends Annotation> void whenQualifiedWith(Q qualifier) {
            verifyNotQualified();
            StrictBinding.this.qualifier = qualifier;
            qualified = true;
        }
        
        private void verifyNotQualified() {
            if (qualified) {
                throw new IllegalStateException("Qualifier already bound");
            }
        }
        
    }
    
}
