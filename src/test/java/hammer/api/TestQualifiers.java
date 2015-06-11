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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Named;
import javax.inject.Qualifier;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class TestQualifiers {
    
    @Test
    public void testNamedEquality() throws Exception {
        Named named1 = Annotated1.class.getAnnotation(Named.class);
        Named named2 = Annotated2.class.getAnnotation(Named.class);

        Assert.assertEquals(named1, Qualifiers.named("test"));
        Assert.assertEquals(named2, Qualifiers.named("test2"));
        Assert.assertNotEquals(named1, Qualifiers.named("blah"));
        Assert.assertEquals(Qualifiers.named("test"), named1);
    }
    
    @Test
    public void testQualifierEquality() throws Exception {
        MyQualifier mine = MyAnnotated.class.getAnnotation(MyQualifier.class);
        
        Assert.assertEquals(mine, Qualifiers.qualifier(MyQualifier.class));
        Assert.assertEquals(Qualifiers.qualifier(MyQualifier.class), mine);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNoQualifier() throws Exception {
        Qualifiers.qualifier(NoQualifier.class);
    }
    
    @Named("test")
    public static class Annotated1 {}
    
    @Named("test2")
    public static class Annotated2 {}
    
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public static @interface MyQualifier {}
    
    @MyQualifier
    public static class MyAnnotated {}
    
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface NoQualifier {}
    
    @NoQualifier
    public static class MyNoAnnotated {}
}
