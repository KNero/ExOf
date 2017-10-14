package balam.exof.test.environment;

import org.junit.Assert;
import org.junit.Test;
import team.balam.exof.environment.TextCrypto;

public class TextCryptoTest {
	@Test
	public void testbyte() throws Exception {
		String text = "test-test-1";
		TextCrypto crypto = new TextCrypto();
		byte[] enc = crypto.encode(text.getBytes());
		byte[] dec = crypto.decode(enc);

		Assert.assertEquals(text, new String(dec));
	}

	@Test
	public void testbase64() throws Exception {
		String text = "test-test-1";
		TextCrypto crypto = new TextCrypto();
		String base64Enc = crypto.encodeBase64(text.getBytes());
		byte[] bsae64Dec = crypto.decodeBase64(base64Enc);

		Assert.assertEquals(text, new String(bsae64Dec));
	}
}
