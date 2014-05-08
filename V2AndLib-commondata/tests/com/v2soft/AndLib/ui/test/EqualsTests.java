package com.v2soft.AndLib.ui.test;

import com.v2soft.AndLib.containers.TTLMap;
import com.v2soft.AndLib.dao.EqualTools;
import com.v2soft.AndLib.filecache.FileCache;
import com.v2soft.AndLib.filecache.JavaHashFactory;
import com.v2soft.AndLib.filecache.MD5CacheFactory;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class EqualsTests {

    @Before
    public void setUp() {
    }

	@Test
	public void testEquals() {
        TestObject t1 = new TestObject(UUID.randomUUID().toString(), null, 10);
        TestObject t2 = new TestObject(null, UUID.randomUUID().toString(), 10);
        TestObject t3 = new TestObject(t1.string1, t1.string2, 10);
        assertTrue(EqualTools.checkObjects(t1, t3));
        assertFalse(EqualTools.checkObjects(t1, t2));
        assertFalse(EqualTools.checkObjects(t3, t2));
	}

    private class TestObject {
        String string1;
        String string2;
        int int1;

        public TestObject(String s1, String s2, int i) {
            string1 = s1;
            string2 = s2;
            int1 = i;
        }

        @Override
        public boolean equals(Object o) {
            if ( o == null ) {
                return false;
            }
            if ( o instanceof TestObject ) {
                TestObject obj = (TestObject) o;
                boolean result = int1 == obj.int1;
                result &= EqualTools.checkObjects(string1, obj.string1);
                result &= EqualTools.checkObjects(string2, obj.string2);
                return result;
            }
            return super.equals(o);
        }
    }

}
