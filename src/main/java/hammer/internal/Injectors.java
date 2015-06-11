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

import java.util.Arrays;

import hammer.api.Hammer;
import hammer.api.Injector;
import hammer.api.Loader;

/**
 *
 */
public class Injectors {

    public static Injector createInjector(Hammer.Token token, Loader... loaders) {
        return createInjector(token, Arrays.asList(loaders));
    }
    
    public static Injector createInjector(Hammer.Token token, 
                                          Iterable<? extends Loader> loaders) {
        return new InjectorImpl(loaders);
    }
}
