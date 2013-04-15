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

class DigestFile {
	public String filename;
	public ArrayList<DigestDetail> digestList = new ArrayList<DigestDetail>();

	public DigestFile(String path, String digestAlgorithm) throws IOException, NoSuchAlgorithmException {
		this.filename = path;
		
		File file = new File(path);
		byte[] fileData = new byte[(int) file.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		dis.readFully(fileData);
		dis.close();		
		
		MessageDigest digest = MessageDigest.getInstance(digestAlgorithm);		
		digest.update(fileData);
		digestList.add(new DigestDetail(digestAlgorithm, DigestCalculator.encodeHexString(digest.digest())));
	}
	
	@Override
	public String toString() {
		String digests = "";
		for (int i = 0; i < digestList.size(); i++) {
			digests += digestList.get(i).toString() + " ";
		}
		return filename + " " + digests;
	}
}

class DigestListFile {
	// Lista de arquivos descritos neste DigestFile
	public DigestFile[] files;

	// Le um arquivo de Digests e retorna um objeto instanciado
	public static DigestListFile read(String path) {

		return null;
	}

	// Escreve este arquivo
	public void write(String path) {

	}

	// Checks if this FileDigestDetail
	public String checkFile(DigestFile digestFile) {
		
		return null;
	}
}

public class DigestCalculator {

	public static final String ENCODING = "UTF-8";	

	public static String encodeHexString(byte[] str) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			String hex = Integer.toHexString(0x0100 + (str[i] & 0x00FF)).substring(1);
			buf.append((hex.length() < 2 ? "0" : "") + hex);
		}
		return buf.toString();
	}
	
	public static void main(String[] args) throws Exception {	
		MessageDigest digest;
		String digestType, digestListFile;
		ArrayList<String> files = new ArrayList<String>();

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

		// get digistListFile
		digestListFile = args[args.length - 1];

		for (int i = 0; i < files.size(); i++) {
			DigestFile df = new DigestFile(files.get(i), digestType);
			System.out.println(df.toString());			
		}
	}

}
