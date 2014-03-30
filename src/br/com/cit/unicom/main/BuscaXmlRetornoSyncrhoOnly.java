package br.com.cit.unicom.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import br.com.cit.unicom.staticclass.MetodosComuns;

public class BuscaXmlRetornoSyncrhoOnly extends JPanel {

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

		BuscaXmlRetornoSyncrhoOnly.realizaCruzamentoDeDados(arquivosLog, registrosLidos);
	}

    public static void realizaCruzamentoDeDados(List<File> arquivosLog,
    		List<String> registrosLidos) {
    
        List<String> serieCte = Arrays.asList(new String[]{"005","006","007","100","101","102","103","104","105","106","107","200","201","300","301","400","401"}); ;        
    	String carregamentos = "";
    
    	for (File file : arquivosLog) {
    		try {
    			// instancia do arquivo que vou ler
    			FileReader reader = new FileReader(file);
    			BufferedReader leitor = new BufferedReader(reader);
    			String linha = null;
    			int i = 0;
                while ((linha = leitor.readLine()) != null) {
                    i++;
                    for (String string : registrosLidos) {
                        //Se encontrar nestas condições printa o XML
                        if ((linha.contains("<RequestBilling") || linha.contains("<CompositeRequestBilling")) && linha.contains(">" + string + "<")
                                && !carregamentos.contains(string)) {
                            carregamentos += string + ";";
                            String endFile = "</RequestBilling>";
                            String serie = "";
                            //Definição de CTE ou DESPACHO
                            String s = "<dispatchSeries>";
                            if(linha.contains(s)){
                                serie = linha.substring(linha.indexOf(s)+s.length(), linha.indexOf(s)+s.length()+3);
                            }
                                
                            //System.out.println("\nArquivo:"+file.getAbsolutePath()+" Linha:"+i);
                            System.out.println(serie+";"+(serieCte.contains(serie)?"CTE":"DES")+";"+ string + ";"+linha.substring(0,linha.indexOf(endFile)+endFile.length()));
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
