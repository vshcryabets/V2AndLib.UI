import com.v2soft.AndLib.medianative.MP3DecoderStream;

import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class AudioStreamsTests  {

	@Test
	public void testMP3DecoderStream() throws IOException, NoSuchAlgorithmException, InterruptedException {
        byte[] block = new byte[8192];
        MP3DecoderStream decoder = new MP3DecoderStream("/Users/user/Documents/git/V2AndLib.UI/V2AndLib-media-plain/sample/audiosample.mp3");
        decoder.read(block);
        decoder.close();
	}

}
