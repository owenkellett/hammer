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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * A {@link TypeToken} is used to combat type erasure and allow introspection of 
 * parameterized types.  As a declared abstract class, an instance of {@link TypeToken} by
 * definition, must be a subclass.  Subclasses have access to complete type information of
 * declared superclasses at runtime so the type of T of an instantiated {@link TypeToken}
 * is always reifiable.
 * 
 * A {@link TypeToken} can be instantiated for any type bound to T with the following
 * exceptions:
 * <ul>
 *   <li>Type variables are not allowed in any form (i.e. 
 *       {@code new TypeToken<Set<T>>() {}} is not allowed)</li>
 *   <li>Inner classes and local classes are not allowed to be used as TypeTokens</li>
 * </ul>
 */
public abstract class TypeToken<T> {
    private final Type type;
    private Class<?> rawClass;

    /**
     * Constructs a new {@code TypeToken}, extracting the type information from the
     * superclass for easy introspection.
     */
    protected TypeToken() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
            verifyInvariants(type);
            setRawClass(type);
        } else {
            throw new IllegalArgumentException("Missing type parameter");
        }
    }
    
    /**
     * Constructs a new {@link TypeToken} to represent the given class type.
     * 
     * @param c the type to represent as a TypeToken
     */
    private TypeToken(Class<?> c) {
        type = c;
        rawClass = c;
        verifyInvariants(type);
    }
    
    /**
     * Constructs a new {@link TypeToken} to represent the given type.
     * 
     * @param t the type to represent as a TypeToken
     */
    private TypeToken(Type t) {
        type = t;
        verifyInvariants(type);
        setRawClass(type);
    }
    
    private void setRawClass(Type type) {
        if (type instanceof Class<?>) {
            rawClass = (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            rawClass = (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            rawClass = getClass().getSuperclass();
        } else if (type instanceof WildcardType) {
            throw new IllegalArgumentException("Raw wildcard type not allowed : " + 
                                               type);
        } else {
            throw new IllegalArgumentException("Invalid type parameter : " + type);
        }
    }
    
    /**
     * Recursively ensure that the given type is not a type variable and does not contain
     * any type variables as parameterized type arguments.  Also ensure that all declared
     * types are not inner classes nor local classes nor anonymous classes.
     * 
     * @param type the type to verify
     * @throws IllegalArgumentException if the type contains a type variable in its
     *                                  declaration
     */
    private static void verifyInvariants(Type type) {
        if (type instanceof TypeVariable) {
            throw new IllegalArgumentException ("Type variables not allowed : " + type);
        } else if (type instanceof GenericArrayType) {
            verifyInvariants (((GenericArrayType) type).getGenericComponentType());
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            verifyInvariants (pt.getOwnerType());
            verifyInvariants (pt.getRawType());
            for (Type argument : pt.getActualTypeArguments()) {
                verifyInvariants (argument);
            }
        } else if (type instanceof WildcardType) {
            WildcardType wt = (WildcardType) type;
            for (Type bound : wt.getLowerBounds()) {
                verifyInvariants (bound);
            }
            for (Type bound : wt.getUpperBounds()) {
                verifyInvariants (bound);
            }
        } else if (type instanceof Class<?>) {
            Class<?> rawType = (Class<?>) type;
            if (rawType.isLocalClass()) {
                throw new IllegalArgumentException("Local classes not allowed : " + 
                                                   rawType);
            }
            if (rawType.isMemberClass() && !Modifier.isStatic(rawType.getModifiers())) {
                throw new IllegalArgumentException("Inner classes not allowed : " + 
                                                   rawType);
            }
            if (rawType.isAnonymousClass()) {
                throw new IllegalArgumentException("Anonymous classes not allowed : " +
                                                   rawType);
            }
        } 
    }
    
    /**
     * Creates a {@link TypeToken} for the given class type.
     * 
     * @param <T> the type
     * @param c the class type to use for the token
     * @return a TypeToken representing the type of the given class
     */
    public static <T> TypeToken<T> forClass(Class<T> c) {
        return new TypeToken<T>(c) {};
    }
    
    /**
     * Creates a {@link TypeToken} for the given type.
     * 
     * @param type the type to use for the token
     * @return a TypeToken representing the type
     */
    public static TypeToken<?> forType(Type type) {
        return new TypeToken(type) {};
    }

    /**
     * @return the type associated with this instance's instantiated type parameter
     */
    public Type getType() {
        return type;
    }

    /**
     * @return the raw class associated with this instance's instantiated type parameter
     */
    public Class<?> getRawClass() {
        return rawClass;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TypeToken) {
            return getType().toString().equals(((TypeToken) o).getType().toString()) &&
                   getRawClass().equals(((TypeToken) o).getRawClass());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getType().toString().hashCode();
    }

    @Override
    public String toString() {
        return getType().toString();
    }

}
