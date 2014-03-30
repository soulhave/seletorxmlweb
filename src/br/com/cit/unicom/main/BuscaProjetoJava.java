package br.com.cit.unicom.main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import br.com.cit.unicom.staticclass.MetodosComuns;

public class BuscaProjetoJava extends JPanel {

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
		JLabel label = new JLabel("Processar Matriz");
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
				    	  BuscaProjetoJava.start();
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
		BuscaProjetoJava.realizaCruzamentoDeDados(arquivosLog);
		jProgressBar.setString("PARADO");
		jProgressBar.setValue(0);
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
					int i = 0; 
					while ((linhaLida = leitor.readLine()) != null) {
						//Verificação deve ser colocada aqui.
						i++;
						final String exceptionString = "Exception:";
						if (linhaLida.contains(exceptionString)) {
							
						}
					}
						
					leitor.close();
					reader.close();
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
    	}
    }
}
