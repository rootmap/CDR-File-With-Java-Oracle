//File Lib Start
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Scanner;
//File Lib End

//Email Lib Start
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
//Email Lib End

public class MailnAttachment {
	
	static Backlog log;
	static String logFileDirectory;
	static String attachmentDump;
	static String attachmentDumpRead="";
	static int developmentMode=0;
	static String destinationDir="DemoDestination";
	static String sourceDir="DemoSource";
	
    
    public MailnAttachment()
    {
    	Properties p=new Properties();
    	try { p.load(new FileInputStream("app.properties")); }
		catch(Exception e){ System.out.println("Properties failed to load due to : "+e.toString()); }
    	logFileDirectory=p.getProperty("logFileDirectory");
		log=new Backlog(logFileDirectory);
		attachmentDump=p.getProperty("attachmentDump");
		attachmentDumpRead=p.getProperty("attachmentDumpRead");
	    developmentMode =Integer.parseInt(p.getProperty("developmentMode"));
	    destinationDir =p.getProperty("destinationDir");
	    sourceDir =p.getProperty("sourceDir");
	    
    }
	
	public static ArrayList getSource()
	{
		ArrayList list = new ArrayList();
		try (Stream<Path> filePathStream=Files.walk(Paths.get(attachmentDump))) {
		    filePathStream.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            list.add(filePath);  
		            //System.out.println(filePath);
		        }
		    });
		}
		catch (IOException ioe) 
	    {
			if(developmentMode==0)
				System.out.println(ioe);
	    }
		System.out.println("Files Get :"+list);
		return list;
	}
	
	public static String getCorrectedStr(String str)
	{
		String strAr[] = str.split(" ");
		String retString="";
		if(strAr.length>0)
		{
			retString=strAr[strAr.length-1];
		}
		
		return retString;
	}
	public static String getDateParsed(String str)
	{
		String dateParse="";
		//Date myDate = new Date();
		try {
			String sDate1=str;  
		    Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(sDate1); 
			dateParse=new SimpleDateFormat("dd/MM/yyyy").format(date1);
		}
		catch (Exception e) {
			log.WriteInfo("Date Conversion Failed : "+e.getMessage().toString());
			dateParse="**/**/****";
		}
		
		return dateParse;
	}
	public static ArrayList getSourceFileContent(ArrayList list)
	{
		ArrayList listFileName = new ArrayList();
		int count=0;
		while (list.size() > count) {
      
			BufferedReader br = null;
		    String newFile=list.get(count)+"";
		    
		    
				    try{	
				    	   
				           br = new BufferedReader(new FileReader(newFile));		
				           String contentLine = br.readLine();
						   while (contentLine != null) {
							  listFileName.add(contentLine);
						      contentLine = br.readLine();				      
						   }
						   
						
						   
				    } 
				    catch (IOException ioe) 
				    {
				    	if(developmentMode==0)
				    	   ioe.printStackTrace();
				    } 
				    
			        finally 
				    {
						   try {
						      if (br != null)
						    	  br.close();
						   } 
						   catch (IOException ioe) 
					       {
							   if(developmentMode==0)
								   System.out.println("Error in closing the BufferedReader");
						   }
				    }
				   
			       System.out.println(listFileName);;
				   System.out.println("File Read Mode 1 " + newFile);
				   //System.exit(1);
				        
				        //System.out.println("File & Email Array " + listFileName);
			        
			       
		       		
		    		
		       
			       //File Move Start 
				   /*File fileM = new File(newFile);
		    	   String simpleFileNameDump = fileM.getName();
		    	   File srcFile = new File(newFile);
			       srcFile.renameTo(new File(attachmentDumpRead,simpleFileNameDump));
			       log.WriteReceiver(simpleFileNameDump+" Dump File Moved to New Read Directory.");
			       System.out.println(simpleFileNameDump+" Dump File Moved to New Read Directory.");
			       //File Move FInal Nofification */
			
			count++;
	    }
		
		//System.out.println(listFileName);
		log.WriteInfo("List of file dump : "+listFileName.toString());
		return listFileName;
	}
	
	public static ArrayList getSourceFileRead(File filename)
	{
		
			ArrayList listFileName = new ArrayList();
			BufferedReader br = null;
		    	    try{	
				    	   
				           br = new BufferedReader(new FileReader(filename));		
				           String contentLine = br.readLine();
						   while (contentLine != null) {
							  listFileName.add(contentLine);
						      contentLine = br.readLine();				      
						   }
						   
						
						   
				    } 
				    catch (IOException ioe) 
				    {
				    	if(developmentMode==0)
				    	   ioe.printStackTrace();
				    } 
				    
			        finally 
				    {
						   try {
						      if (br != null)
						    	  br.close();
						   } 
						   catch (IOException ioe) 
					       {
							   if(developmentMode==0)
								   System.out.println("Error in closing the BufferedReader");
						   }
				    }
				   
			       System.out.println(listFileName);;
				   System.out.println("File Read Mode 1 " + filename);
				   //
				   System.out.println("File & Email Array " + listFileName);
				   System.exit(1);
		return listFileName;
	}
	
	public static void SendMail(String contentLine)
	{
		
		String[] arrayRowLine = contentLine.split("\\,");
		//System.out.println(arrayRowLine.length);
		if(arrayRowLine.length>0)
		{
					
			
			
			System.out.println("Mail FI:"+arrayRowLine[0]);
			System.exit(1);
			
		}
		else
		{
			System.out.println("Invalid File Length : "+contentLine);
		}

		
	}
	
	

}
