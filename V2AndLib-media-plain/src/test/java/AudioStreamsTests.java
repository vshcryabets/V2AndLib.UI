import com.v2soft.AndLib.medianative.MP3DecoderStream;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class AudioStreamsTests {

    @Test
    public void testMP3DecoderStream() throws IOException, NoSuchAlgorithmException, InterruptedException {
        byte[] block = new byte[8192];
        String current = new java.io.File(".").getCanonicalPath();
        System.out.println("Current dir:" + current);
        if (!current.endsWith("V2AndLib-media-plain")) {
            current = current + "/V2AndLib-media-plain";
        }
        MP3DecoderStream decoder = new MP3DecoderStream(current + "/sample/audiosample.mp3");
        FileOutputStream out = new FileOutputStream(current + "/temp.raw");
        int read;
        int summaryRead = 0;
        while (( read = decoder.read(block) ) > 0) {
            summaryRead += read;
            out.write(block, 0, read);
        }
        out.close();
        assertEquals("Wrong read size", 22847488, summaryRead);
        decoder.close();
    }
}
