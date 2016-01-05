
package hammer.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hammer.api.TypeToken;

/**
 *
 */
class CollectionTypes {

    static TypeToken<?> mapType(TypeToken<?> keyType, TypeToken<?> valueType) {
        return TypeToken.forType(new ParameterizedTypeImpl(
                Map.class, keyType.getType(), valueType.getType()));
    }
    
    static TypeToken<?> setType(TypeToken<?> elementType) {
        return TypeToken.forType(new ParameterizedTypeImpl(
                Set.class, elementType.getType()));
    }
    
    static TypeToken<?> listType(TypeToken<?> elementType) {
        return TypeToken.forType(new ParameterizedTypeImpl(
                List.class, elementType.getType()));
    }
    
    private static class ParameterizedTypeImpl implements ParameterizedType {
        
        private final Class<?> rawType;
        private final Type[] typeArguments;
        
        private ParameterizedTypeImpl(Class<?> rawType, Type... typeArguments) {
            this.rawType = rawType;
            this.typeArguments = typeArguments;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return typeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
        
        @Override public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(rawType.getName());
            
            if (typeArguments.length == 0) {
                return stringBuilder.toString();
            }
            
            stringBuilder.append("<").append(getTypeName(typeArguments[0]));
            for (int i = 1; i < typeArguments.length; i++) {
                stringBuilder.append(", ").append(getTypeName(typeArguments[i]));
            }
            return stringBuilder.append(">").toString();
        }
        
        private static String getTypeName(Type type) {
            if (type instanceof Class) {
                return ((Class<?>) type).getName();
            } else {
                return type.toString();
            }
        }
        
    }
}
