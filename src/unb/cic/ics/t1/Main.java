package unb.cic.ics.t1;

public class Main {
	public static void main(String[] args) {
		LeitorArquivo leitor = new LeitorArquivo("mvioloncelo1.mid");
		Tocador tocador = new Tocador(leitor.getArquivo());
		tocador.executar();
	}
}
