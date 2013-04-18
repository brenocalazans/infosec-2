import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
	public String status = null;

	// Constroi um DigestFile a partir de uma linha do arquivo de lista de
	// digests
	public DigestFile(String fileLine) {
		String[] tokens = fileLine.split(" ");
		this.filename = tokens[0];
		this.digestList.add(new DigestDetail(tokens[1], tokens[2]));
		if (tokens.length > 3)
			this.digestList.add(new DigestDetail(tokens[3], tokens[4]));
	}

	// Constroi um DigestFile a partir de um arquivo
	public DigestFile(String path, String digestAlgorithm) throws IOException,
			NoSuchAlgorithmException {
		this.filename = path.substring(path.lastIndexOf('/') + 1);

		File file = new File(path);
		byte[] fileData = new byte[(int) file.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		dis.readFully(fileData);
		dis.close();

		MessageDigest digest = MessageDigest.getInstance(digestAlgorithm);
		digest.update(fileData);
		digestList.add(new DigestDetail(digestAlgorithm, DigestCalculator
				.encodeHexString(digest.digest())));
	}
	
	public void checkCollisionWith(DigestFile df) {
		// Para cada digest nesse arquivo
		for (int i = 0; i < this.digestList.size(); i++){
			String digestType = this.digestList.get(i).digestType;
			String digest = this.digestList.get(i).digest;
			String matchingDigest = "";
			// Acha o hash com mesmo digestType na lista de df
			for (int k = 0; k < df.digestList.size(); k++) {
				if (digestType.equals(df.digestList.get(k).digestType))
					matchingDigest = df.digestList.get(k).digest;
			}
			
			// Há colisão
			if (digest.equals(matchingDigest) && !this.filename.equals(df.filename)) {
				this.status = "COLLISION";
				break;
			}
		}
	}

	public void checkStatusForList(ArrayList<DigestFile> files) {
		System.out.println("Checando " + this.toString()  + " com " + files.toString() + "\n");
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
	public ArrayList<DigestFile> files = new ArrayList<DigestFile>();
	public String file;

	public DigestListFile(ArrayList<DigestFile> files){
		this.files = files;
	}
	
	// Constroi a partir de um arquivo
	public DigestListFile(String file) throws IOException {
		this.file = file;
		BufferedReader br = new BufferedReader(new FileReader(this.file));
		try {
			String line = "";
			while ((line = br.readLine()) != null) {
				this.files.add(new DigestFile(line));
			}
		} finally {
			br.close();
		}
	}

	// Escreve este arquivo
	public void write(String path) {

	}

	// Atualiza os status dos seus DigestFiles
	public void checkFiles(ArrayList<DigestFile> calculatedDigest) {
		DigestListFile collisionCheckList = new DigestListFile(calculatedDigest);
		collisionCheckList.files.addAll(this.files);
		
		for (int i = 0; i < calculatedDigest.size(); i++) {
			DigestFile currentDigestFile = collisionCheckList.files.get(i);
			// Checa colisões
			for (int j = 0; j < collisionCheckList.files.size(); j++) {
				currentDigestFile.checkCollisionWith(collisionCheckList.files.get(j));
			}
			
			// Checar status dos calculatedDigest com os da lista
			currentDigestFile.checkStatusForList(this.files);
			
			if (currentDigestFile.status == null) {
				currentDigestFile.status = DigestCalculator.NOT_FOUND;
			}
			System.out.println(currentDigestFile.toString() + currentDigestFile.status + "\n");
		}
	}
}

public class DigestCalculator {

	public static final String ENCODING = "UTF-8";

	public static final String NOT_OK = "NOT OK";
	public static final String OK = "OK";
	public static final String NOT_FOUND = "NOT FOUND";
	public static final String COLISION = "COLISION";

	public static String encodeHexString(byte[] str) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			String hex = Integer.toHexString(0x0100 + (str[i] & 0x00FF))
					.substring(1);
			buf.append((hex.length() < 2 ? "0" : "") + hex);
		}
		return buf.toString();
	}

	public static void main(String[] args) throws Exception {
		MessageDigest digest;
		String digestType;
		ArrayList<String> files = new ArrayList<String>();
		ArrayList<DigestFile> calculatedDigest = new ArrayList<DigestFile>();
		DigestListFile digestListFile;

		// verify args
		if (args.length < 3) {
			System.err
					.println("Usage: java DigestCalculator digestType filePath1...filePathN digestListFilePath");
			System.exit(1);
		}

		// get digestType
		digestType = args[0];

		// get files to be processed
		for (int i = 1; i < args.length - 1; i++) {
			files.add(args[i]);
		}

		// Le o arquivo de lista
		digestListFile = new DigestListFile(args[args.length - 1]);
		
		// Calcula os digests
		for (int i = 0; i < files.size(); i++) {
			calculatedDigest.add(new DigestFile(files.get(i), digestType));
		}
		
		digestListFile.checkFiles(calculatedDigest);
	}

}
