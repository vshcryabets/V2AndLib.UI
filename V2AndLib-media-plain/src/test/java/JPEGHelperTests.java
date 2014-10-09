import com.v2soft.AndLib.medianative.JPEGHelper;
import com.v2soft.AndLib.medianative.JPEGOptions;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class JPEGHelperTests {

    @Test
    public void testGetOptions() throws IOException, NoSuchAlgorithmException, InterruptedException {
        String current = new java.io.File(".").getCanonicalPath();
        if (!current.endsWith("V2AndLib-media-plain")) {
            current = current + "/V2AndLib-media-plain";
        }
        File file = new File(current + "/sample/large.jpg");

        assertTrue("Can't read large.jpg file", file.exists());
        JPEGHelper helper = new JPEGHelper();
        JPEGOptions options = helper.getImageOptions(file);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 3508, options.mWidth);
        assertEquals("Wrong height", 4960, options.mHeight);
    }
}
