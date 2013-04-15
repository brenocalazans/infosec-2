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
	public String currentStatus = null;

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
	public String file;
	public ArrayList<String> digestLines = new ArrayList<String>();

	public DigestListFile(String file) throws IOException {
		this.file = file;		
	}
	
	public void read() throws IOException {
		digestLines.clear();
		BufferedReader br = new BufferedReader(new FileReader(this.file));
	    try {	        
	        String line = br.readLine();	        
	        
	        while (line != null) {
	        	digestLines.add(line);
	            line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
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
	
	public static final String NOT_OK = "NOT OK";
	public static final String OK = "OK";
	public static final String NOT_FOUND= "NOT FOUND";
	public static final String COLISION = "COLISION";

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
		String digestType;
		ArrayList<String> files = new ArrayList<String>();
		ArrayList<DigestFile> calculatedDigest = new ArrayList<DigestFile>();

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

		// get digestListFile		
		DigestListFile digestListFile = new DigestListFile(args[args.length - 1]);				
				//
		for (int i = 0; i < files.size(); i++) {			
			calculatedDigest.add(new DigestFile(files.get(i), digestType));
		}
		
		// Verifica se os digests calculados nessa rodada colidem com outro digest calculado 
		for (int i = 0; i < calculatedDigest.size(); i++) {
			DigestFile currentDigestFile = calculatedDigest.get(i);
			for (int j = 0; j < calculatedDigest.size(); j++) {
				if (i==j) 
					continue;
				String digest1 = currentDigestFile.digestList.get(0).toString();
				String digest2 = calculatedDigest.get(j).digestList.get(0).toString();
				
				if (digest1.equals(digest2)) {
					calculatedDigest.get(j).currentStatus = DigestCalculator.COLISION;					
				}
			}
		}
		
		// verifica os digests calculados com os digests do digestListFile 
		for (int i = 0; i < calculatedDigest.size(); i++) {
			DigestFile df = calculatedDigest.get(i);
			
			// Pula, caso o digest calculado ja tenha entrado em caso de colisão 
			// com os digests calculados pela linha de comando
			if (df.currentStatus != null && df.currentStatus.equals(DigestCalculator.COLISION))
				continue;
			
			// le o arquivo de lista de digests
			digestListFile.read();
			// para cada linha do arquivo da lista de digests
			for (int j = 0; j < digestListFile.digestLines.size(); j++) {
				String line = digestListFile.digestLines.get(j);
				
				// para cada digest do arquivo
				for (int k = 0; k < df.digestList.size(); k++) {					
					// verifica se a linha possui o digest criado
					if (line.contains(df.digestList.get(k).toString())) {
						
						// verifica se a linha corrente possui o nome do arquivo
						if (line.contains(df.filename)) {
							// STATUS: OK
							df.currentStatus = DigestCalculator.OK;
						}
						// caso outro arquivo possua o mesmo digest, entao colisão
						else {
							// STATUS: COLISION
							df.currentStatus = DigestCalculator.COLISION;
						}							
					}
					else {
						// caso a linha contenha o nome do arquivo
						// porem nao contenha o digest
						if (line.contains(df.filename)) {
							// STATUS: NOT OK
							df.currentStatus = DigestCalculator.NOT_OK;
						}
					}
				}				
			}
			if (df.currentStatus == null) {
				df.currentStatus = DigestCalculator.NOT_FOUND;
			}
			System.out.println(df.toString() + df.currentStatus);
		}
	}

}
