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

import junit.framework.Test;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.Engine;
import org.atinject.tck.auto.FuelTank;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.V8Engine;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

/**
 *
 */
@RunWith(AllTests.class)
public class TestTck {
    
    public static Test suite() {
        Injector hammer = Hammer.createInjector(new TckLoader());
        Car car = hammer.getInstance(Car.class);
        return Tck.testsFor(car,
                true /* supportsStatic */,
                true /* supportsPrivate */);
    }
    
    public static class TckLoader implements Loader {

        @Override
        public void load(Container container) {
            container.addImplType(Convertible.class).asStrictBinding().forSpecificTypes(Car.class);
            container.addImplType(DriversSeat.class).asStrictBinding().forSpecificTypes(Seat.class).whenQualifiedWith(Qualifiers.qualifier(Drivers.class));
            container.addImplType(Seat.class).asStrictBinding().forItself();
            container.addImplType(Tire.class).asStrictBinding().forItself();
            container.addImplType(V8Engine.class).asStrictBinding().forSpecificTypes(Engine.class);
            container.addImplType(SpareTire.class).asStrictBinding().forSpecificTypes(Tire.class).whenQualifiedWith(Qualifiers.named("spare"));
            container.addImplType(Cupholder.class).asStrictBinding().forItself();
            container.addImplType(SpareTire.class).asStrictBinding().forItself();
            container.addImplType(FuelTank.class).asStrictBinding().forItself();
            
            container.configureStaticInjections(Convertible.class);
            container.configureStaticInjections(DriversSeat.class);
            container.configureStaticInjections(Seat.class);
            container.configureStaticInjections(Tire.class);
            container.configureStaticInjections(V8Engine.class);
            container.configureStaticInjections(SpareTire.class);
            container.configureStaticInjections(Cupholder.class);
            container.configureStaticInjections(FuelTank.class);
        }
        
    }
    
}
