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

/**
 * An enumeration of the supported injection types by {@code Injector}s.
 */
public enum InjectionType {

    /**
     * Injection via {@code @Inject} annotated constructors or constructor parameters
     * on public constructors.
     */
    PUBLIC_CONSTRUCTOR,
    
    /**
     * Injection via {@code @Inject} annotated constructors or constructor parameters
     * on non-public constructors.
     */
    NON_PUBLIC_CONSTRUCTOR,
    
    /**
     * Injection via {@code @Inject} annotated public member methods.
     */
    PUBLIC_MEMBER_METHOD,
    
    /**
     * Injection via {@code @Inject} annotated non-public member methods.
     */
    NON_PUBLIC_MEMBER_METHOD,
    
    /**
     * Injection via {@code @Inject} annotated public member fields.
     */
    PUBLIC_MEMBER_FIELD,
    
    /**
     * Injection via {@code @Inject} annotated non-public member fields.
     */
    NON_PUBLIC_MEMBER_FIELD,
    
    /**
     * Injection via {@code @Inject} annotated public static methods.
     */
    PUBLIC_STATIC_METHOD,
    
    /**
     * Injection via {@code @Inject} annotated non-public static methods.
     */
    NON_PUBLIC_STATIC_METHOD,
    
    /**
     * Injection via {@code @Inject} annotated public static fields.
     */
    PUBLIC_STATIC_FIELD,
    
    /**
     * Injection via {@code @Inject} annotated non-public static fields.
     */
    NON_PUBLIC_STATIC_FIELD;
    
}
