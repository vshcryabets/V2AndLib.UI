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
        String current = AudioStreamsTests.getCurrentDir();
        File file = new File(current + "/sample/large.jpg");

        assertTrue("Can't read large.jpg file", file.exists());
        JPEGHelper helper = new JPEGHelper();
        JPEGOptions options = helper.getImageOptions(file);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 3508, options.mWidth);
        assertEquals("Wrong height", 4960, options.mHeight);
    }

    @Test
    public void testCrop() throws IOException, NoSuchAlgorithmException, InterruptedException {
        String current = AudioStreamsTests.getCurrentDir();
        File file = new File(current + "/sample/test_pal_large.jpeg");

        assertTrue("Can't read test_pal_large.jpeg file", file.exists());
        JPEGHelper helper = new JPEGHelper();
        JPEGOptions options = helper.getImageOptions(file);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 4096, options.mWidth);
        assertEquals("Wrong height", 4096, options.mHeight);

        File outfile = new File(current + "/out.jpeg");
        int[] cropArea = new int[]{0, 0, 10, 10};
        helper.crop(file, cropArea, outfile);

        options = helper.getImageOptions(outfile);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 10, options.mWidth);
        assertEquals("Wrong height", 10, options.mHeight);

        cropArea = new int[]{4076, 4076, 4096, 4096};
        helper.crop(file, cropArea, outfile);

        options = helper.getImageOptions(outfile);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 20, options.mWidth);
        assertEquals("Wrong height", 20, options.mHeight);

        outfile.delete();
    }

    @Test
    public void testRotate() throws IOException, NoSuchAlgorithmException, InterruptedException {
        String current = AudioStreamsTests.getCurrentDir();
        File file = new File(current + "/sample/test_pal_large.jpeg");

        assertTrue("Can't read test_pal_large.jpeg file", file.exists());
        JPEGHelper helper = new JPEGHelper();
        JPEGOptions options = helper.getImageOptions(file);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 4096, options.mWidth);
        assertEquals("Wrong height", 4096, options.mHeight);

        File outfile = new File(current + "/out.jpeg");
        helper.rotate(file, JPEGHelper.Rotate.CW_90, outfile);
        outfile.delete();
    }
}
