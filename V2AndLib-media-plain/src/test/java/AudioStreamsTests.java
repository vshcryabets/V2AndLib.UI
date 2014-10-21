import com.v2soft.AndLib.medianative.MP3DecoderStream;
import com.v2soft.AndLib.medianative.MP3EncoderStream;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class AudioStreamsTests {

    @Test
    public void testMP3DecoderStream() throws IOException, NoSuchAlgorithmException, InterruptedException {
        byte[] block = new byte[8192];
        int summaryRead = 0;

        String current = new java.io.File(".").getCanonicalPath();
        if (!current.endsWith("V2AndLib-media-plain")) {
            current = current + "/V2AndLib-media-plain";
        }
        FileInputStream input = new FileInputStream(current + "/sample/sine440s2.mp3");
        CounterStream counterOutStream = new CounterStream(new NullOutputStream());
        MP3DecoderStream decoder = new MP3DecoderStream(counterOutStream);
        assertFalse(decoder.isInitialized());
        int read = input.read(block);
        summaryRead += read;
        decoder.write(block, 0, read);

        assertTrue(decoder.isInitialized());
        assertEquals(1, decoder.getChannelsCount());
        assertEquals(48000, decoder.getSampleRate());
        while (( read = input.read(block) ) > 0) {
            summaryRead += read;
            decoder.write(block, 0, read);
        }
        assertEquals("Wrong read size", 238080, counterOutStream.getPassedBytes());
        decoder.close();
    }

    @Test
    public void testMP3DecoderSpeed() throws IOException, NoSuchAlgorithmException, InterruptedException {
        byte[] block = new byte[8192];

        String current = new java.io.File(".").getCanonicalPath();
        if (!current.endsWith("V2AndLib-media-plain")) {
            current = current + "/V2AndLib-media-plain";
        }
        RandomAccessFile input = new RandomAccessFile(current + "/sample/audiosample.mp3", "r");
        final FileOutputStream out = new FileOutputStream(current + "/temp.raw");

        MP3DecoderStream decoder = new MP3DecoderStream(new NullOutputStream());
        assertFalse(decoder.isInitialized());
        int read = input.read(block);
        decoder.write(block, 0, read);

        assertTrue(decoder.isInitialized());
        assertEquals(1, decoder.getChannelsCount());
        assertEquals(48000, decoder.getSampleRate());
        long startTime = System.currentTimeMillis();
        for ( int i = 0; i < 100; i++ ) {
            input.seek(128);
            while (( read = input.read(block) ) > 0) {
                decoder.write(block, 0, read);
            }
        }
        out.close();
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        System.out.println("Encoder speed "+diff+" milliseonds per 100 times. Buffers count="+decoder
                .getHandledBuffersCount());
    }
    @Test
    public void testMP3EncoderStream() throws IOException, NoSuchAlgorithmException, InterruptedException {
        byte[] block = new byte[8192];
        String current = new java.io.File(".").getCanonicalPath();
        if (!current.endsWith("V2AndLib-media-plain")) {
            current = current + "/V2AndLib-media-plain";
        }
        File outputFile = new File(current + "/temp.mp3");
        FileInputStream input = new FileInputStream(current + "/sample/audiosample.mp3");
        FileOutputStream out = new FileOutputStream(outputFile);
        CounterStream counterOutput = new CounterStream(out);
        final MP3EncoderStream encoder = new MP3EncoderStream(counterOutput);
        MP3DecoderStream decoder = new MP3DecoderStream(encoder);
        decoder.setListener(new MP3DecoderStream.DecoderStateListener() {
            @Override
            public void onInitialized(MP3DecoderStream decoder) {
                encoder.setEncodingParams( decoder.getChannelsCount(), decoder.getSampleRate(),
                    decoder.getSampleRate(), MP3EncoderStream.EncodingMode.mono);
            }
        });

        int read;
        while (( read = input.read(block) ) > 0) {
            decoder.write(block, 0, read);
        }
        encoder.close();
        decoder.close();
        assertEquals("Wrong read size", 238080, counterOutput.getPassedBytes());
    }

    private class CounterStream extends OutputStream {
        protected OutputStream mOut;
        protected long summaryPassedBytes;

        public CounterStream(OutputStream out) {
            this.mOut = out;
            summaryPassedBytes = 0;
        }

        @Override
        public void write ( int b)throws IOException {
            mOut.write(b);
            summaryPassedBytes++;
        }

        @Override
        public void write ( byte b[], int off, int len)throws IOException {
            summaryPassedBytes += len;
            mOut.write(b, off, len);
        }

        public long getPassedBytes() {
            return summaryPassedBytes;
        }
    }

    protected class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
        }

        @Override
        public void write(byte[] b, int offset, int count) throws IOException {
        }
    }
}
