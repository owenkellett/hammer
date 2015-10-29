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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import hammer.api.InjectionType;
import hammer.api.TypeToken;


/**
 * Utilities for introspection on classes in order to extract injection configuration.
 */
class Introspector {
    
    /**
     * Produces an {@link InjectionProfile} which represents all of the injectable
     * elements of the given type which are also compatible with the given
     * {@link AccessProfile}.
     * 
     * @param type the type to introspect
     * @param accessProfile the {@link AccessProfile} of elements to consider
     * @return an {@link InjectionProfile} for the given type
     */
    static InjectionProfile getInjectionProfile(TypeToken<?> type, 
                                                AccessProfile accessProfile) {
        if (Modifier.isAbstract(type.getRawClass().getModifiers())) {
            throw new IllegalArgumentException("Abstract classes cannot be instantiated");
        }
        
        Constructor<?> constructor = getInjectableConstructor(
                type.getRawClass(),
                accessProfile.getConstructorAccess());
        
        InjectionProfile profile = new InjectionProfile(type, constructor);
        addInjectables(profile, accessProfile);
        
        return profile;
    }
    
    /**
     * Convert the collection of {@link InjectionType}s into an {@link AccessProfile} that
     * represents the types of injectable elements to consider during introspection of
     * a type.
     * 
     * @param injectionTypes the collection of {@link InjectionType}s to consider
     * @return an {@link AccessProfile} equivalent to the supported injection types
     */
    static AccessProfile getAccessProfile(Collection<InjectionType> injectionTypes) {
        return new AccessProfile(injectionTypes);
    }
    
    
    
    private static Constructor<?> getInjectableConstructor(Class<?> type,
                                                           AccessTypes access) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        
        // check for default injectable constructor
        if (access.includesPublic() && 
            constructors.length == 1 &&
            Modifier.isPublic(constructors[0].getModifiers()) &&
            constructors[0].getParameterTypes().length == 0) {
            return constructors[0];
        }
        
        // look for injectable constructor
        Constructor<?> candidate = null;
        for (Constructor<?> constructor : constructors) {
            if (isInjectable(constructor, access)) {
                if (candidate != null) {
                    throw new IllegalArgumentException(
                            "Only one injectable constructor allowed");
                }
                candidate = constructor;
            }
        }
        
        if (candidate == null) {
            throw new IllegalArgumentException("No injectable constructor found");
        }
        
        return makeAccessible(candidate);
    }
    
    private static void addInjectables(InjectionProfile profile, AccessProfile access) {
        Class<?> clss = profile.getType().getRawClass();
        
        MethodSignatures signatures = new MethodSignatures();
        for (Class<?> c = clss; c != null; c = c.getSuperclass()) {
            // add methods
            for (Method method : c.getDeclaredMethods()) {
                // no abstract or static methods
                if (Modifier.isAbstract(method.getModifiers())) {
                    continue;
                }
                // no methods with type parameters
                if (method.getTypeParameters().length > 0) {
                    continue;
                }
                
                if (Modifier.isStatic(method.getModifiers())) {
                    if (isInjectable(method, access.staticMethodAccess)) {
                        profile.addFirstStatic(makeAccessible(method));
                    }
                } else {
                    if (isInjectable(method, access.memberMethodAccess) && 
                            !signatures.isOverridden(method)) {
                        profile.addFirstMember(makeAccessible(method));
                    }
                    signatures.add(method);
                }
            }
            
            // add fields
            for (Field field : c.getDeclaredFields()) {
                // no final fields
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                
                if (Modifier.isStatic(field.getModifiers())) {
                    if (isInjectable(field, access.staticFieldAccess)) {
                        profile.addFirstStatic(makeAccessible(field));
                    }
                } else {
                    if (isInjectable(field, access.memberFieldAccess)) {
                        profile.addFirstMember(makeAccessible(field));
                    }
                }
            }
        }
    }
    
    private static <A extends AccessibleObject & Member> boolean isInjectable(
            A element, AccessTypes access) {
        if ((access.includesPublic() && Modifier.isPublic(element.getModifiers())) ||
            (access.includesNonPublic() && !Modifier.isPublic(element.getModifiers()))) {
                
            if (element.getAnnotation(Inject.class) != null) {
                return true;
            }
            
        }
        
        return false;
    }
    
    private static <A extends AccessibleObject & Member > A makeAccessible(A element) {
        if (!Modifier.isPublic(element.getModifiers())) {
            element.setAccessible(true);
        }
        
        return element;
    }
    
    private static class MethodSignatures {
        private final List<Method> methods = new ArrayList<>();
        private MethodSignatures() {}
        
        private void add(Method method) {
            methods.add(method);
        }
        
        private boolean isOverridden(Method candidate) {
            for (Method m : methods) {
                // check to see if the signature is the same
                if (Objects.equals(m.getName(), candidate.getName()) &&
                    Objects.deepEquals(m.getParameterTypes(), candidate.getParameterTypes()) &&
                    Objects.equals(m.getReturnType(), candidate.getReturnType())) {

                    // check to see if the candidate method is not visible from the
                    // possibly overridding method
                    if (Modifier.isPrivate(candidate.getModifiers())) {
                        continue;
                    }
                    if (!Modifier.isPublic(candidate.getModifiers()) &&
                        !Modifier.isProtected(candidate.getModifiers()) &&
                        !Modifier.isPrivate(candidate.getModifiers()) &&
                        !Objects.equals(m.getDeclaringClass().getPackage(), 
                                        candidate.getDeclaringClass().getPackage())) {
                        continue;
                    }
                    
                    return true;
                }
            }
            
            return false;
        }
    }
    
    
    /** === Utility classes === **/
    
    /**
     * An enumeration of possible access types that represents whether to include public
     * and/or non-public elements for a particular type of injectable entity.
     */
    static enum AccessTypes {
        /**
         * Include both public and non-public elements.
         */
        ALL (true, true),
        
        /**
         * Only include public elements.
         */
        ONLY_PUBLIC (true, false),
        
        /**
         * Only include non-public elements.
         */
        ONLY_NON_PUBLIC (false, true),
        
        /**
         * Don't include any elements at all.
         */
        NONE (false, false);
        
        private final boolean includesPublic;
        private final boolean includesNonPublic;
        
        private AccessTypes(boolean includesPublic, boolean includesNonPublic) {
            this.includesPublic = includesPublic;
            this.includesNonPublic = includesNonPublic;
        }
        
        /**
         * @return {@code true} if public elements are to be included
         */
        boolean includesPublic() {
            return includesPublic;
        }
        
        /**
         * @return {@code true} if non-public elements are to be included
         */
        boolean includesNonPublic() {
            return includesNonPublic;
        }
        
        private static AccessTypes get(boolean includesPublic, 
                                       boolean includesNonPublic) {
            if (includesPublic) {
                if (includesNonPublic) {
                    return ALL;
                } else {
                    return ONLY_PUBLIC;
                }
            } else {
                if (includesNonPublic) {
                    return ONLY_NON_PUBLIC;
                } else {
                    return NONE;
                }
            }
        }
    }
    
    /**
     * An {@link AccessProfile} represents a collection of {@link AccessTypes} to
     * indicate which elements of a class should be considered for introspection for
     * injection purposes.
     */
    static class AccessProfile {
        
        private final AccessTypes constructorAccess;
        private final AccessTypes memberFieldAccess;
        private final AccessTypes memberMethodAccess;
        private final AccessTypes staticFieldAccess;
        private final AccessTypes staticMethodAccess;
        
        private AccessProfile(Collection<InjectionType> injectionTypes) {
            constructorAccess = AccessTypes.get(
                    injectionTypes.contains(InjectionType.PUBLIC_CONSTRUCTOR),
                    injectionTypes.contains(InjectionType.NON_PUBLIC_CONSTRUCTOR));
            memberFieldAccess = AccessTypes.get(
                    injectionTypes.contains(InjectionType.PUBLIC_MEMBER_FIELD),
                    injectionTypes.contains(InjectionType.NON_PUBLIC_MEMBER_FIELD));
            memberMethodAccess = AccessTypes.get(
                    injectionTypes.contains(InjectionType.PUBLIC_MEMBER_METHOD),
                    injectionTypes.contains(InjectionType.NON_PUBLIC_MEMBER_METHOD));
            staticFieldAccess = AccessTypes.get(
                    injectionTypes.contains(InjectionType.PUBLIC_STATIC_FIELD),
                    injectionTypes.contains(InjectionType.NON_PUBLIC_STATIC_FIELD));
            staticMethodAccess = AccessTypes.get(
                    injectionTypes.contains(InjectionType.PUBLIC_STATIC_METHOD),
                    injectionTypes.contains(InjectionType.NON_PUBLIC_STATIC_METHOD));
        }
        
        /**
         * @return the {@link AccessTypes} of constructors to consider.
         */
        AccessTypes getConstructorAccess() {
            return constructorAccess;
        }
        
        /**
         * @return the {@link AccessTypes} of member fields to consider.
         */
        AccessTypes getMemberFieldAccess() {
            return memberFieldAccess;
        }
        
        /**
         * @return the {@link AccessTypes} of member methods to consider.
         */
        AccessTypes getMemberMethodAccess() {
            return memberMethodAccess;
        }
        
        /**
         * @return the {@link AccessTypes} of static fields to consider.
         */
        AccessTypes getStaticFieldAccess() {
            return staticFieldAccess;
        }
        
        /**
         * @return the {@link AccessTypes} of static methods to consider.
         */
        AccessTypes getStaticMethodAccess() {
            return staticMethodAccess;
        }
    }

    /**
     * An {@link InjectionProfile} represents a collection of injectable elements of a
     * particular type including constructors, fields, and methods.
     */
    static class InjectionProfile {
        
        private final TypeToken<?> type;
        private final Constructor constructor;
        private final Deque<AccessibleObject> injectableMembers;
        private final Deque<AccessibleObject> injectableStatics;
        
        private InjectionProfile(TypeToken<?> type, Constructor constructor) {
            this.type = type;
            this.constructor = constructor;
            this.injectableMembers = new LinkedList<>();
            this.injectableStatics = new LinkedList<>();
        }
        
        TypeToken<?> getType() {
            return type;
        }
        
        Constructor<?> getInjectableConstructor() {
            return constructor;
        }
        
        Iterable<AccessibleObject> getInjectableMembers() {
            return injectableMembers;
        }
        
        Iterable<AccessibleObject> getInjectableStatics() {
            return injectableStatics;
        }
        
        private void addFirstMember(AccessibleObject member) {
            injectableMembers.addFirst(member);
        }
        
        private void addLastMember(AccessibleObject member) {
            injectableMembers.addLast(member);
        }
        
        private void addFirstStatic(AccessibleObject stat) {
            injectableStatics.addFirst(stat);
        }
        
        private void addLastStatic(AccessibleObject stat) {
            injectableStatics.addLast(stat);
        }

    }
    
}
