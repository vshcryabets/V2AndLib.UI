import com.v2soft.AndLib.medianative.JPEGHelper;
import com.v2soft.AndLib.medianative.JPEGHelperException;
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
    public void testGetOptions() throws IOException, NoSuchAlgorithmException, InterruptedException, JPEGHelperException {
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
    public void testCrop() throws IOException, NoSuchAlgorithmException, InterruptedException, JPEGHelperException {
        String current = AudioStreamsTests.getCurrentDir();
        File file = new File(current + "/sample/test_pal_large.jpeg");
        assertTrue("Can't read test_pal_large.jpeg file", file.exists());

        final int[] wrongCropAreaArray = new int[]{1, 2, 3};
        final int[] negativeCropArea = new int[]{-100, -100, 0, 0};
        final int[] incorrectCropArea = new int[]{100, 100, 0, 0};
        final int[] overboundCropArea = new int[]{100000, 100000, 100010, 100010};
        final int[] cropAreaTopLeft = new int[]{0, 0, 10, 10};
        final int[] cropAreaBottomRight = new int[]{4076, 4076, 4096, 4096};
        File outfile = new File(current + "/out.jpeg");
        File fakeinput = new File(current + "/nosuchfile.jpeg");

        JPEGHelper helper = new JPEGHelper();

        // check no file
        try {
            helper.crop(fakeinput, cropAreaTopLeft, outfile);
            assertTrue("There should be exception No such file", false);
        } catch (JPEGHelperException e) {
            assertEquals(JPEGHelperException.ERR_NO_FILE, e.getCode());
        }

        // check wrong number of int in the crop area
        try {
            helper.crop(file, wrongCropAreaArray, outfile);
            assertTrue("3 parameters in crop area", false);
        } catch (JPEGHelperException e) {
            assertEquals(JPEGHelperException.ERR_INCORRECT_GEOMETRY_PARAMETER, e.getCode());
        }

        // check negative values in the crop area
        try {
            helper.crop(file, negativeCropArea, outfile);
            assertTrue("Negative value in crop area", false);
        } catch (JPEGHelperException e) {
            assertEquals(JPEGHelperException.ERR_INCORRECT_GEOMETRY_PARAMETER, e.getCode());
        }

        // check wrong values order in the crop area
        try {
            helper.crop(file, incorrectCropArea, outfile);
            assertTrue("Negative value in crop area", false);
        } catch (JPEGHelperException e) {
            assertEquals(JPEGHelperException.ERR_INCORRECT_GEOMETRY_PARAMETER, e.getCode());
        }

        // check big values in the crop area
        try {
            helper.crop(file, overboundCropArea, outfile);
            assertTrue("Negative value in crop area", false);
        } catch (JPEGHelperException e) {
            assertEquals(JPEGHelperException.ERR_INCORRECT_GEOMETRY_PARAMETER, e.getCode());
        }

        JPEGOptions options = helper.getImageOptions(file);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 4096, options.mWidth);
        assertEquals("Wrong height", 4096, options.mHeight);

        helper.crop(file, cropAreaTopLeft, outfile);

        options = helper.getImageOptions(outfile);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 10, options.mWidth);
        assertEquals("Wrong height", 10, options.mHeight);

        helper.crop(file, cropAreaBottomRight, outfile);

        options = helper.getImageOptions(outfile);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 20, options.mWidth);
        assertEquals("Wrong height", 20, options.mHeight);

        outfile.delete();
    }

    @Test
    public void testRotate() throws IOException, NoSuchAlgorithmException, InterruptedException, JPEGHelperException {
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
