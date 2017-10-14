package team.balam.exof.environment;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TextCrypto {
	private Cipher cipher;
	private Key key;

	public TextCrypto() {
		try {
			byte[] secretKey = Base64.getDecoder().decode("olThOfn8KrcMeYYmgCKPlw==");
			String transformations = "AES";

			this.key = new SecretKeySpec(secretKey, transformations);
			this.cipher = Cipher.getInstance(transformations);
		} catch (Exception e) {
		}
	}

	public void setKey(Key _key) {
		this.key = _key;
	}

	/**
	 * default AES
	 * @param _transformations "algorithm/mode/padding" or "algorithm"
	 */
	public void setTransformations(String _transformations) throws NoSuchAlgorithmException, NoSuchPaddingException{
		this.cipher = Cipher.getInstance(_transformations);
	}

	public byte[] encode(byte[] _data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.ENCRYPT_MODE, this.key);
		return this.cipher.doFinal(_data);
	}

	/**
	 * 입력값을 암호화 후 base64 를 적용한다
	 * @param _data
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String encodeBase64(byte[] _data)  throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] enc = this.encode(_data);
		return Base64.getEncoder().encodeToString(enc);
	}

	public byte[] decode(byte[] _data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.DECRYPT_MODE, this.key);
		return this.cipher.doFinal(_data);
	}

	/**
	 * encodeBase64 의 결과를 다시 복호화 한다.
	 * @param _data
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] decodeBase64(String _data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] dec = Base64.getDecoder().decode(_data);
		return this.decode(dec);
	}
}
