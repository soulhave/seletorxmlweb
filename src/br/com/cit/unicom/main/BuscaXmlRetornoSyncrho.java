package br.com.cit.unicom.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.swing.JPanel;

import br.com.cit.unicom.staticclass.MetodosComuns;

public class BuscaXmlRetornoSyncrho extends JPanel {

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

		BuscaXmlRetornoSyncrho.realizaCruzamentoDeDados(arquivosLog, registrosLidos);
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
    				if(linha.contains("<ns0:returnStage>SYNCHRO<")){ //Se for registro Syncrho
    					String xml = "<ns0:ResponseTCFL xmlns:ns0=\"http://www.vale.com/CM/UnifComercial/LogisticBilling/Schema\">";
    					xml+=linha.trim(); //Linha do XML
    					xml+=leitor.readLine().trim(); //message
    					xml+=leitor.readLine().trim(); //identifysystem
    					String numunicom = leitor.readLine().trim();
    					xml+=numunicom;
    					findRegistro: for (String string : registrosLidos) { //
    					        if (numunicom.contains(">" + string + "<")	&& !carregamentos.contains(string)) { //Tiver na lista
    					                String endOfFile = "</ns0:ResponseTCFL>";
                                        preencheXml: while ((linha = leitor.readLine()) != null) { //Recupera xml restante do XML											
    									    xml+=linha;
    										if(linha.contains(endOfFile)){
    											break preencheXml;
    										}
    									}
    					                xml = xml.substring(0,xml.indexOf(endOfFile)+endOfFile.length());
    					                carregamentos += string + ";";
    									System.out.println(string+";"+xml); //Printa o XML e quebra o for 
    									break findRegistro;
    							}
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
