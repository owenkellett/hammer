
package hammer.internal;


import java.lang.annotation.Annotation;

import hammer.api.Container;
import hammer.api.Container.MapMemberBinder;
import hammer.api.Container.MapMemberKeyBinder;
import hammer.api.TypeToken;

/**
 *
 */
class MapBinding<V> extends AbstractCollectionBinding<V> implements MapMemberBinder<V> {
    
    private TypeToken<?> mapType;
    private KeyBinderImpl<?> keyBinder;
    

    MapBinding (TypeToken<V> implementation, Annotation scope) {
        super(implementation, scope);
    }
    
    MapBinding (V instance, Annotation scope) {
        super(instance, scope);
    }

    @Override
    public <K> MapMemberKeyBinder<K> forMapType(Class<K> keyType, 
                                                Class<? super V> valueType) {
        return forMapType(TypeToken.forClass(keyType), TypeToken.forClass(valueType));
    }

    @Override
    public <K> MapMemberKeyBinder<K> forMapType(TypeToken<K> keyType, 
                                                TypeToken<? super V> valueType) {
        verifyMapTypeNotSet();
        this.mapType = CollectionTypes.mapType(keyType, valueType);

        return getKeyBinder();
    }
    
    private <K> KeyBinderImpl<K> getKeyBinder() {
        KeyBinderImpl<K> kb = new KeyBinderImpl<>();
        this.keyBinder = kb;
        return kb;
    }
    
    protected void verifyMapTypeNotSet() {
        if (this.mapType != null) {
            if (getInstance() != null) {
                throw new IllegalStateException(
                        "Map type already chosen for map binding for instance of type " + 
                        getInstance().getClass());
            } else {
                throw new IllegalStateException(
                        "Map type already chosen for map binding for impl type " + 
                        getImplementation());
            }
        }
    }
    
    InjectionRequest getMapInjectionRequest() {
        verifyBound();
        
        return new InjectionRequest(mapType, getQualifier());
    }
    
    Object getKey() {
        verifyBound();
        
        return keyBinder.getKey();
    }
    
    private class KeyBinderImpl<K> implements MapMemberKeyBinder<K> {
        
        private K key;

        @Override
        public Container.QualifierBinder withKey(K key) {
            verifyNotBound();
            this.key = key;
            MapBinding.this.completeBinding();
            return MapBinding.this.getQualifierBinder();
        }
        
        private K getKey() {
            return key;
        }
    }
    
}
