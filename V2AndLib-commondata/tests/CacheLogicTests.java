import com.v2soft.AndLib.containers.TTLMap;
import com.v2soft.AndLib.filecache.FileCache;
import com.v2soft.AndLib.filecache.JavaHashFactory;
import com.v2soft.AndLib.filecache.MD5CacheFactory;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class CacheLogicTests  {
	private static final String MD5_NAME = "eef5051286bff7a55c25645bf44606aa";
	private static final String JAVA_HASH_NAME = "784584757";
	String inputName = "http://developer.android.com/reference/android/app/Activity.html";

    @Before
    public void setUp() {
    }

	@Test
	public void testNamesFactory() throws URISyntaxException, IOException, NoSuchAlgorithmException {
		String inputName = "http://developer.android.com/reference/android/app/Activity.html";
		FileCache.NameFactory factory = new MD5CacheFactory();
		String result = factory.getLocalName(inputName);
		assertEquals("Wrong MD5 result", MD5_NAME, result);
		// test Java hash naming
		factory = new JavaHashFactory();
		result = factory.getLocalName(inputName);
		assertEquals("Wrong Java hash result", JAVA_HASH_NAME, result);
	}

    @Test
    public void testTTLMap() throws InterruptedException {
        TTLMap<String> map = new TTLMap<String>();
        map.addValidThrough(MD5_NAME, 0);
        assertFalse("Object should not be in TTLMap", map.contains(MD5_NAME));
        map.addValidThrough(MD5_NAME, System.currentTimeMillis() + 1000);
        assertTrue("Object should be in TTLMap", map.contains(MD5_NAME));
        Thread.sleep(1000);
        assertFalse("Object should not be in TTLMap", map.contains(MD5_NAME));

        map.addWithTTL(MD5_NAME, 0);
        assertFalse("Object should not be in TTLMap", map.contains(MD5_NAME));
        map.addWithTTL(MD5_NAME, 1000);
        assertTrue("Object should be in TTLMap", map.contains(MD5_NAME));
        Thread.sleep(1000);
        assertFalse("Object should not be in TTLMap", map.contains(MD5_NAME));
    }
}
