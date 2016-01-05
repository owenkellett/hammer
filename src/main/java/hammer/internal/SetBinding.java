
package hammer.internal;

import java.lang.annotation.Annotation;

import hammer.api.Container.QualifierBinder;
import hammer.api.Container.SetMemberBinder;
import hammer.api.TypeToken;

/**
 *
 */
class SetBinding<V> extends AbstractCollectionBinding<V> implements SetMemberBinder<V> {
    
    private TypeToken<?> setType;
    
    SetBinding (TypeToken<V> implementation, Annotation scope) {
        super(implementation, scope);
    }
    
    SetBinding (V instance, Annotation scope) {
        super(instance, scope);
    }

    @Override
    public QualifierBinder forElementType(Class<? super V> elementType) {
        return forElementType(TypeToken.forClass(elementType));
    }

    @Override
    public QualifierBinder forElementType(TypeToken<? super V> elementType) {
        verifyNotBound();
        this.setType = CollectionTypes.setType(elementType);
        this.completeBinding();
        return getQualifierBinder();
    }
    
    InjectionRequest getSetInjectionRequest() {
        verifyBound();
        
        return new InjectionRequest(setType, getQualifier());
    }
}
