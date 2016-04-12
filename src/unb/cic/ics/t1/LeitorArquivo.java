package unb.cic.ics.t1;

import java.io.File;

public class LeitorArquivo {
	
	File arquivo;
	
	public LeitorArquivo(String nomeArquivo) {
		arquivo = new File(nomeArquivo);
	}

	public File getArquivo() {
		return arquivo;
	}
	
}
