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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hammer.api.Container.QualifierBinder;
import hammer.api.Container.StrictBinder;
import hammer.api.TypeToken;


/**
 * A {@link StrictBinding} defines the exact implementation class and {@code Scope} OR 
 * instance to be used for a specific declared type and optional {@code Qualifier}.
 */
class StrictBinding<T> extends AbstractBinding<T> implements StrictBinder<T> {

    private final List<TypeToken<?>> types = new ArrayList<>();
    
    StrictBinding (TypeToken<T> implementation) {
        super(implementation);
    }
    
    StrictBinding (T instance) {
        super(instance);
    }

    @Override
    public QualifierBinder forItself() {
        verifyNotBound();
        if (getImplementation() != null) {
            types.add(getImplementation());
        } else {
            types.add(TypeToken.forClass(getInstance().getClass()));
        }
        completeBinding();
        return getQualifierBinder();
    }

    @Override
    public QualifierBinder forSpecificTypes(Class<? super T>... types) {
        verifyNotBound();
        for (Class<? super T> t : types) {
            this.types.add(TypeToken.forClass(t));
        }
        completeBinding();
        return getQualifierBinder();
    }

    @Override
    public QualifierBinder forSpecificTypes(TypeToken<? super T>... types) {
        verifyNotBound();
        this.types.addAll(Arrays.asList(types));
        completeBinding();
        return getQualifierBinder();
    }
    
    List<InjectionRequest> getInjectionRequests() {
        verifyBound();
        List<InjectionRequest> requests = new ArrayList<>();
        for (TypeToken<?> t : types) {
            requests.add(new InjectionRequest(t, getQualifier()));
        }

        return requests;
    }
    
}
