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
 * Thrown when an injection attempt fails while trying to create an instance
 * with an injectable constructor or while invoking an injectable method.
 */
public class InjectionException extends RuntimeException {

    /**
     * Creates an instance of {@code InjectionException} 
     * without detail message.
     */
    public InjectionException() {
    }

    /**
     * Constructs an instance of {@code InjectionException} 
     * with the specified detail message.
     *
     * @param msg the detail message
     */
    public InjectionException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of {@code InjectionException} 
     * with the specified route cause
     *
     * @param t the route cause of the exception
     */
    public InjectionException(Throwable t) {
        super(t);
    }

    /**
     * Constructs an instance of {@code InjectionException} 
     * with the specified route cause and detail message
     *
     * @param msg the detail message
     * @param t the route cause of the exception
     */
    public InjectionException(String msg, Throwable t) {
        super(msg, t);
    }
}
