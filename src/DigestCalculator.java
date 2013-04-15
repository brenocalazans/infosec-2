import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

class DigestDetail {
	public String digestType;
	public String digest;

	DigestDetail(String digestType, String digest) {
		this.digestType = digestType;
		this.digest = digest;
	}

	@Override
	public String toString() {
		return digestType + " " + digest;
	}
}

class FileDigestDetail {
	public String filename;
	public DigestDetail[] digestList;

	public FileDigestDetail(String path) throws IOException, NoSuchAlgorithmException {
		this.filename = path.substring(path.lastIndexOf('/')+1);
		this.digestList = new DigestDetail[2];

		File file = new File(path);
		byte[] fileData = new byte[(int) file.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		dis.readFully(fileData);
		dis.close();

		MessageDigest md = DigestCalculator.getMD5();
		md.update(fileData);
		this.digestList[0] = new DigestDetail("MD5", DigestCalculator.encodeHexString(md.digest()));

		md = DigestCalculator.getSHA1();
		md.update(fileData);
		this.digestList[1] = new DigestDetail("SHA1", DigestCalculator.encodeHexString(md.digest()));
	}

	@Override
	public String toString() {
		return filename + " " + Arrays.toString(digestList);
	}
}

class DigestFile {
	// Lista de arquivos descritos neste DigestFile
	public FileDigestDetail[] files;

	// Lê um arquivo de Digests e retorna um objeto instanciado
	public static DigestFile read(String path) {

		return null;
	}

	// Escreve este arquivo
	public void write(String path) {

	}

	// Checks if this FileDigestDetail
	public String checkFile(FileDigestDetail fileDigestDetail) {

		return null;
	}
}

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
		for (int i = 0; i < str.length; i++) {
			String hex = Integer.toHexString(0x0100 + (str[i] & 0x00FF)).substring(1);
			buf.append((hex.length() < 2 ? "0" : "") + hex);
		}
		return buf.toString();
	}

	@SuppressWarnings("null")
	public static void main(String[] args) throws Exception {
		MessageDigest digest;
		String digestType, digestListFile;
		ArrayList<String> files = new ArrayList();

		// verify args
		if (args.length < 3) {
			System.err.println("Usage: java DigestCalculator digestType filePath1...filePathN digestListFilePath");
			System.exit(1);
		}

		// get digestType
		digestType = args[0];

		// get files to be processed
		for (int i = 1; i < args.length - 1; i++) {
			files.add(args[i]);
		}

		System.out.println(new FileDigestDetail("arquivo2.txt").toString());

		// get digistListFile
		digestListFile = args[args.length - 1];

		switch (digestType) {
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


		// read files		
		for (String string : files) {
			System.out.println(string);
		}
	}

}
