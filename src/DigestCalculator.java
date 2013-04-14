import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DigestCalculator {
	
	public static final String ENCODING = "UTF-8";
	
	public static MessageDigest getMD5() throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("MD5");
	}
	public static MessageDigest getSHA1() throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("SHA1");
	}
	
	public static String encodeHexString(byte[] str) {
		StringBuffer buf = new StringBuffer();
	    for(int i = 0; i < str.length; i++) {
	       String hex = Integer.toHexString(0x0100 + (str[i] & 0x00FF)).substring(1);
	       buf.append((hex.length() < 2 ? "0" : "") + hex);
	    }
	    return buf.toString();
	}
	
	public static void main(String[] args) throws Exception {
		MessageDigest digest;
		String digestType;
		
		// verify args
		if (args.length < 3) {
			System.err.println("Usage: java DigestCalculator digestType filePath1...filePathN digestListFilePath");
			System.exit(1);
		}
		
		// get digestType
		digestType = args[0];
		
		switch(digestType) {
			case "MD5": 
				digest = getMD5();
				break;
			case "SHA1": 
				digest = getSHA1();
				break;
			default:
				System.err.println("digestType can only be MD5 or SHA1");
				System.exit(1);
		}
		
	}

}
