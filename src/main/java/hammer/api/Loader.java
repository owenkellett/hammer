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
 * A {@link Loader} is responsible for loading type bindings into a {@link Container}.
 */
public interface Loader {

    /**
     * Load the required type bindings defined by this loader into the given 
     * {@link Container}.
     * 
     * @param container the container to load
     */
    void load(Container container);
}
