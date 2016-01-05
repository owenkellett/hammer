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

/**
 * An {@code InjectionProvider} provides instances of objects typically to serve
 * {@link InjectionRequest}s.
 */
interface InjectionProvider {
    
    /**
     * Provide an instance for an {@link InjectionRequest} according to the rules of this
     * particular provider.  Depending on the implementation, multiple invocations of
     * this method may or may not return the same instance.
     * 
     * @param request the {@link InjectionRequest} to satisfy
     * @param activeScopes the list of active scopes that this provider should honor
     *                     when returning instances
     * @return an object of the appropriate type for this provider
     */
    Object provide(InjectionRequest request, InjectionContext context);

}
