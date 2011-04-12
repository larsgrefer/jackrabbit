/*
 * Copyright 1997-2011 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */
package org.apache.jackrabbit.commons.privilege;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * <code>PrivilegeReadTest</code>...
 */
public class PrivilegeHandlerTest extends TestCase {

    /*
    <privilege name="foo:testRead"/>
    <privilege name="foo:testWrite"/>
    <privilege abstract="true" name="foo:testAbstract"/>
    <privilege abstract="false" name="foo:testNonAbstract"/>
    <privilege name="foo:testAll">
        <contains name="foo:testRead"/>
        <contains name="foo:testWrite"/>
    </privilege>

     */
    public static PrivilegeDefinition DEF_READ = new PrivilegeDefinition("foo:testRead", false, null);
    public static PrivilegeDefinition DEF_WRITE = new PrivilegeDefinition("foo:testWrite", false, null);
    public static PrivilegeDefinition DEF_ABSTRACT = new PrivilegeDefinition("foo:testAbstract", true, null);
    public static PrivilegeDefinition DEF_NON_ABSTRACT = new PrivilegeDefinition("foo:testNonAbstract", false, null);
    public static PrivilegeDefinition DEF_ALL = new PrivilegeDefinition("foo:testAll", false, new String[]{"foo:testRead", "foo:testWrite"});
    public static PrivilegeDefinition[] DEF_EXPECTED = new PrivilegeDefinition[]{
            DEF_READ,
            DEF_WRITE,
            DEF_ABSTRACT,
            DEF_NON_ABSTRACT,
            DEF_ALL
    };

    public void testRead() throws Exception {
        InputStream in = getClass().getResourceAsStream("readtest.xml");

        PrivilegeDefinitionReader reader = new PrivilegeDefinitionReader(in, "text/xml");

        Map<String, PrivilegeDefinition> defs = new HashMap<String, PrivilegeDefinition>();
        for (PrivilegeDefinition def: reader.getPrivilegeDefinitions()) {
            defs.put(def.getName(), def);
        }
        for (PrivilegeDefinition def: DEF_EXPECTED) {
            PrivilegeDefinition e = defs.remove(def.getName());
            assertNotNull("Definition " + def.getName() + " missing");
            assertEquals("Definition mismatch.", def,  e);
        }
        assertTrue("Not all definitions present", defs.isEmpty());

        // check for namespace
        String fooUri = reader.getNamespaces().get("foo");
        assertEquals("Namespace included", "http://www.foo.com/1.0", fooUri);
    }

    public void testWrite() throws Exception {

        PrivilegeDefinitionWriter writer = new PrivilegeDefinitionWriter("text/xml");
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("foo", "http://www.foo.com/1.0");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.writeDefinitions(out, DEF_EXPECTED, namespaces);

        String result = out.toString("utf-8").trim();

        byte[] buffer = new byte[0x10000];
        int read = getClass().getResourceAsStream("writetest.xml").read(buffer);
        String expected = new String(buffer, 0, read, "utf-8").trim();
        assertEquals("Write", expected, result);
    }

}