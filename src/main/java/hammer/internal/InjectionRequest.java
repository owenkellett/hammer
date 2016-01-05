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
import java.util.Objects;

import hammer.api.TypeToken;

/**
 * Represents a request for a specific injection of a specific type and a specific
 * instance of a qualifier annotation.
 */
class InjectionRequest {
    
    private final TypeToken<?> type;
    private final Annotation qualifier;
    
    /**
     * Create a new {@link QualifiedInjectionRequest} with the given parameters.
     * 
     * @param type the type of the request
     * @param qualifier the qualifier annotation of the request
     */
    InjectionRequest(TypeToken<?> type, Annotation qualifier) {
        Objects.requireNonNull(type, "type cannot be null");
        Annotations.requireQualifier(qualifier);
        
        this.type = type;
        this.qualifier = qualifier;
    }
    
    /**
     * @return the type associated with this request
     */
    TypeToken<?> getType() {
        return type;
    }

    /**
     * @return the instance of a qualifier annotation associated with this request
     */
    Annotation getQualifier() {
        return qualifier;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof InjectionRequest)) {
            return false;
        }
        
        final InjectionRequest other = (InjectionRequest) obj;
        return Objects.equals(this.getType(), other.getType()) &&
               Objects.equals(this.getQualifier(), other.getQualifier());
    }
}
