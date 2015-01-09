import com.v2soft.AndLib.medianative.MP3DecoderStream;
import com.v2soft.AndLib.medianative.MP3EncoderStream;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

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

        String current = getCurrentDir();
        FileInputStream input = new FileInputStream(current + "/sample/sine440s2.mp3");
        FileOutputStream output = new FileOutputStream(current + "/sine440s2.data");
        CounterStream counterOutStream = new CounterStream(output);
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
//        int expectedLength = decoder.getExpectedLength();
//        assertTrue("", expectedLength > 1000000);

        decoder.close();
        assertEquals("Wrong decoded samples count", 48000 * 2 * 2, counterOutStream.getPassedBytes());
    }

    @Test
    public void testMP3DecoderSpeed() throws IOException, NoSuchAlgorithmException, InterruptedException {
        byte[] block = new byte[8192];

        String current = getCurrentDir();
        RandomAccessFile input = new RandomAccessFile(current + "/sample/sine440s2.mp3", "r");
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
        File outputFile = new File(getCurrentDir() + "/testMP3EncoderStream.mp3");
        File inputFile = new File(getCurrentDir() + "/sample/sine440s1.raw");
        FileInputStream input = new FileInputStream(inputFile);
        FileOutputStream out = new FileOutputStream(outputFile);
        CounterStream counterOutput = new CounterStream(out);
        final MP3EncoderStream encoder = new MP3EncoderStream(counterOutput);
        CounterStream counterDecoded = new CounterStream(encoder);
        encoder.setEncodingParams( 1, 48000, 48000, MP3EncoderStream.EncodingMode.mono);

        int read;
        while (( read = input.read(block) ) > 0) {
            counterDecoded.write(block, 0, read);
        }
        long decodedSamplesCount = counterDecoded.getPassedBytes()/2;
        assertTrue("Got samples count " + decodedSamplesCount, decodedSamplesCount == 48000 );
//        int expectedLength = decoder.getExpectedLength();
//        assertTrue("Got length "+expectedLength, expectedLength == 48000 );
        encoder.close();
        // TODO check output file duration
//        assertTrue("Wrong output size expected "+inputFile.length()+" got " + outputFile.length(),
//                Math.abs(outputFile.length() - inputFile.length()) < 1024);
    }

    public static String getCurrentDir() throws IOException {
        String current = new java.io.File(".").getCanonicalPath();
        if (!current.endsWith("V2AndLib-media-plain")) {
            current = current + "/V2AndLib-media-plain";
        }
        return current;
    }

    @Test
    public void testEncodeDecodeCheck() throws IOException, NoSuchAlgorithmException, InterruptedException {
        // generate sine
        boolean bigEndianMode = false;
        boolean stereo = false;
        Random random = new Random();
        int sourceFrequency = 440; //random.nextInt(3000) + 100;
        int sourceDuration = random.nextInt(1000)+1000;
        int sourceSampleRate = 44100;

        byte source[] = sineGenerator(bigEndianMode, stereo, sourceSampleRate, sourceDuration, sourceFrequency, 0.8f);
        int frequency = checkFrequency(bigEndianMode, source, stereo, sourceSampleRate);
        assertTrue("Wrong frequency 1 "+sourceFrequency+" expected but was "+ frequency, Math.abs(frequency - sourceFrequency) < 50 );

        ByteArrayOutputStream decodedBuffer = new ByteArrayOutputStream(source.length*2);
        MP3DecoderStream decoder = new MP3DecoderStream(decodedBuffer);
        MP3EncoderStream encoder = new MP3EncoderStream(decoder, (stereo?2:1), sourceSampleRate, sourceSampleRate,
                (stereo ? MP3EncoderStream.EncodingMode.stereo : MP3EncoderStream.EncodingMode.mono) );
        encoder.write(source);

        File outputFile = new File(getCurrentDir() + "/source.dat");
        FileOutputStream out = new FileOutputStream(outputFile);
        out.write(source);
        out.flush();
        out.close();

        outputFile = new File(getCurrentDir() + "/decoded.dat");
        out = new FileOutputStream(outputFile);
        decodedBuffer.writeTo(out);
        out.close();

        int resultFreqency = checkFrequency(bigEndianMode, decodedBuffer.toByteArray(), stereo, sourceSampleRate);
        assertTrue("Wrong frequency of result " + sourceFrequency + " expected but was " + resultFreqency,
                Math.abs(resultFreqency - sourceFrequency) < 50);


        int sourceSamplesCount = source.length / (stereo ? 4 : 2);
        int resultSamplesCount = decodedBuffer.size() / ( stereo ? 4 : 2 );
        assertEquals("Wrong number of samples count", sourceSamplesCount, resultSamplesCount);
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

    private byte[] sineGenerator(boolean bigendian, boolean stereo, int sampleRate, int longInMs, int frequencyInHZ,
                                 float amplitude) {
        int framesCount = sampleRate * longInMs / 1000;
        int bytesPerFrame = ( stereo ? 4 : 2 );
        byte dataArray[] = new byte[framesCount * bytesPerFrame];
        double left = 0.0;
        double right = Math.PI / 2;
        double scale = frequencyInHZ / ( 1 / ( 2 * Math.PI ) );
        for (int i = 0; i < framesCount; i++) {
            double t = (double) i / (double) sampleRate;
            short leftSample = (short) ( Short.MAX_VALUE * Math.sin(t * scale + left) * amplitude );
            short rightSample = (short) ( Short.MAX_VALUE * Math.sin(t * scale + right) * amplitude );
            byte first = (byte) ( leftSample >> 8 );
            byte second = (byte) ( leftSample & 0xFF );
            if ( !bigendian ) {
                first = (byte) ( leftSample & 0xFF );
                second = (byte) ( leftSample >> 8 );
            }
            dataArray[i * bytesPerFrame] = first;
            dataArray[i * bytesPerFrame + 1] = second;
            if (stereo) {
                if (!bigendian) {
                    first = (byte) ( leftSample & 0xFF );
                    second = (byte) ( leftSample >> 8 );
                } else {
                    first = (byte) ( rightSample >> 8 );
                    second = (byte) ( rightSample & 0xFF );
                }
                dataArray[i * bytesPerFrame + 2] = first;
                dataArray[i * bytesPerFrame + 3] = second;
            }
        }
        return dataArray;
    }

    private int checkFrequency(boolean bigendian, byte rawData[], boolean stereo, int sampleRate) {
        int zeroCount = 0;
        int offset = 0;
        int bytesPerFrame = ( stereo ? 4 : 2 );
        int prevValue = 0;
        while (offset < rawData.length) {
            short first = rawData[offset];
            short second = rawData[offset + 1];
            if ( !bigendian ) {
                first = rawData[offset + 1];
                second = rawData[offset];
            }
            short value = (short) ( ( first << 8 ) | ( second & 0xFF ) );
            if (prevValue < 0 && value >= 0) {
                zeroCount++;
            }
            prevValue = value;
            offset += bytesPerFrame;
        }
        int result = zeroCount * ( bytesPerFrame * sampleRate ) / rawData.length;
        return result;
    }
}
