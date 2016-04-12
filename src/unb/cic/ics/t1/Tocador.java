package unb.cic.ics.t1;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class Tocador {

	Sequencer sequenciador;
	Sequence sequencia;

	public Tocador(File arquivoLeitura) {
		inicializar(arquivoLeitura);
	}

	private void inicializar(File arquivoLeitura) {
		try {
			sequencia = MidiSystem.getSequence(arquivoLeitura);
			sequenciador = MidiSystem.getSequencer();
			sequenciador.setSequence(sequencia);
			sequenciador.open();
		} catch (InvalidMidiDataException e) {
			System.out.println(e + " : Erro nos dados midi.");
		} catch (IOException e) {
			System.out.println(e + " : O arquivo midi não foi encontrado.");
			System.out.println("Sintaxe: " + "java TocaMidi arquivo.mid");
		} catch (MidiUnavailableException e) {
			System.out.println(e + " : Dispositivo midi não disponível.");
		}
	}

	public void executar() {

			exibirDados();
			retardo(500);
			sequenciador.start(); // --aqui começa a tocar.
			// -----------------------------------------------

			verificarExecucao();

			System.out.println("");
			System.out.println("* * * \n");

			retardo(1000);
			sequenciador.stop();
			sequenciador.close();
		
	}

	private void verificarExecucao() {
		// -- O laço abaixo verifica (a cada 1 segundo) se a execução já está
		// -- completada. Quando estiver, então o sequenciador será 'fechado';

		int i = 0;
		System.out.println("Instante em segundos: ");

		long posicao;
		int seg;
		while (sequenciador.isRunning()) {
			retardo(1000);
			// ----exibir o instante real em segundos:---------
			posicao = sequenciador.getMicrosecondPosition();
			seg = Math.round(posicao * 0.000001f);
			System.out.print(seg + " ");
			i++;
			if (i == 20) {
				System.out.println("");
				i = 0;
			}
		}
	}

	static void retardo(int miliseg) {
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
}
