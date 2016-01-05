
package hammer.internal;

import hammer.api.TypeToken;

/**
 * An {@code InjectionInstantiator} is responsible for performing the actual instantiation
 * of objects when required for an {@code InjectionRequest}.
 */
interface InjectionInstantiator {

    Object instantiate(InjectionContext context);
    
    TypeToken<?> getType();
}
