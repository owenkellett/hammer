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

import javax.inject.Qualifier;
import javax.inject.Scope;

/**
 * Utility methods to work with {@code Scope} and {@code Qualifier} annotations.
 */
class Annotations {

    /**
     * Require that the given annotation is annotated with {@code Qualifier}.
     * 
     * @param annotation the annotation to check
     * @throws IllegalArgumentException if the given annotation is not annotated with
     *                                  {@code Qualifier}
     */
    static void requireQualifier(Annotation annotation) {
        Objects.requireNonNull(annotation.annotationType().getAnnotation(Qualifier.class),
                               "Annotation must be annotated with @Qualifier");
    }
    
    /**
     * Require that the given annotation is annotated with {@code Scope}.
     * 
     * @param annotation the annotation to check
     * @throws IllegalArgumentException if the given annotation is not annotated with
     *                                  {@code Scope}
     */
    static void requireScope(Annotation annotation) {
        Objects.requireNonNull(annotation.annotationType().getAnnotation(Scope.class),
                               "Annotation must be annotated with @Scope");
    }
}
