package br.com.cit.unicom.staticclass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import javax.swing.JFileChooser;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

public class MetodosComuns {

    private static final String CSV = ".csv";

	public static List<String> lerArquivoTxt(File file) {
    	List<String> list = new ArrayList<String>();
    
    	try {
    		// instancia do arquivo que vou ler
    		FileReader reader = new FileReader(file);
    		BufferedReader leitor = new BufferedReader(reader);
    		String linha = null;
    
    		// loop que percorrer� todas as linhas do arquivo.txt que eu quero
    		// ler
    		while ((linha = leitor.readLine()) != null) {
    			list.add(linha.trim());
    		}
    
    		leitor.close();
    		reader.close();
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    
    	return list;
    }

    public static List<File> buscaArquivosLog() {
    	JFileChooser jf = new JFileChooser();
    	File[] arquivosLog = null;
    	List<File> retorno = new ArrayList<File>();
    
    	jf.setMultiSelectionEnabled(true);
    	jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    	
    	int showOpenDialog = jf.showOpenDialog(jf);
    	System.out.println(showOpenDialog);
    	if (showOpenDialog == 0) {
    		arquivosLog = jf.getSelectedFiles();
    		if (arquivosLog != null) {
    			System.out.println("====Arquivos de Log:===");
    			for (File file : arquivosLog) {
    				if(file.isDirectory()){
    					retorno.addAll(subFoldersAndFiles(file.listFiles()));
    				}else if(file.isFile()){
    					retorno.add(file);
    					System.out.println(file.getName());
    				}
    			}
    			System.out.println("====Arquivos de Log:===");
    		}
    	}
    	return retorno;
    }

    //Subdiretorios, percorre e inclui os arquivos
    public static List<File> subFoldersAndFiles(File[] listFiles) {
    	
    	List<File> retorno = new ArrayList<File>();
    	
    	for (File file2 : listFiles) {
    		if(file2.isFile()){
    			retorno.add(file2);
    			System.out.println(file2.getName());
    		}else{
    			retorno.addAll(subFoldersAndFiles(file2.listFiles()));
    		}
    	}
    	
    	return retorno;
    }

    public static File buscaListaCarregamentos() {
    	JFileChooser jf = new JFileChooser();
    
    	jf.setMultiSelectionEnabled(false);
    	int showOpenDialog = jf.showOpenDialog(jf);
    	System.out.println(showOpenDialog);
    	if (showOpenDialog == 0) {
    		return jf.getSelectedFile();
    	}
    	return null;
    }
    
	/**
	 * Cria arquivo csv
	 * @param nameFiles
	 */
	public static void criarArquivo(String nameFiles,String body) {
		PrintStream out = null;
		try {
		    out = new PrintStream(new FileOutputStream(nameFiles+CSV),false, "UTF-8");
		    out.print(body);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		finally {
		    if (out != null) out.close();
		}
	}

	/**
	 * Converte String Date
	 * @param date
	 * @return
	 */
	public static Calendar converteStringDate(String date,String pattern){
		if(date!=null && !date.isEmpty()){
			Calendar cal = Calendar.getInstance(Locale.getDefault());
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));
			cal.getTimeZone();
			try {
				cal.setTimeInMillis(sdf.parse(date).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return cal;
		}
		return null;
	}
	
	/**
	 * Dia da semana
	 * @param diaSemana
	 * @return
	 */
	public static String diaSemana(int diaSemana){
		String semana = "";
		switch (diaSemana) {
		case 1:
			semana ="1-Domingo";
			break;
		case 2:
			semana ="2-Segunda-feira";
			break;
		case 3:
			semana ="3-Terça-feira";
			break;
		case 4:
			semana ="4-Quarta-feira";
			break;
		case 5:
			semana ="5-Quinta-feira";
			break;
		case 6:
			semana ="6-Sexta-feira";
			break;
		case 7:
			semana ="7-Sábado";
			break;
		default:
			semana ="Este não é um dia válido!";
		}
		
		return semana;
	}

	public static String estiloHora(int i) {
		String hora = Integer.toString(i);
		return (i<10?"0":"")+hora+"-"+String.format("%2s", hora).replace(" ", "0")+":00:00";
	}

	public static String semana(int i) {
		String semana = Integer.toString(i);
		return (i<10?"0":"")+semana+"-"+semana;
	}

	public static String diario(Calendar dateTime) {
		String dateFormat = (new SimpleDateFormat("dd/MM/yyyy")).format(dateTime.getTime());
		return (new SimpleDateFormat("yyyyMMdd").format(dateTime.getTime()))+"-"+dateFormat;
	}
	
	public static String lpad(String str, int n, String x){
		for (int i=0; i<(n-str.length()); i++) {
		    str = x + str;
		}
		return str;
	}
	
	/**
	 * Apaga os CSV
	 * @param path
	 * @return
	 */
	public static boolean deleteCSVfiles(String path) {
		File folder = new File(path);
		File[] lista = folder.listFiles();
		for (File file : lista) {
			if (file.getName().indexOf(CSV) != -1) {
				if(!file.delete())
					return false;
			}
		}
		return true;
	}

	/**
	 * Converte todos os csv de um diretório em um único XLS
	 * @param path - Caminho onde contém os csv
	 * @param fileXLS - Arquivo xls
	 */
	public static void converteCSVtoXLS(String path, String fileXLS) {
		try {
			HSSFWorkbook hwb = new HSSFWorkbook();
			File folder = new File(path);
			File[] lista = folder.listFiles();
			for (File file : lista) {
				if (file.getName().indexOf(CSV) != -1) {
					ArrayList<ArrayList<String>> arList = null;
					ArrayList<String> al = null;
					String fName = file.getAbsolutePath();
					String thisLine;
					FileInputStream fis = new FileInputStream(fName);
					BufferedReader d = new BufferedReader(new InputStreamReader(fis));
					int i = 0;
					arList = new ArrayList<ArrayList<String>>();
					while ((thisLine = d.readLine()) != null) {
						al = new ArrayList<String>();
						String strar[] = thisLine.split(";");
						for (int j = 0; j < strar.length; j++) {
							al.add(strar[j]);
						}
						arList.add(al);
						i++;
					}

					HSSFSheet sheet = hwb.createSheet(file.getName().substring(0,file.getName().indexOf(".")));
					for (int k = 0; k < arList.size(); k++) {
						ArrayList<?> ardata = arList.get(k);
						HSSFRow row = sheet.createRow((short) 0 + k);
						for (int p = 0; p < ardata.size(); p++) {
							HSSFCell cell = row.createCell(p);
							String data = ardata.get(p).toString();
							if (data.startsWith("=")) {
								cell.setCellType(Cell.CELL_TYPE_STRING);
								data = data.replaceAll("\"", "");
								data = data.replaceAll("=", "");
							} else if (data.startsWith("\"")) {
								data = data.replaceAll("\"", "");
								cell.setCellType(Cell.CELL_TYPE_STRING);
							} else {
								data = data.replaceAll("\"", "");
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							}
							if(soContemNumeros(data))
								cell.setCellValue(Double.parseDouble(data));
							else
								cell.setCellValue(data);
						}
					}
					
					fis.close();
				}
			}
			FileOutputStream fileOut = new FileOutputStream(path+(fileXLS.contains(".xls")?fileXLS:fileXLS+".xls"));
			hwb.write(fileOut);
			fileOut.close();
			System.out.println("Arquivo excel "+path+fileXLS+" gerado com sucesso.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Verifica se somente contém números 
	 * @param texto
	 * @return
	 */
	public static boolean soContemNumeros(String texto) {
		if (texto == null)
			return false;
		for (char letra : texto.toCharArray())
			if (letra < '0' || letra > '9')
				return false;
		return true;
	}
	/**
	 * Descompactador de arquivos
	 * @param source - Nome do arquivo de Origem
	 * @return
	 */
	public static File descompactarArquivos(String source) {
		try {
			GZIPInputStream gzipInputStream = null;
			gzipInputStream = new GZIPInputStream(new FileInputStream(source));
			String target = source.substring(0,source.lastIndexOf(".gz"))+".log";			
			OutputStream out = new FileOutputStream(target);
			byte[] buf = new byte[1024];
			int len;
			while ((len = gzipInputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			gzipInputStream.close();
			out.close();
			
			File file = new File(source);
			
			//Apaga o arquivo fonte
			if(file.exists())
				file.delete();

			//Se o target existir retorna ele
			file = new File(target);
			
			if(file.exists())
				return file;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
