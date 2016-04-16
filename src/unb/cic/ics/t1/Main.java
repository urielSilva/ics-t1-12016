package unb.cic.ics.t1;

import javax.sound.midi.InvalidMidiDataException;

public class Main {
	public static void main(String[] args) throws InvalidMidiDataException {
		LeitorArquivo leitor = new LeitorArquivo("mvioloncelo1.mid");
		Tocador tocador = new Tocador(leitor.getArquivo());
		tocador.executar();
	}
}
