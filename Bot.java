import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Bot {

    static boolean keepFiles = false;

    public static void main(String[] args) {
        final String PREDNOSTNA_URL = "https://www.sdl.si/prednostna-lista/prednostna-lista-16-9-2025/";
        final String VSELITEV_URL = "https://www.sdl.si/aktualno/napoteni-na-vselitev/";
        final String PREDNOSTNA_NAME = "prednostna", VSELITEV_NAME = "vselitev";
        int vpisna_st = 0;
        

        if(args.length == 0)
        {
            System.out.println("Vnesi vpisno st:");
            try
            {
                Scanner in = new Scanner(System.in);
                String data = in.nextLine();
                vpisna_st = Integer.parseInt(data.split(" ")[0]);
                if(data.split(" ").length > 1)
                {
                    if(data.split(" ")[1].toLowerCase().equals("true"))
                        keepFiles = true;
                }
                in.close();

            }catch(Exception e)
            {
                e.printStackTrace();
                System.exit(1);
            }

        }else
        {
            try {
                vpisna_st = Integer.parseInt(args[0]);
                if(args.length > 1)
                {
                    if(args[1].equals("true"))
                        keepFiles = true;
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
                  

        String prednostnaListaData = "";
        String vselitevData = "";

        String prednostnaFileURL = getXlsxFileURL(PREDNOSTNA_URL);

        CreateFileFromURL(prednostnaFileURL,PREDNOSTNA_NAME);
        prednostnaListaData = GetDataFromXlsx(new File(PREDNOSTNA_NAME+".xlsx"),vpisna_st);
        
        vselitevData = getPdfFileURL(VSELITEV_URL);
        CreateFileFromURL(vselitevData, "Vselitev");
        vselitevData = GetDataFromPdf(new File(VSELITEV_NAME+".pdf"), vpisna_st);

        System.out.println();
        System.out.println("Prednostna lista za vpisST: "+vpisna_st);
        System.out.println(prednostnaListaData);
        System.out.println("Podatki za vselitev za: "+vpisna_st);
        System.out.println(vselitevData);

    }

    public static String getXlsxFileURL(String inURL)
    {
        String url = "";
        String htmlCode = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new URL(inURL).openStream()));
            while(br.ready())
            {
                htmlCode = br.readLine().toString();
                if(htmlCode.contains("<strong>Prednostne liste:</strong>"))
                {
                    String[] pTagArray = htmlCode.split("<p>");
                    for(String s : pTagArray)
                        if(s.contains("ki</a></p>"))
                            url = s.split(">")[0].split("\"")[1];
                    break;
                }
            }
            br.close();

        } catch (Exception e) {
            // TODO: handle exception
        }

        return inURL.substring(0,inURL.indexOf(".si")+3) + url;
    }

    public static String getPdfFileURL(String inURL)
    {
        String url = "";
        String htmlCode = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new URL(inURL).openStream()));
            while(br.ready())
            {
                htmlCode = br.readLine().toString();
                if(htmlCode.contains("Napoteni na vselitev v obdobju med"))
                {
                    url = htmlCode.split("Napoteni na vselitev")[5].split("href=\"")[1].split("\"")[0];
                }
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return inURL.substring(0,inURL.indexOf(".si")+3) + url;
    }

    public static void CreateFileFromURL(String URL, String filename)
    {
        try {
            ReadableByteChannel ReadableByteChannel = Channels.newChannel(new URL(URL).openStream());
            String prefix = "."+URL.split("[.]")[URL.split("[.]").length - 1];
            FileOutputStream fos = new FileOutputStream(filename+prefix);
            FileChannel fc = fos.getChannel();
            fc.transferFrom(ReadableByteChannel, 0, Long.MAX_VALUE);
            fos.close();
        } catch (Exception e) {
            System.err.println("Something went wrong while trying to fetch data!\n"+e.getMessage());
        }
    }

    public static String GetDataFromXlsx(File f, int vpisna)
    {
        try {
            FileInputStream fis = new FileInputStream(f);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            String data;

            for (Row row : sheet) {
                data = "";
                // Loop through cells
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING:
                            data += (cell.getStringCellValue() + "\t");
                            break;
                        case NUMERIC:
                            data += (cell.getNumericCellValue() + "\t");
                            break;
                        case BOOLEAN:
                            data += (cell.getBooleanCellValue() + "\t");
                            break;
                        default:
                            data += ("?\t");
                    }
                }
                

                if(!(data.split("\t")[1].contains("?") || data.split("\t")[1].toLowerCase().contains("š")) && (int)Float.parseFloat(data.split("\t")[1]) == vpisna)
                {
                    if(!keepFiles)
                    {
                        fis.close();
                        f.delete();
                    }
                    return data;
                }
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
            if(!keepFiles)
                f.delete();
        return "No data found";
    }

    public static String GetDataFromPdf(File f, int vpisna)
    {
        try {
            PDDocument document = PDDocument.load(f);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            String[] splitStrings = text.split("\n");
            for(String s : splitStrings)
            {
                try{
                    int vst = Integer.parseInt(s.split(" ")[1]);
                    if(vst == vpisna)
                    {
                        if(!keepFiles)
                        {
                            document.close();   
                            f.delete();
                        }
                        return s;
                    }
                }catch(Exception e){}
            }

            document.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        if(!keepFiles)
            f.delete();
        return "No data found for "+vpisna;
        //return "No data found for: "+vpisna+"!";
    }

}
