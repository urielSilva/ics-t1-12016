package unb.cic.ics.t1;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.text.DecimalFormat;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
public class GUI extends JFrame implements Runnable {

	private int largura = 490;
	private int altura = 500;

	private int posx = 400;
	private int posy = 140;

	ImageIcon logo = null;

	private String diretorio = System.getProperty("user.dir");

	final JButton botaoABRIR = GUIUtils.constroiBotao("Abrir", 9);
	final JButton botaoTOCAR = GUIUtils.constroiBotao("\u25b6", 9);
	final JButton botaoFAZERPAUSA = GUIUtils.constroiBotao("\u25ae\u25ae", 9);
	final JButton botaoPARAR = GUIUtils.constroiBotao("\u25fc", 9);
	final JButton botaoAJUSTAR = GUIUtils.constroiBotao("+", 9);

	final JButton botaoMOSTRADORcaminho = GUIUtils.constroiBotao(" DIR: " + diretorio, 9);
	final JButton botaoMOSTRADORarquivo = GUIUtils.constroiBotao(" Arquivo: ", 9);
	final JButton botaoMOSTRADORduracao = GUIUtils.constroiBotao(" Dura\u00e7\u00e3o: ", 9);
	final JButton botaoMOSTRADORinstante = GUIUtils.constroiBotao(" ", 9);
	final JButton botaoMOSTRADORvalorvolume = GUIUtils.constroiBotao(" ", 9);
	final JButton botaoMOSTRADORandamento = GUIUtils.constroiBotao("Andamento: ", 9);
	final JButton botaoMOSTRADORtonalidade = GUIUtils.constroiBotao("Tonalidade: ", 9);
	final JButton botaoMOSTRADORformcompasso = GUIUtils.constroiBotao("Compasso: ", 9);
	
	final JTextArea area = new JTextArea(30,30);
	final JScrollPane scroller = new JScrollPane(area);
	
	
	
	Tocador tocador;
	private long inicio = 0;

	private int volumeATUAL = 75;
	private JSlider sliderVolume = new JSlider(JSlider.HORIZONTAL, 0, 127, volumeATUAL);
	private JProgressBar sliderPROGRESSOinstante = new JProgressBar();

	private Container painel = getContentPane();
	private boolean soando = false;

	public static void main(String[] args) {
		GUI gui = new GUI();
		Thread thread = new Thread(gui);
		thread.start();
	}
	
	public GUI() {
		super("TocadorMidi");
		GUIUtils.personalizarInterfaceUsuario();
		configurarBotoes();

//		ImageIcon logo = new javax.swing.ImageIcon(getClass().getResource("ics25.png"));
//		setIconImage(logo.getImage());

		Color corARQ = new Color(230, 230, 228);
		try {
			JPanel p1 = new JPanel();
			p1.setBackground(new Color(34, 102, 102));
			JPanel p2 = new JPanel();
			p2.setBackground(new Color(34, 102, 102));
			JPanel p3 = new JPanel();
			p3.setBackground(new Color(34, 102, 102));
			JPanel p4 = new JPanel();
			p4.setBackground(new Color(34, 102, 102));
			JPanel p5 = new JPanel();
			p5.setBackground(new Color(34, 102, 102));
			JPanel p6 = new JPanel();
			p6.setBackground(new Color(34, 102, 102));

			JPanel painelOPERACOES = new JPanel();

			painelOPERACOES.setLayout(new GridLayout(3, 0));
			painel.setLayout(new GridLayout(6, 0));

			// -----
			p1.add(botaoMOSTRADORcaminho);

			// -----
			p2.add(botaoABRIR);
			p2.add(botaoTOCAR);
			p2.add(botaoFAZERPAUSA);
			p2.add(botaoPARAR);
			// painelOPERACOES.add(p2);

			// ------
			sliderPROGRESSOinstante.setPreferredSize(new Dimension(200, 20));
			sliderPROGRESSOinstante.setFocusable(false);
			p5.add(sliderPROGRESSOinstante);
			p5.add(botaoMOSTRADORinstante);
			p5.add(botaoAJUSTAR);

			// -----
			botaoMOSTRADORcaminho.setBackground(corARQ);
			botaoMOSTRADORarquivo.setBackground(corARQ);
			botaoMOSTRADORduracao.setBackground(corARQ);
			botaoMOSTRADORinstante.setBackground(corARQ);
			botaoMOSTRADORvalorvolume.setBackground(corARQ);
			botaoMOSTRADORandamento.setBackground(corARQ);
			botaoMOSTRADORtonalidade.setBackground(corARQ);
			botaoMOSTRADORformcompasso.setBackground(corARQ);
			area.setSize(400, 400);
			p3.add(scroller);
			p3.add(botaoMOSTRADORarquivo);
			p4.add(botaoMOSTRADORduracao);
			p4.add(botaoMOSTRADORandamento);
			p4.add(botaoMOSTRADORtonalidade);
			p4.add(botaoMOSTRADORformcompasso);

			// painelOPERACOES.add(p3);
			// painelOPERACOES.add(p4);

			JLabel vol = new JLabel("Volume: ");
			p6.add(vol);

			sliderVolume.setPreferredSize(new Dimension(150, 20));
			sliderVolume.setFocusable(false);
			p6.add(sliderVolume);

			p6.add(botaoMOSTRADORvalorvolume);
			
			
			// painel.add(painelOPERACOES);
			painel.add(p4);
			painel.add(p2);
			painel.add(p5);
			
			
			painel.add(p3);
			painel.add(p1);
			painel.add(p6);
			

			botaoMOSTRADORvalorvolume.setText("" + (volumeATUAL * 100) / 127 + "%");

			sliderVolume.addChangeListener(e -> {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int valor = source.getValue();

					ShortMessage mensagemDeVolume = new ShortMessage();
					for (int i = 0; i < 16; i++) {
						try {
							mensagemDeVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, valor);
							tocador.enviarMensagem(mensagemDeVolume, -1);
						} catch (InvalidMidiDataException e1) {
						}
					}
					volumeATUAL = valor;
					botaoMOSTRADORvalorvolume.setText("" + (volumeATUAL * 100) / 127 + "%");
				}
			});

			setSize(largura, altura);
			setLocation(posx, posy);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setVisible(true);
			// this.setResizable(false);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	private void configurarBotoes() {

		Color corOPR = new Color(180, 220, 220);

		botaoABRIR.setBackground(corOPR);
		botaoTOCAR.setBackground(corOPR);
		botaoFAZERPAUSA.setBackground(corOPR);
		botaoPARAR.setBackground(corOPR);

		botaoABRIR.setEnabled(true);
		botaoAJUSTAR.setEnabled(true);
		botaoTOCAR.setEnabled(false);
		botaoFAZERPAUSA.setEnabled(false);
		botaoPARAR.setEnabled(false);
		//Botao para ajustar inicio da música - sofri
		botaoAJUSTAR.addActionListener(e -> {
			inicio += 100000;
			System.out.println(inicio);

			tocador.ajustarPosicaoMicroSegundo(inicio);
		});

		botaoABRIR.addActionListener(e -> abrir());

		botaoTOCAR.addActionListener(e -> tocar(botaoMOSTRADORcaminho.getText(), inicio));

		botaoFAZERPAUSA.addActionListener(e -> {
			inicio = tocador.getSequenciador().getMicrosecondPosition();
			fazerpausa();
		});

		botaoPARAR.addActionListener(e -> parar());
	}

	public void tocar(String caminho, long inicio) {
		try {
			File arqmidi = new File(caminho);
			
			botaoMOSTRADORarquivo.setText("Arquivo: \"" + arqmidi.getName() + "\"");
			String dados = tocador.executar();
			area.setText(dados);
			area.setCaretPosition(0);
			long duracao = tocador.getSequenciador().getMicrosecondLength() / 1000000;
			botaoMOSTRADORduracao.setText("\nDura\u00e7\u00e3o:" + formataInstante(duracao));
			botaoMOSTRADORinstante.setText(formataInstante(0));
			tocador.ajustarPosicaoMicroSegundo(inicio);

			if (tocador.isRunning()) {
				duracao = tocador.getSequenciador().getMicrosecondLength();
				soando = true;
			} else {
				soando = false;
				tocador.parar();
				inicio = 0L;
				duracao = 0;
			}

			botaoABRIR.setEnabled(false);
			botaoTOCAR.setEnabled(false);
			botaoFAZERPAUSA.setEnabled(true);
			botaoPARAR.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fazerpausa() {
		soando = false;
		tocador.pausar();

		botaoABRIR.setEnabled(false);
		botaoTOCAR.setEnabled(true);
		botaoFAZERPAUSA.setEnabled(false);
		botaoPARAR.setEnabled(false);
	}

	public void parar() {
		soando = false;
		tocador.parar();
		inicio = 0L;

		botaoABRIR.setEnabled(true);
		botaoTOCAR.setEnabled(true);
		botaoFAZERPAUSA.setEnabled(false);
		botaoPARAR.setEnabled(false);

		sliderPROGRESSOinstante.setValue(0);
		botaoMOSTRADORinstante.setText(formataInstante(0));
	}

	public void abrir() {
		JFileChooser selecao = new JFileChooser(".");
		selecao.setFileSelectionMode(JFileChooser.FILES_ONLY);
		selecao.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (!f.isFile())
					return true;
				String name = f.getName().toLowerCase();
				if (name.endsWith(".mid"))
					return true;
				if (name.endsWith(".midi"))
					return true;
				return false;
			}

			public String getDescription() {
				return "Arquivo Midi (*.mid,*.midi)";
			}
		});
		selecao.showOpenDialog(this);
		if (selecao.getSelectedFile() != null) {
			botaoMOSTRADORcaminho.setText(selecao.getSelectedFile().toString());
			File arqseqnovo = selecao.getSelectedFile();
			if(tocador == null) {
				tocador = new Tocador(arqseqnovo);
			} else {
				tocador.trocarArquivo(arqseqnovo);
			}
			try {
				if (tocador.isRunning()) {
					tocador.parar();
				}
				Sequence sequencianova = MidiSystem.getSequence(arqseqnovo);
				double duracao = sequencianova.getMicrosecondLength() / 1000000.0d;
				double bpm = tocador.getAndamento(sequencianova, duracao);
				Par    fc  =  null;
				Track trilha[] = sequencianova.getTracks();
				Track track = trilha[0];
				String tonalidade = Tocador.getTonalidade(track);
				fc = Tocador.getFormulaDeCompasso(track);
				
				botaoMOSTRADORarquivo.setText("Arquivo: \"" + arqseqnovo.getName() + "\"");
				botaoMOSTRADORduracao.setText("\nDura\u00e7\u00e3o:" + formataInstante(duracao));
				botaoMOSTRADORandamento.setText("andamento: " + Math.round(bpm));
				botaoMOSTRADORtonalidade.setText("Tonalidade: " + tonalidade);
				botaoMOSTRADORformcompasso.setText("Compasso: " + fc.getX() +":"+ (int)(Math.pow(2, fc.getY())));
				
				botaoTOCAR.setEnabled(true);
				botaoFAZERPAUSA.setEnabled(false);
				botaoPARAR.setEnabled(false);
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}
		
	}

	public void run() {
		double dur;
		double t;
		int pos = 0;

		while (true) {
			if (soando) {
				dur = tocador.getSequenciador().getMicrosecondLength() / 1000000;
				t = tocador.getSequenciador().getMicrosecondPosition() / 1000000;
				pos = (int) ((t * 100) / dur);
				try {
					sliderPROGRESSOinstante.setValue(pos);
					botaoMOSTRADORinstante.setText(formataInstante(t));
					tocador.retardo(1000);
					if (t >= dur) {
						sliderPROGRESSOinstante.setValue(0);
						botaoMOSTRADORinstante.setText(formataInstante(0));

						botaoABRIR.setEnabled(true);
						botaoTOCAR.setEnabled(true);
						botaoFAZERPAUSA.setEnabled(false);
						botaoPARAR.setEnabled(false);
					}
				} catch (Exception e) {
				}
			}

			else {
				try {
					tocador.retardo(1000);
				} catch (Exception e) {
//					System.out.println(e.getMessage());
				}
			}
		}

	}

	public String formataInstante(double t1) {

		// --------início
		double h1 = (int) (t1 / 3600.0);
		double m1 = (int) ((t1 - 3600 * h1) / 60);
		double s1 = (t1 - (3600 * h1 + 60 * m1));

		double s1r = (t1 - (3600 * h1 + 60 * m1));

		String sh1 = "";
		String sm1 = "";
		String ss1 = "";

		if (h1 == 0)
			sh1 = "00";
		else if (h1 < 10)
			sh1 = "0" + reformata(h1, 0);
		else if (h1 < 100)
			sh1 = "" + reformata(h1, 0);
		else
			sh1 = "" + reformata(h1, 0);

		if (m1 == 0)
			sm1 = "00";
		else if (m1 < 10)
			sm1 = "0" + reformata(m1, 0);
		else if (m1 < 60)
			sm1 = "" + reformata(m1, 0);

		if (s1 == 0)
			ss1 = "00";
		else if (s1 < 10)
			ss1 = "0" + reformata(s1r, 2);
		else if (s1 < 60)
			ss1 = reformata(s1r, 2);

		return "\n" + "   " + sh1 + "h " + sm1 + "m " + ss1 + "s";
	}

	public String reformata(double x, int casas) {
		DecimalFormat df = new DecimalFormat();
		df.setGroupingUsed(false);
		df.setMaximumFractionDigits(casas);
		return df.format(x);
	}

	// ---procedimento para customizar a interface GUI
	

}
