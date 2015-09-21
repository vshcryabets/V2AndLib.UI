import com.v2soft.AndLib.medianative.JPEGHelper;
import com.v2soft.AndLib.medianative.JPEGHelperException;
import com.v2soft.AndLib.medianative.JPEGOptions;
import com.v2soft.AndLib.medianative.PNGHelper;
import com.v2soft.AndLib.medianative.Rect;

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
public class PNGHelperTests {

    @Test
    public void testGetOptions() throws IOException, NoSuchAlgorithmException, InterruptedException, JPEGHelperException {
        String current = AudioStreamsTests.getCurrentDir();
        File file = new File(current + "/sample/large.jpg");

        assertTrue("Can't read large.jpg file", file.exists());
        PNGHelper helper = new PNGHelper();
        JPEGOptions options = helper.getImageOptions(file);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 3508, options.mWidth);
        assertEquals("Wrong height", 4960, options.mHeight);
    }

    @Test
    public void testRegionLoader() throws IOException, NoSuchAlgorithmException, InterruptedException,
            JPEGHelperException {
        String current = AudioStreamsTests.getCurrentDir();
        File file = new File(current + "/sample/test_pal_large.jpeg");
        File outfile = new File(current + "/out.jpeg");
        assertTrue("Can't read test_pal_large.jpeg file", file.exists());
        PNGHelper helper = new PNGHelper();

        // red
        Rect rect = new Rect(2046, 2036, 2056, 2046);
        byte[] data = helper.load(file, rect);
        helper.save(outfile, data, 10, 10, 100);
        checkColor(file, 0xFFFF0000, rect);

        // black
        rect = new Rect(2036, 2036, 2046, 2046);
        data = helper.load(file, rect);
        helper.save(outfile, data, 10, 10, 100);
        checkColor(file, 0xFF000000, rect);

        // blue
        checkColor(file, 0xFF0000FF, new Rect(2046, 2046, 2056, 2056));


        // green
        checkColor(file, 0xFF00FF00, new Rect(2036, 2046, 2046, 2056));
    }

    @Test
    public void testCrop() throws IOException, NoSuchAlgorithmException, InterruptedException, JPEGHelperException {
        String current = AudioStreamsTests.getCurrentDir();
        File file = new File(current + "/sample/test_pal_large.jpeg");
        assertTrue("Can't read test_pal_large.jpeg file", file.exists());

        final Rect negativeCropArea = new Rect(-100, -100, 0, 0);
        final Rect incorrectCropArea = new Rect(100, 100, 0, 0);
        final Rect overboundCropArea = new Rect(100000, 100000, 100010, 100010);
        final Rect cropAreaTopLeft =new Rect(0, 0, 10, 10);
        final Rect cropAreaBottomRight =new Rect(4076, 4076, 4096, 4096);
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

        // black
        helper.crop(file, cropAreaTopLeft, outfile);
        options = helper.getImageOptions(outfile);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 10, options.mWidth);
        assertEquals("Wrong height", 10, options.mHeight);
        checkColor(outfile, 0xFF000000, null);

        // blue
        helper.crop(file, cropAreaBottomRight, outfile);
        options = helper.getImageOptions(outfile);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 20, options.mWidth);
        assertEquals("Wrong height", 20, options.mHeight);
        checkColor(outfile, 0xFF0000FF, null);

        // red
        helper.crop(file, new Rect(4076, 0, 4096, 12), outfile);
        options = helper.getImageOptions(outfile);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 20, options.mWidth);
        assertEquals("Wrong height", 12, options.mHeight);
        checkColor(outfile, 0xFFFF0000, null);

        // green
        helper.crop(file, new Rect(0, 4076, 15, 4096), outfile);
        options = helper.getImageOptions(outfile);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 15, options.mWidth);
        assertEquals("Wrong height", 20, options.mHeight);
        checkColor(outfile, 0xFF00FF00, null);

        outfile.delete();
    }

    private void checkColor(File infile, int color, Rect region) throws JPEGHelperException {
        JPEGHelper helper = new JPEGHelper();
        JPEGOptions options = helper.getImageOptions(infile);
        byte [] pixel = helper.load(infile, region);
        int count = pixel.length;
        assertTrue("Wrong padding "+count, count % 3 == 0);
        count = count / 3;
        int ir = color >> 16 & 0xFF;
        int ig = color >> 8 & 0xFF;
        int ib = color >> 0 & 0xFF;
        for ( int i = 0; i < count; i++ ) {
            int rr = pixel[i * 3 + 0] & 0xFF;
            int rg = pixel[i * 3 + 1] & 0xFF;
            int rb = pixel[i * 3 + 2] & 0xFF;
            // Thats really strange but after libjpeg some pixels have big deviation
            assertTrue("Wrong r " + ir + "="+rr + " " + i, Math.abs(rr-ir)<0x19);
            assertTrue("Wrong g " + ig + "=" + rg + " " + i, Math.abs(rg - ig) < 0x19);
            assertTrue("Wrong b " + ib + "=" + rb + " " + (i*3+2), Math.abs(rb - ib) < 0x19);
        }
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
