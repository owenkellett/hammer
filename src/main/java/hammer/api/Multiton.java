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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Scope;

/**
 * A {@link Multiton} is a special type of {@code @Scope} annotation that is given
 * special semantic meaning when used with an {@link Injector}.  A {@link Multiton}
 * annotation has distinct meaning in two contexts:
 * <ol>
 * <li>When added as an annotation on a class, and that class is added as an
 *     implementation type in an {@link Injector}.  In this scenario, when injecting
 *     instances of the given implementation type, the injector treats the type as a
 *     muliton, associating one instance per {@code Qualifier}.  In other words, for
 *     every injection request that resolves to the implementation type and
 *     is associated with a specific {@code Qualifier} instance, the same instance will
 *     be used.</li>
 * <li>When added as an annotation on an annotation type that is also annotated
 *     with the {@code Scope} annotation.  In this scenario, if the annotated
 *     {@code Scope} annotation is used as an active scope in an {@link Injector}, the
 *     custom annotation will be treated as a multiton annotation, with the same
 *     behavior as the {@link Multiton} scope.</li>
 * </ol>
 */
@Target(value = {ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Multiton
@Scope
public @interface Multiton {

}
