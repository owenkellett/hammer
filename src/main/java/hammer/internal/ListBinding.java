
package hammer.internal;

import java.lang.annotation.Annotation;

import hammer.api.Container.ListMemberBinder;
import hammer.api.Container.QualifierBinder;
import hammer.api.TypeToken;

/**
 *
 */
class ListBinding<V> extends AbstractCollectionBinding<V> implements ListMemberBinder<V> {
    
    private TypeToken<?> listType;
    
    ListBinding (TypeToken<V> implementation, Annotation scope) {
        super(implementation, scope);
    }
    
    ListBinding (V instance, Annotation scope) {
        super(instance, scope);
    }

    @Override
    public QualifierBinder forElementType(Class<? super V> elementType) {
        return forElementType(TypeToken.forClass(elementType));
    }

    @Override
    public QualifierBinder forElementType(TypeToken<? super V> elementType) {
        verifyNotBound();
        this.listType = CollectionTypes.listType(elementType);
        this.completeBinding();
        return getQualifierBinder();
    }
    
    InjectionRequest getListInjectionRequest() {
        verifyBound();
        
        return new InjectionRequest(listType, getQualifier());
    }
}
