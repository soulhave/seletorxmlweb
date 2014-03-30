package br.com.cit.unicom.main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import br.com.cit.unicom.staticclass.MetodosComuns;

public class BuscaExceptions extends JPanel {

	private static final String ZERO = "0";
	private static final String QUEBRA_LINHA = "\n";
	private static final String SEPARADOR = ";";
	public static HashMap<String, Integer> EXCECAO_DATA_HORA = new HashMap<String, Integer>();
	public static HashMap<String, Integer> EXCECAO_QUANTIDADE = new HashMap<String, Integer>();
	public static HashMap<String, Integer> EXCECAO_QUANTIDADE_HORA = new HashMap<String, Integer>();
	public static HashMap<String, Integer> EXCECAO_QUANTIDADE_DIA_SEMANA = new HashMap<String, Integer>();
	public static HashMap<String, Integer> EXCECAO_QUANTIDADE_SEMANA = new HashMap<String, Integer>();
	public static HashMap<String, Integer> EXCECAO_QUANTIDADE_DIARIO = new HashMap<String, Integer>();
	public static String TIPO_EXCECAO_DATA_HORA = ""; //Geral
	public static String TIPO_EXCECAO_QUANTIDADE = ""; //Quantidade total por exceção (Exception)
	public static String TIPO_EXCECAO_QUANTIDADE_HORA = ""; //Quantidade total por hora Chave (Exception;hora)
	public static String TIPO_EXCECAO_QUANTIDADE_DIA_SEMANA = ""; //Quantidade total por dia da semana (Segunda, terça, quarta...). Chave (Exception;dia_da_semana)
	public static String TIPO_EXCECAO_QUANTIDADE_SEMANA = ""; //Número da semana no ano. Chave (Exception;semana;ano)
	public static String TIPO_EXCECAO_QUANTIDADE_DIARIO = ""; //Data (Exception;data)
	public static List<String> DIA_SEMANA = new ArrayList<String>();
	public static List<String> HORA_DIA = new ArrayList<String>();
	public static List<String> SEMANAS = new ArrayList<String>();
	public static List<String> DIARIO = new ArrayList<String>();
	public static String PATH = "csv"+File.separator;

	public static JProgressBar jProgressBar = new JProgressBar();
	
	/**
     * 
     */
    private static final long serialVersionUID = 2516241047838960746L;

    /**
	 * @param args
	 */
	public static void main(String[] args) {
		
		JFrame dialog = new JFrame();
		JFrame.setDefaultLookAndFeelDecorated(true);
		dialog.setBounds(0, 0, 500, 180);
		dialog.setLayout(null);
		dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dialog.setResizable(false);

		//Label de processamento
		JLabel label = new JLabel("Processar Métricas de Log's");
		label.setBounds(10,10,470,40);
		dialog.add(label);
		label.setVisible(true);
		
		//Meeter
		dialog.add(jProgressBar);
		jProgressBar.setBounds(10,70,470,40);
		jProgressBar.setMinimum(0);
		jProgressBar.setStringPainted(true);
		jProgressBar.setString("PARADO");
		jProgressBar.setVisible(true);
		
		//Butoes
		JButton jbut = new JButton("Iniciar");
		jbut.setBounds(170, 130, 70, 20);
		jbut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 Thread queryThread = new Thread() {
				      public void run() {
				    	  BuscaExceptions.start();
				      }
				    };
				    queryThread.start();
			}
		});
		dialog.add(jbut);

		JButton jbut2 = new JButton("Sair");
		jbut2.setBounds(250, 130, 70, 20);
		jbut2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(UNDEFINED_CONDITION);
			}
		});
		dialog.add(jbut2);
		dialog.setVisible(true);
		centraliza(dialog);
	}

	public static void centraliza(JFrame frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		frame.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
	}
	
	public static void start() {
		List<File> arquivosLog = MetodosComuns.buscaArquivosLog();
		BuscaExceptions.realizaCruzamentoDeDados(arquivosLog);

		//Print Metricas
		TIPO_EXCECAO_QUANTIDADE = printMetricas(EXCECAO_QUANTIDADE,"SOMENTE EXCEÇÃO",null); //SOMENTE EXCEÇÃO
		TIPO_EXCECAO_QUANTIDADE_HORA = printMetricas(EXCECAO_QUANTIDADE_HORA,"EXCEÇÃO POR HORA", HORA_DIA); //EXCEÇÃO POR HORA
		TIPO_EXCECAO_QUANTIDADE_DIA_SEMANA = printMetricas(EXCECAO_QUANTIDADE_DIA_SEMANA,"TIPO_EXCECAO_DIA_SEMANA",DIA_SEMANA); //EXCEÇÃO POR DIA DA SEMANA
		TIPO_EXCECAO_QUANTIDADE_SEMANA = printMetricas(EXCECAO_QUANTIDADE_SEMANA,"EXCEÇÃO POR SEMANA",SEMANAS); //EXCEÇÃO POR SEMANA
		TIPO_EXCECAO_QUANTIDADE_DIARIO = printMetricas(EXCECAO_QUANTIDADE_DIARIO,"EXCEÇÃO POR DIA",DIARIO); //EXCEÇÃO POR SEMANA

		//Gera os arquivos
		gerarArquivos();
		
		jProgressBar.setString("PARADO");
		jProgressBar.setValue(0);
	}

	/**
	 * Gera arquivos
	 */
	private static void gerarArquivos() {
		final String raizNameFiles = (new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss")).format(System.currentTimeMillis());  
		final String diretorio = PATH+raizNameFiles+File.separator;
		
		//Cria diretório se ele não existir
		File file = new File(diretorio);
		if((file.mkdirs())){
			try{
				MetodosComuns.criarArquivo(diretorio + "DETALHADO_COM_DATA_HORA",TIPO_EXCECAO_DATA_HORA);
				MetodosComuns.criarArquivo(diretorio + "QUANTIDADE",TIPO_EXCECAO_QUANTIDADE);
				MetodosComuns.criarArquivo(diretorio + "QUANTIDADE_HORA",TIPO_EXCECAO_QUANTIDADE_HORA);
				MetodosComuns.criarArquivo(diretorio + "QUANTIDADE_DIA_SEMANA",TIPO_EXCECAO_QUANTIDADE_DIA_SEMANA);
				MetodosComuns.criarArquivo(diretorio + "QUANTIDADE_PERIODO_SEMANAL",TIPO_EXCECAO_QUANTIDADE_SEMANA);
				MetodosComuns.criarArquivo(diretorio + "QUANTIDADE_PERIODO_DIARIO",TIPO_EXCECAO_QUANTIDADE_DIARIO);

				//Gerando arquivos excel
				jProgressBar.setValue(jProgressBar.getValue()+1);
				jProgressBar.setString("GERANDO ARQUIVO EXCEL...");
				jProgressBar.repaint();
				MetodosComuns.converteCSVtoXLS(diretorio, raizNameFiles);
				MetodosComuns.deleteCSVfiles(diretorio);
				
				JOptionPane.showMessageDialog(null, "Arquivos gerados com sucesso, pasta:"+QUEBRA_LINHA+file.getAbsolutePath(),"Sucesso",JOptionPane.INFORMATION_MESSAGE); 
			}catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Erro na geração dos arquivos!","Erro",JOptionPane.ERROR_MESSAGE);
			}
		}else{
			JOptionPane.showMessageDialog(null, "Erro na geração dos arquivos!","Erro",JOptionPane.ERROR_MESSAGE);
		}
		
		
	}

	/**
	 * Printa as metricas de excecoes encontradas
	 */
	private static String printMetricas(Map<String, Integer> impressoes,String titulo,List<String> itens) {
		String concat = "";
		System.out.println("\n\n---"+titulo+"---\n");
		
		if (itens != null) {
			Collections.sort(itens); //Ordena a lista
			//Printa cabeçalho
			concat+=titulo+SEPARADOR;
			
			for (String chave : impressoes.keySet()) { // Percorre todas as chaves
				String exception = chave.split(SEPARADOR)[0]; //Quebra somente as exceções
				if(!concat.contains(exception)) { //Caso a exeção não tenha sido impressa
					concat+=exception+SEPARADOR; //Imprime a exceção.
				}
			}
			
			
			System.out.println(concat);
			concat+=QUEBRA_LINHA;
			
			//Quebras
			String[] head = concat.split(SEPARADOR); //Quebra de dados
			
			//Printa o corpo de toda matriz de acordo com o que foi impresso
			for (String iten : itens) {
				String linhaFormacao = iten.substring(iten.indexOf("-")+1) + SEPARADOR;
				for (int i = 1; i < (head.length-1); i++) {
					Integer valor = impressoes.get(head[i]+SEPARADOR+iten); //Pega o valor da chave montanda
					if(valor!=null){ //Caso valor não seja nulo
						linhaFormacao += valor.toString()+SEPARADOR;
					}else{
						linhaFormacao += ZERO+SEPARADOR;
					}
				}
				
				System.out.println(linhaFormacao); //Printa a linha de formação
				concat += linhaFormacao + QUEBRA_LINHA; //Concatena no arquivo final
			}
			
		}else{//Caso não tenha itens compostos
			for (String chave : impressoes.keySet()) {
				final String stringFormatada = chave + SEPARADOR
						+ impressoes.get(chave);
				concat += stringFormatada + QUEBRA_LINHA;
				System.out.println(stringFormatada);
			}			
		}
		
		return concat;
	}

    /**
     * Cruzamento de dados
     * @param arquivosLog
     */
	public static void realizaCruzamentoDeDados(List<File> arquivosLog) {
    
		jProgressBar.setMaximum(arquivosLog.size()+1);
		int n = 0;
    	for (File file : arquivosLog) {
    		jProgressBar.setValue(++n);
    		jProgressBar.setString("PROCESSANDO ARQUIVO:"+file.getName().toUpperCase());
    		jProgressBar.repaint();
			if(!file.getName().contains(".err")
				&&!file.getName().contains("-painel.log")
				&&!file.getName().contains("performance-cte.log")){
				
				//Se for do tipo .gz descompacta
				while(file.getName().contains(".gz")){
					jProgressBar.setString("DESCOMPACTANDO ARQUIVO:"+file.getName().toUpperCase());
					file = MetodosComuns.descompactarArquivos(file.getAbsolutePath());
					jProgressBar.setString("PROCESSANDO ARQUIVO:"+file.getName().toUpperCase());
				}
				
				try {
					// instancia do arquivo que vou ler
					FileReader reader = new FileReader(file);
					BufferedReader leitor = new BufferedReader(reader);
					String linhaLida = null;
					String linhaAnterior = "";
					int i = 0; 
					while ((linhaLida = leitor.readLine()) != null) {
						//Verificação deve ser colocada aqui.
						i++;
						final String exceptionString = "Exception:";
						if (linhaLida.contains(exceptionString)
								&& !linhaAnterior.isEmpty()
								&& !linhaLida.contains("Resources not defined for Validator")
								&& !linhaLida.toUpperCase().contains("CAUSED BY:")
								&& !linhaLida.contains("<messageID>")
								&& !linhaAnterior.toUpperCase().contains("NESTED EXCEPTION IS:")
								&& !linhaLida.contains("Resources not defined for Validator")
								&& !linhaLida.toUpperCase().contains("MSGESPECIFICA")
								&& !linhaLida.toUpperCase().contains("MSG = REQUEST=")
								&& !linhaLida.toUpperCase().contains("REQUEST = REQUEST=")
								&& !linhaLida.toUpperCase().contains("THIS LOADER HAS BEEN CLOSED AND SHOULD NOT BE IN USE.")
								&& !linhaLida.toUpperCase().contains("INFO  COM.VALE.SC.UNIFICACAOCOMERCIAL.UNICOM.FATURAMENTO.INTERFACEDESPACHO.SERVICES.BO.INTERFACEDESPACHOBO.PROCESSARDESPACHO")
								&& !linhaLida.toUpperCase().contains("INFO  COM.VALE.SC.UNIFICACAOCOMERCIAL.UNICOM.FATURAMENTO.INTERFACEDESPACHO.SERVICES.BO.INTERFACEDESPACHOBO.REPROCESSARDESPACHOREJEITADO")
								&& !linhaAnterior.contains("Thread.java")) {
							String descricaoException = linhaLida.substring(linhaLida.indexOf(exceptionString)+exceptionString.length());
							String exception = linhaLida.substring(linhaLida.substring(0,linhaLida.indexOf(exceptionString)+exceptionString.length()).lastIndexOf(".")+1,linhaLida.indexOf(exceptionString)+exceptionString.length()-1);
							String dataHora = "";
							Calendar dateTime = null;
							if(linhaAnterior.contains("Shutting down...") 
									|| linhaAnterior.contains("in org.apache.log4j.RollingFileAppender.")
									|| linhaLida.contains("java.io.IOException")
							){ //Se contiver shutdown
								dataHora = linhaAnterior.substring(0,18);
								dateTime = MetodosComuns.converteStringDate(dataHora,"yy/MM/dd HH:mm:ss");
								descricaoException = exception = "Shutting down...";
							}else{
								dataHora = linhaAnterior.substring(linhaAnterior.lastIndexOf(" - ")+3,linhaAnterior.lastIndexOf(" - ")+22);
								dateTime = MetodosComuns.converteStringDate(dataHora,"dd/MM/yyyy HH:mm:ss");
							}
							
							
							if(!verificaSeJaExisteExcecaoMesmaDataEHora(exception, dataHora)){
								final String linhaFormatada = file.getCanonicalPath() + SEPARADOR+i+SEPARADOR+ exception + SEPARADOR + descricaoException+SEPARADOR+dataHora;
								TIPO_EXCECAO_DATA_HORA += linhaFormatada + QUEBRA_LINHA;
								System.out.println(linhaFormatada);
								
								registraQtdeExceptionsGeradas(exception,EXCECAO_QUANTIDADE,null);
								registraQtdeExceptionsGeradas(exception + SEPARADOR + MetodosComuns.estiloHora(dateTime.get(Calendar.HOUR_OF_DAY)),EXCECAO_QUANTIDADE_HORA,HORA_DIA);
								registraQtdeExceptionsGeradas(exception + SEPARADOR + MetodosComuns.diaSemana(dateTime.get(Calendar.DAY_OF_WEEK)),EXCECAO_QUANTIDADE_DIA_SEMANA,DIA_SEMANA );
								registraQtdeExceptionsGeradas(exception + SEPARADOR + MetodosComuns.semana(dateTime.get(Calendar.WEEK_OF_YEAR))+"/"+dateTime.get(Calendar.YEAR),EXCECAO_QUANTIDADE_SEMANA,SEMANAS);
								registraQtdeExceptionsGeradas(exception + SEPARADOR + MetodosComuns.diario(dateTime),EXCECAO_QUANTIDADE_DIARIO,DIARIO);
							}
							
						}
						linhaAnterior = linhaLida.toString();
					}
					leitor.close();
					reader.close();
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
    	}
    }

	/**
	 * Verifica se existe o cara dentro da lista
	 * @param exception
	 * @param dataHora
	 * @return
	 */
	private static boolean verificaSeJaExisteExcecaoMesmaDataEHora(String exception, String dataHora) {
		
		final String chave = exception+SEPARADOR+exception+SEPARADOR+dataHora;
		if(EXCECAO_DATA_HORA.get(chave)==null){
			EXCECAO_DATA_HORA.put(chave, 0);
			return false;
		}
		return true;
	}

    /**
     * Registra a quantidade de exceções encontradas por tipo de exceção.
     * @param exception
     * @param itens 
     */
    private static void registraQtdeExceptionsGeradas(String exception, HashMap<String, Integer> map, List<String> itens) {
		
    	Integer total = map.get(exception);
    	
    	if(total==null){
    		total = 1;
    	}else{
    		total++;
    	}
    	
    	if(itens!=null){ //Itens a serem verificados
    		String[] split = exception.split(SEPARADOR); //Quebra a chave
    		if(split.length>1) //Verifica se ela é complexa (mais de um item)
    			Collections.sort(itens);
    			if(Collections.binarySearch(itens, split[1])<0) //Caso seja verifica se o dado existe na lista de itens. Caso não exista, insere.
    				itens.add(split[1]);
    	}
    	
    	map.put(exception, total);
    		
	}
}
