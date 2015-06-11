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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import hammer.api.InjectionType;

/**
 *
 */
public class TestIntrospectorAccessProfiles {

    @Test
    public void testAccessProfileConstructorsNone() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.<InjectionType>asList());
        
        Assert.assertEquals(Introspector.AccessTypes.NONE, ap.getConstructorAccess());
    }
    
    @Test
    public void testAccessProfileConstructorsOnlyPublic() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR));
        
        Assert.assertEquals(Introspector.AccessTypes.ONLY_PUBLIC, ap.getConstructorAccess());
    }
    
    @Test
    public void testAccessProfileConstructorsOnlyNonPublic() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.NON_PUBLIC_CONSTRUCTOR));
        
        Assert.assertEquals(Introspector.AccessTypes.ONLY_NON_PUBLIC, ap.getConstructorAccess());
    }
    
    @Test
    public void testAccessProfileConstructorsAll() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR,
                              InjectionType.NON_PUBLIC_CONSTRUCTOR));
        
        Assert.assertEquals(Introspector.AccessTypes.ALL, ap.getConstructorAccess());
    }
    
    @Test
    public void testAccessProfileMembersNone() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.<InjectionType>asList());
        
        Assert.assertEquals(Introspector.AccessTypes.NONE, ap.getMemberFieldAccess());
    }
    
    @Test
    public void testAccessProfileMembersOnlyPublic() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.PUBLIC_MEMBER_FIELD));
        
        Assert.assertEquals(Introspector.AccessTypes.ONLY_PUBLIC, ap.getMemberFieldAccess());
    }
    
    @Test
    public void testAccessProfileMembersOnlyNonPublic() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.NON_PUBLIC_MEMBER_FIELD));
        
        Assert.assertEquals(Introspector.AccessTypes.ONLY_NON_PUBLIC, ap.getMemberFieldAccess());
    }
    
    @Test
    public void testAccessProfileMembersAll() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.PUBLIC_MEMBER_FIELD,
                              InjectionType.NON_PUBLIC_MEMBER_FIELD));
        
        Assert.assertEquals(Introspector.AccessTypes.ALL, ap.getMemberFieldAccess());
    }
    
    @Test
    public void testAccessProfileMethodsNone() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.<InjectionType>asList());
        
        Assert.assertEquals(Introspector.AccessTypes.NONE, ap.getMemberMethodAccess());
    }
    
    @Test
    public void testAccessProfileMethodsOnlyPublic() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.PUBLIC_MEMBER_METHOD));
        
        Assert.assertEquals(Introspector.AccessTypes.ONLY_PUBLIC, ap.getMemberMethodAccess());
    }
    
    @Test
    public void testAccessProfileMethodsOnlyNonPublic() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.NON_PUBLIC_MEMBER_METHOD));
        
        Assert.assertEquals(Introspector.AccessTypes.ONLY_NON_PUBLIC, ap.getMemberMethodAccess());
    }
    
    @Test
    public void testAccessProfileMethodsAll() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.PUBLIC_MEMBER_METHOD,
                              InjectionType.NON_PUBLIC_MEMBER_METHOD));
        
        Assert.assertEquals(Introspector.AccessTypes.ALL, ap.getMemberMethodAccess());
    }
    
    @Test
    public void testAccessProfileStaticsNone() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.<InjectionType>asList());
        
        Assert.assertEquals(Introspector.AccessTypes.NONE, ap.getStaticFieldAccess());
    }
    
    @Test
    public void testAccessProfileStaticsOnlyPublic() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.PUBLIC_STATIC_FIELD));
        
        Assert.assertEquals(Introspector.AccessTypes.ONLY_PUBLIC, ap.getStaticFieldAccess());
    }
    
    @Test
    public void testAccessProfileStaticsOnlyNonPublic() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.NON_PUBLIC_STATIC_FIELD));
        
        Assert.assertEquals(Introspector.AccessTypes.ONLY_NON_PUBLIC, ap.getStaticFieldAccess());
    }
    
    @Test
    public void testAccessProfileStaticsAll() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.PUBLIC_STATIC_FIELD,
                              InjectionType.NON_PUBLIC_STATIC_FIELD));
        
        Assert.assertEquals(Introspector.AccessTypes.ALL, ap.getStaticFieldAccess());
    }
    
    @Test
    public void testAccessProfileAll() throws Exception {
        Introspector.AccessProfile ap = Introspector.getAccessProfile(
                Arrays.asList(InjectionType.PUBLIC_CONSTRUCTOR,
                              InjectionType.NON_PUBLIC_CONSTRUCTOR,
                              InjectionType.PUBLIC_MEMBER_FIELD,
                              InjectionType.NON_PUBLIC_MEMBER_FIELD,
                              InjectionType.PUBLIC_MEMBER_METHOD,
                              InjectionType.NON_PUBLIC_MEMBER_METHOD,
                              InjectionType.PUBLIC_STATIC_FIELD,
                              InjectionType.NON_PUBLIC_STATIC_FIELD,
                              InjectionType.PUBLIC_STATIC_METHOD,
                              InjectionType.NON_PUBLIC_STATIC_METHOD));
        
        Assert.assertEquals(Introspector.AccessTypes.ALL, ap.getConstructorAccess());
        Assert.assertEquals(Introspector.AccessTypes.ALL, ap.getMemberFieldAccess());
        Assert.assertEquals(Introspector.AccessTypes.ALL, ap.getMemberMethodAccess());
        Assert.assertEquals(Introspector.AccessTypes.ALL, ap.getStaticFieldAccess());
        Assert.assertEquals(Introspector.AccessTypes.ALL, ap.getStaticMethodAccess());
    }
}
