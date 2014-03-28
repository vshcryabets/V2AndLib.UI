import com.v2soft.AndLib.media.JPEGHelper;
import com.v2soft.AndLib.media.JPEGOptions;

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
//        URI uri = URI.create(BitmapOperationsTests.ASSET_LARGE_FILE_PATH);
//        FileCache cache = new AndroidFileCache.Builder(getContext()).build();
//        cache.clear();
//        File file = cache.getFileByURI(uri);
//        StreamHelper stream = AndroidStreamHelper.getStream(getContext(), uri);
//        FileOutputStream output = new FileOutputStream(file);
//        stream.copyToOutputStream(output);
//        output.close();
        java.net.URL url = JPEGHelperTests.class.getResource("/large.jpg");
        File current = new File(".");
        System.out.println("QQ="+current.getAbsolutePath());
        File file = new File("resources/large.jpg");
        assertTrue("Can't read large.jpg file", file.exists());
        JPEGHelper helper = new JPEGHelper();
        JPEGOptions options = helper.getImageOptions(file);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 3508, options.mWidth);
        assertEquals("Wrong height", 4960, options.mHeight);
	}



}
