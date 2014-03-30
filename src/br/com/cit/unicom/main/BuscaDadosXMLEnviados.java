package br.com.cit.unicom.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.swing.JPanel;

import br.com.cit.unicom.staticclass.MetodosComuns;

public class BuscaDadosXMLEnviados extends JPanel {

	/**
     * 
     */
    private static final long serialVersionUID = 2516241047838960746L;

    /**
	 * @param args
	 */
	public static void main(String[] args) {
		start();
		System.exit(UNDEFINED_CONDITION);
	}

	public static void start() {
		List<File> arquivosLog = MetodosComuns.buscaArquivosLog();
		File buscaListaCarregamentos = MetodosComuns.buscaListaCarregamentos();
		List<String> registrosLidos = MetodosComuns.lerArquivoTxt(buscaListaCarregamentos);

		BuscaDadosXMLEnviados.realizaCruzamentoDeDados(arquivosLog, registrosLidos);
	}

    public static void realizaCruzamentoDeDados(List<File> arquivosLog,
    		List<String> registrosLidos) {
    
    	String carregamentos = "";
    
    	for (File file : arquivosLog) {
    		try {
    			// instancia do arquivo que vou ler
    			FileReader reader = new FileReader(file);
    			BufferedReader leitor = new BufferedReader(reader);
    			String linha = null;
                
    			while ((linha = leitor.readLine()) != null) {
                    for (String string : registrosLidos) {
                        //Se encontrar nestas condições printa o XML
                        if ((linha.contains("<RequestBilling") || linha.contains("<CompositeRequestBilling")) && linha.contains(">" + string + "<")
                                && !carregamentos.contains(string)) {
                            carregamentos += string + ";";
                            System.out.println(linha);
                        }
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
