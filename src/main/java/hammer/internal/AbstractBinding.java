
package hammer.internal;

import java.lang.annotation.Annotation;
import java.util.Objects;

import hammer.api.Container.QualifierBinder;
import hammer.api.TypeToken;

/**
 *
 */
abstract class AbstractBinding<T> {
    
    private final TypeToken<T> implementation;
    private final T instance;
    
    private QualifierBindingImpl qualifier;
    private boolean bound = false;
    
    AbstractBinding (TypeToken<T> implementation) {
        Objects.requireNonNull(implementation);
        this.implementation = implementation;
        this.instance = null;
    }
    
    AbstractBinding (T instance) {
        Objects.requireNonNull(instance);
        this.implementation = null;
        this.instance = instance;
    }
    
    /** === package-private API surface === **/

    TypeToken<T> getImplementation() {
        return implementation;
    }

    T getInstance() {
        return instance;
    }
    
    Annotation getQualifier() {
        if (qualifier != null) {
            return qualifier.getQualifier();
        } else {
            return null;
        }
    }
    
    
    /** === Utility methods and classes === **/
    
    protected void completeBinding() {
        this.bound = true;
    }
    
    protected void verifyNotBound() {
        if (bound) {
            if (instance != null) {
                throw new IllegalStateException(
                        "Binding already set for instance of type " + 
                        instance.getClass());
            } else {
                throw new IllegalStateException(
                        "Binding already set for impl type " + implementation);
            }
        }
    }
    
    protected void verifyBound() {
        if (!bound) {
            if (instance != null) {
                throw new IllegalStateException(
                        "Binding not complete for instance of type " + 
                        instance.getClass());
            } else {
                throw new IllegalStateException(
                        "Binding not complete for impl type " + implementation);
            }
        }
    }

    protected QualifierBinder getQualifierBinder() {
        if (qualifier == null) {
            this.qualifier = new QualifierBindingImpl();
        }
        
        return this.qualifier;
    }
    
    private class QualifierBindingImpl implements QualifierBinder {
        
        private Annotation qualifier;
        private boolean qualified;

        @Override
        public <Q extends Annotation> void whenQualifiedWith(Q qualifier) {
            verifyNotQualified();
            Annotations.requireQualifier(qualifier);
            this.qualifier = qualifier;
            qualified = true;
        }
        
        private Annotation getQualifier() {
            return qualifier;
        }
        
        private void verifyNotQualified() {
            if (qualified) {
                throw new IllegalStateException("Qualifier already bound");
            }
        }
        
    }

}
