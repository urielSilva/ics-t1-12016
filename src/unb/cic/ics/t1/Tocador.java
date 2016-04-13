package unb.cic.ics.t1;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

public class Tocador {

	private Sequencer sequenciador;
	private Sequence sequencia;
	private Receiver receptor = null;

	public Tocador(File arquivoLeitura) {
		configurarSequencia(arquivoLeitura);
	}

	private void configurarSequencia() {
		try {
			sequenciador = MidiSystem.getSequencer();
			sequenciador.setSequence(sequencia);
			sequenciador.open();
			receptor = sequenciador.getTransmitters().iterator().next().getReceiver();
			sequenciador.getTransmitter().setReceiver(receptor);
		} catch (InvalidMidiDataException e) {
			System.out.println(e + " : Erro nos dados midi.");
		} catch (MidiUnavailableException e) {
			System.out.println(e + " : Dispositivo midi não disponível.");
		}
	}
	private void configurarSequencia(File arquivoLeitura) {
			try {
				sequencia = MidiSystem.getSequence(arquivoLeitura);
			} catch (InvalidMidiDataException | IOException e) {
				e.printStackTrace();
			}
			configurarSequencia();
	}
	public void trocarArquivo(File novoArquivo) {
		configurarSequencia(novoArquivo);
	}
	public void executar() {

		exibirDados();
		retardo(500);
		if(!sequenciador.isOpen()) {
			configurarSequencia();
		}
		sequenciador.start(); // --aqui começa a tocar.
		// -----------------------------------------------
		System.out.println("");
		System.out.println("* * * \n");

		retardo(1000);

	}

	public void retardo(int miliseg) {
		try {
			Thread.sleep(miliseg);
		} catch (InterruptedException e) {
		}
	}

	private void exibirDados() {
		long duracao = sequencia.getMicrosecondLength() / 1000000;
		int resolucao = sequencia.getResolution();
		long totaltiques = sequencia.getTickLength();

		float durtique = (float) duracao / totaltiques;
		float durseminima = durtique * resolucao;
		float bpm = 60 / durseminima;
		int totalseminimas = (int) (duracao / durseminima);

		System.out.println("");
		System.out.println("------------------------------------------");
		System.out.println("resolução            = " + resolucao + " tiques   (número de divisões da semínima)");
		System.out.println("duração              = " + duracao + " s");
		System.out.println("número de tiques     = " + totaltiques + " ");
		System.out.println("duração do tique     = " + durtique + " s");
		System.out.println("duração da semínima  = " + durseminima + " s");
		System.out.println("total de seminimas   = " + totalseminimas);
		System.out.println("andamento            = " + Math.round(bpm) + " bpm");
		System.out.println("---");

		System.out.println("");
	}

	public Sequencer getSequenciador() {
		return sequenciador;
	}

	public void setSequenciador(Sequencer sequenciador) {
		this.sequenciador = sequenciador;
	}

	public Sequence getSequencia() {
		return sequencia;
	}

	public void setSequencia(Sequence sequencia) {
		this.sequencia = sequencia;
	}

	public void ajustarPosicaoMicroSegundo(long inicio) {
		sequenciador.setMicrosecondPosition(inicio);

	}

	public boolean isRunning() {
		return sequenciador.isRunning();
	}

	public void parar() {
		sequenciador.stop();
		sequenciador.close();
	}

	public void pausar() {
		sequenciador.stop();
	}

	public void enviarMensagem(ShortMessage mensagem, int i) {
		receptor.send(mensagem, -1);
	}
}
