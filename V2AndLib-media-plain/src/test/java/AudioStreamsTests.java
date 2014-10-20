import com.v2soft.AndLib.medianative.MP3DecoderStream;
import com.v2soft.AndLib.medianative.MP3EncoderStream;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
        int summaryRead = 0;
        final int summaryWrite[] = new int[]{0};

        String current = new java.io.File(".").getCanonicalPath();
        if (!current.endsWith("V2AndLib-media-plain")) {
            current = current + "/V2AndLib-media-plain";
        }
        FileInputStream input = new FileInputStream(current + "/sample/audiosample.mp3");
        final FileOutputStream out = new FileOutputStream(current + "/temp.raw");

        MP3DecoderStream decoder = new MP3DecoderStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                out.write(b);
            }

            @Override
            public void write(byte b[], int off, int len) throws IOException {
                summaryWrite[0] += len;
                out.write(b, off, len);
            }
        });
        int read = input.read(block);
        summaryRead += read;
        decoder.write(block, 0, read);

        assertEquals(1, decoder.getChannelsCount());
        assertEquals(48000, decoder.getSampleRate());
        while (( read = input.read(block) ) > 0) {
            summaryRead += read;
            decoder.write(block, 0, read);
        }
        out.close();
        assertEquals("Wrong read size", 22853174, summaryWrite[0]);
        decoder.close();
    }

    @Test
    public void testMP3EncoderStream() throws IOException, NoSuchAlgorithmException, InterruptedException {
        byte[] block = new byte[8192];
        String current = new java.io.File(".").getCanonicalPath();
        if (!current.endsWith("V2AndLib-media-plain")) {
            current = current + "/V2AndLib-media-plain";
        }
        FileInputStream input = new FileInputStream(current + "/sample/audiosample.mp3");
        FileOutputStream out = new FileOutputStream(current + "/temp.mp3");

//        MP3DecoderStream decoder = new MP3DecoderStream(current + "/sample/audiosample.mp3");
//        MP3EncoderStream encoder = new MP3EncoderStream(out, decoder.getChannelsCount(), decoder.getSampleRate(),
//                decoder.getSampleRate(), MP3EncoderStream.EncodingMode.mono);
//        int read;
//        int summaryRead = 0;
//        while (( read = decoder.read(block) ) > 0) {
//            summaryRead += read;
//            encoder.write(block, 0, read);
//        }
//        encoder.close();
//        assertEquals("Wrong read size", 22847488, summaryRead);
//        decoder.close();
    }
}
