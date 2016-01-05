
package hammer.internal;

import java.lang.annotation.Annotation;

import hammer.api.TypeToken;

/**
 *
 */
class AbstractCollectionBinding<T> extends AbstractBinding<T> {
    
    private final Annotation scope;

    AbstractCollectionBinding(TypeToken<T> implementation, Annotation scope) {
        super(implementation);
        this.scope = scope;
    }

    AbstractCollectionBinding(T instance, Annotation scope) {
        super(instance);
        this.scope = scope;
    }
    
    Annotation getScope() {
        return scope;
    }

}
