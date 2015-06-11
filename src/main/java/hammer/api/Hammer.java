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

import hammer.internal.Injectors;

/**
 * The entry point for the Hammer framework.
 */
public class Hammer {
    
    /**
     * Do not allow instantiation.
     */
    private Hammer() {}
    
    /**
     * Create an {@link Injector} using the given set of {@link Loader}s to load the
     * container.  For each {@link Loader} in the set, the
     * {@link Loader#load(hammer.api.Container)} method will be called exactly once
     * with the same instance of {@link Container} before the {@link Injector} is booted.
     * 
     * @param loaders the set of loaders to use
     * @return a fully booted {@link Injector}
     */
    public static Injector createInjector(Loader... loaders) {
        return Injectors.createInjector(Token.INSTANCE, loaders);
    }
    
    /**
     * Create an {@link Injector} using the given set of {@link Loader}s to load the
     * container.  For each {@link Loader} in the set, the
     * {@link Loader#load(hammer.api.Container)} method will be called exactly once
     * with the same instance of {@link Container} before the {@link Injector} is booted.
     * 
     * @param loaders the set of loaders to use
     * @return a fully booted {@link Injector}
     */
    public static Injector createInjector(Iterable<? extends Loader> loaders) {
        return Injectors.createInjector(Token.INSTANCE, loaders);
    }

    /**
     * A utility class used to protect access to creating internal {@link Injector}s.
     */
    public static class Token {
        
        private static Token INSTANCE = new Token();
        
        /**
         * Only allow private instantiation.
         */
        private Token() {}
    }
}
