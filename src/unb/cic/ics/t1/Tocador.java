package unb.cic.ics.t1;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;


public class Tocador {
	
	final int MESSAGEM_ANDAMENTO = 0x51;  
	final static int MENSAGEM_TONALIDADE = 0x59;
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
	public void executar() throws InvalidMidiDataException {

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
	
	float getAndamento(Track trilha[],int i) throws InvalidMidiDataException
    {       
       MidiMessage mensagem = trilha[i].get(i).getMessage();
       if(((MetaMessage) mensagem).getType() == MESSAGEM_ANDAMENTO)
       {
            MetaMessage mm   = (MetaMessage)mensagem;
            byte[]      data = mm.getData();

            byte primeiro = data[0];
            byte segundo  = data[1];
            byte terceiro = data[2];

            long microseg = (long)(primeiro*Math.pow(2, 16) +
                                   segundo *Math.pow(2,  8) +
                                   terceiro
                                  );

            //int andamento = (int)(60000000.0/microseg);
            //return "Andamento: " + andamento + " bpm";     
                                                       
            return (float)(60000000.0/microseg);                                    
       }
       else{
    	   return 0;
       }
    }
	
	public float getAndamento(Sequence sequencia,double duracao){
		long totaltiques = sequencia.getTickLength();
		int resolucao = sequencia.getResolution();
		float durtique = (float) duracao / totaltiques;
		float durseminima = durtique * resolucao;
		float bpm = 60 / durseminima;
		return bpm;
	}

	private void exibirDados() throws InvalidMidiDataException {
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
		
		Track[] trilhas = sequencia.getTracks();
        
        for(int i=0; i<trilhas.length; i++)
        {
          System.out.println("Início da trilha nº " + i + " **********************");
          System.out.println("------------------------------------------");
          Track trilha =  trilhas[i];
          
          Par    fc  =  null;
          String st  = "--";
          String stx = "--";
          
          //---MetaMensagem de fórmula de compasso
          if(i==0) 
        	  fc = getFormulaDeCompasso(trilha);

          //---MetaMensagem de tonalidade
          if(i==0)
	          try{ 
	        	  st =  getTonalidade(trilha);
	             }
	          catch(Exception e){}
          
          //---MetaMensagem de texto
          try{ 
        	  stx =  getTexto(trilha);
             }
          catch(Exception e){}
          
          if(fc!=null)
           System.out.println("Fórmula de Compasso: " + fc.getX() +":"+ (int)(Math.pow(2, fc.getY())) );
      
           System.out.println("Tonalidade         : " + st);
           System.out.println("Texto              : " + stx);
           System.out.println("------------------------------------------");
           
           for(int j=0; j<trilha.size(); j++)
           {
             System.out.println("Trilha nº " + i );
             System.out.println("Evento nº " + j);
             MidiEvent   e          = trilha.get(j);
             MidiMessage mensagem   = e.getMessage();
             long        tique      = e.getTick();
             
             int n = mensagem.getStatus();
             
             String nomecomando = ""+n;
             
             switch(n)
             {
                 case 128: nomecomando = "noteON"; break;
                 case 144: nomecomando = "noteOFF"; break;
                 case 255: nomecomando = "MetaMensagem  (a ser decodificada)"; break; 
                 //---(introduzir outros casos)
             }
             
             System.out.println("       Mensagem: " + nomecomando );
             System.out.println("       Instante: " + tique );
             System.out.println("------------------------------------------");                                    
           }
           
        }
		
	}
	
	 static final int MENSAGEM_TEXTO = 0x01;  
	    
	    static String getTexto(Track trilha) throws InvalidMidiDataException
	    {       
	       String stexto = "";

	       for(int i=0; i<trilha.size(); i++)
	       { MidiMessage m = trilha.get(i).getMessage();
	              
	         if(((MetaMessage)m).getType() == MENSAGEM_TEXTO)    
	         {                
	           MetaMessage mm  = (MetaMessage)m;
	           byte[]     data = mm.getData();

	           for(int j=0; j<data.length; j++)
	           { stexto += (char)data[j];
	           }         
	        }       
	     }    
	     return stexto;
	    }

    
    static String getTonalidade(Track trilha) throws InvalidMidiDataException
    {       
       String stonalidade = "";
       for(int i=0; i<trilha.size(); i++)
       { MidiMessage m = trilha.get(i).getMessage();
       
              
       if(((MetaMessage)m).getType() == MENSAGEM_TONALIDADE)    
       {
            MetaMessage mm        = (MetaMessage)m;
            byte[]     data       = mm.getData();
            byte       tonalidade = data[0];
            byte       maior      = data[1];

            String       smaior = "Maior";
            if(maior==1) smaior = "Menor";

            if(smaior.equalsIgnoreCase("Maior"))
            {
                switch (tonalidade)
                {
                    case -7: stonalidade = "Dób Maior"; break;
                    case -6: stonalidade = "Solb Maior"; break;
                    case -5: stonalidade = "Réb Maior"; break;
                    case -4: stonalidade = "Láb Maior"; break;
                    case -3: stonalidade = "Mib Maior"; break;
                    case -2: stonalidade = "Sib Maior"; break;
                    case -1: stonalidade = "Fá Maior"; break;
                    case  0: stonalidade = "Dó Maior"; break;
                    case  1: stonalidade = "Sol Maior"; break;
                    case  2: stonalidade = "Ré Maior"; break;
                    case  3: stonalidade = "Lá Maior"; break;
                    case  4: stonalidade = "Mi Maior"; break;
                    case  5: stonalidade = "Si Maior"; break;
                    case  6: stonalidade = "Fá# Maior"; break;
                    case  7: stonalidade = "Dó# Maior"; break;
                }
            }

            else if(smaior.equalsIgnoreCase("Menor"))
            {
                switch (tonalidade)
                {
                    case -7: stonalidade = "Láb Menor"; break;
                    case -6: stonalidade = "Mib Menor"; break;
                    case -5: stonalidade = "Sib Menor"; break;
                    case -4: stonalidade = "Fá Menor"; break;
                    case -3: stonalidade = "Dó Menor"; break;
                    case -2: stonalidade = "Sol Menor"; break;
                    case -1: stonalidade = "Ré Menor"; break;
                    case  0: stonalidade = "Lá Menor"; break;
                    case  1: stonalidade = "Mi Menor"; break;
                    case  2: stonalidade = "Si Menor"; break;
                    case  3: stonalidade = "Fá# Menor"; break;
                    case  4: stonalidade = "Dó# Menor"; break;
                    case  5: stonalidade = "Sol# Menor"; break;
                    case  6: stonalidade = "Ré# Menor"; break;
                    case  7: stonalidade = "Lá# Menor"; break;
                }
            }
         }
      }
      return stonalidade;
    }
          

	
static final int FORMULA_DE_COMPASSO = 0x58;
    
    static Par getFormulaDeCompasso(Track trilha)
    {   int p=1;
        int q=1;

        for(int i=0; i<trilha.size(); i++)
        {
          MidiMessage m = trilha.get(i).getMessage();
          if(m instanceof MetaMessage) 
          {
            if(((MetaMessage)m).getType()==FORMULA_DE_COMPASSO)
            {
                MetaMessage mm = (MetaMessage)m;
                byte[] data = mm.getData();
                p = data[0];
                q = data[1];
                return new Par(p,q);
            }
          }
        }
        return new Par(p,q);
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
