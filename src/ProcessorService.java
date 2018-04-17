import java.time.Instant;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;


public class ProcessorService {
	
	Backlog log;
	Timer logTimer;
	int ThreadInterval=100;
	int ThreadCount=5;
	String logFileDirectory;
	String attachmentDump;
	boolean DataDB=false;
	PreparedStatement ps;
	Statement stmnt;
	OracleConnection dbConnection;
	Connection con;
	String fileFetchQuery = "";
	String processedPath = "";
	
	public ProcessorService()
	{
		Properties p=new Properties();
		try { p.load(new FileInputStream("app.properties")); }
		catch(Exception e){ System.out.println("Properties failed to load due to : "+e.getMessage()); }
		
		logFileDirectory=p.getProperty("logFileDirectory");
		log=new Backlog(logFileDirectory);
		attachmentDump=p.getProperty("attachmentDump");
		ThreadInterval=Integer.parseInt(p.getProperty("ThreadInterval"));
		ThreadCount=Integer.parseInt(p.getProperty("ThreadCount"));

		this.dbConnection = new OracleConnection(log,p);
		if(dbConnection.connect()) {
			log.WriteInfo("Database connection success.");
			System.out.println("Database connection success.");
	    	stmnt = dbConnection.getSelectStatement();
	    	con=dbConnection.getConnection();
	    	System.out.println("connected");
	    	
	    	Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){
					log.WriteInfo("In ShutdownHook");					
					dbConnection.disconnect();
				}
			});
			
			
		}
		else {
			log.WriteError("Database connection failed!");
			System.out.println("Database connection Failed.");
		}
		
		
	}
	
	public void ProcessSchduler()
	{
		log.WriteInfo("Starting Process Schduler.");
		
		logTimer =new Timer();
		
		logTimer.scheduleAtFixedRate(new java.util.TimerTask() {
			
			@Override
			public void run() {
				
				try {
					//System.out.println("Schdule Running 1");
					log.WriteInfo("Timer Scheduler Start Running.");
					ExcuteProcess();
					log.WriteInfo("Timer Scheduler Complete Running.");
					//log.MoveErrorFIleEmail(attachmentDump);
				}
				catch (Exception e) {
					//System.out.println("Schdule Running Failed : "+e.getMessage());
					log.WriteError("Schdule Running Failed : "+e.getMessage().toString());
				}
				
			}
			
		}, 0, ThreadInterval);
	}
	
	/*private Runnable ProcessEmailNDAttachment(String sourceFileName)
	{
		return new Runnable()
		{

			@Override
			public void run() {
				log.WriteInfo("Mail Sending Starting "+sourceFileName);
				MailnAttachment mna=new MailnAttachment();
				mna.SendMail(sourceFileName);
				log.WriteInfo("Mail Sending Complete "+sourceFileName);
			}
			
		};
	}*/
	
	public synchronized void ExcuteProcess()
	{
		try
		{
			
			MailnAttachment mna=new MailnAttachment();
			
			ArrayList sourceFiles;
			sourceFiles=mna.getSource();
			
			if(sourceFiles.size()>0)
			{
					BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100, true);
					ThreadPoolExecutor executor = new ThreadPoolExecutor(
							ThreadCount, // core size
							ThreadCount+5, // max size
							1, // keep alive time
							TimeUnit.MINUTES, // keep alive time units
							queue // the queue to use
					);
										
					int fileQue=0;
					while(sourceFiles.size() > fileQue)
					{
						log.WriteInfo("Mail Looping Queue Starting : "+fileQue);
						String sourceFileName = sourceFiles.get(fileQue)+"";
						File ProcessFile=new File(sourceFileName);
						System.out.println("Sending File In Thread.");
						executor.execute(processFile(ProcessFile,ProcessFile.getName()));
						fileQue++;
					}
					
					try
					{
						executor.shutdown();
						try {
							executor.awaitTermination(5, TimeUnit.MINUTES);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					catch(Exception ex)
					{
						log.WriteError("Error while preparing & processing: " + ex.getMessage() + "|" + ex.getStackTrace()[0]); 
					}
					
			}
			else
			{
				//log.WriteInfo("No Dump File Found.");
				System.out.println("No Dump File Found.");
			}
			
			
		}
		catch(Exception e)
		{
			log.WriteInfo("Excution Failed TO Process.");
			System.out.println("Excution Failed TO Process.");
		}
	}
	
	public String[] arrayStringPush(String item, String[] oldArray) {
	    int len = oldArray.length;
	    String[] newArray = new String[len+1];
	    System.arraycopy(oldArray, 0, newArray, 0, len);
	    newArray[len] = item;

	    return newArray;
	}
	
	public Runnable processFile(final File file, final String FileID) throws IOException, SQLException
	{
		
		
		//System.out.println("Executing Start Successfully");
		
		return new Runnable(){
			public void run(){
		  		try
		  		{	
		  			
		  				
					  		Instant now = Instant.now();
					  		long millis = now.toEpochMilli();
					  		String IdentifierSt=millis+"";
					  		
				  			System.out.println(FileID+" File In Run");
							//System.exit(1);
		  					String InputFileStatus="INSERT INTO BMS_CDR_FILE (FILE_NAME,FILE_IDENTIFIER,FILE_STATUS) VALUES ('"+FileID+"','"+IdentifierSt+"','0')";
		  					String InputFileStatusUpdate="UPDATE BMS_CDR_FILE SET FILE_STATUS='1' WHERE FILE_IDENTIFIER='"+IdentifierSt+"'";
		  			    	String statusUpdate ="UPDATE BMS_CDR_FILE\r\n" + 
		  					     		" set FILE_STATUS=2 where ID=";
		  			    	String statusSucessUpdate ="UPDATE BMS_CDR_FILE set FILE_STATUS=1 where ID=";
		  			    	String statusFailUpdate ="UPDATE BMS_CDR_FILE set FILE_STATUS=3 where ID=";
		  			    
		  			    	
//		  			    	String selectFileIdSQL = "select ID from DND_NUMBER_FILE where FILE_NAME='"+file.getName().toString()+"'";
//		  			    	ResultSet rs=stmnt.executeQuery(selectFileIdSQL);
		  			    	String fileId="1";
//		  			    	while(rs.next())  
//		  			    		fileId=rs.getString(1);
//		  			    	arrayStringPush(fileId,fileIds);
		  			    	//statusUpdate+=FileID;
		  			    	//statusSucessUpdate+=FileID;
		  			    	//statusFailUpdate+=FileID;
		  			    	
		  			    	//stmnt.executeUpdate(statusUpdate);
		  			    	
		  			    	//log.WriteInfo("File processing Start: "+fileId+"|"+file.toPath());
		  			    	stmnt.executeUpdate(InputFileStatus);
		  			    	System.out.println("Initialting file : ");
		  			    	//System.exit(1);
		  			    	
		  			    	String insertTableSQL = "INSERT INTO BMS_CDR_RECORD (CRD,CRD_IDENTIFIER) values(?,?)";
		  					
		  					ps =con.prepareStatement(insertTableSQL);  							
		  					BufferedReader br = null;
				    	    try
				    	    {
				    	    		
				    	       final int batchSize = 1000;
			  			       int count = 0;
					           br = new BufferedReader(new FileReader(file));		
					           String contentLine = br.readLine();
							   while (contentLine != null) {
								    //System.out.println(contentLine);
								    String ContentText=contentLine;
								  	if(contentLine.length()==10)
		  							{
								  		ContentText="880"+contentLine;
		  							}
								  	else if(contentLine.length()==11)
								  	{
								  		ContentText="88"+contentLine;	
								  	}
								  	else if(contentLine.length()==13)
								  	{
								  		ContentText=contentLine;	
								  	}
								    System.out.println(ContentText);
								  	ps.setString(2, IdentifierSt);		        	 
		  							ps.setString(1, ContentText);
		  							ps.addBatch();
		  							if(++count % batchSize == 0) {
		  									ps.executeBatch();
		  							}
								  	
							      contentLine = br.readLine();				      
							   }   
							   
							   ps.executeBatch();
							   stmnt.executeUpdate(InputFileStatusUpdate);
							 //System.exit(1);
						    } 
						    catch (IOException ioe) 
						    {
						    	ioe.printStackTrace();
						    } 
						    
					        finally 
						    {
							   try 
							   {
							      if (br != null)
							    	  br.close();
							   } 
							   catch (IOException ioe) 
						       {
									System.out.println("Error in closing the BufferedReader");
							   }
						    }
				    	    //ps.executeBatch();
		  					//System.exit(1);
		  					
		  			        
		  			
		  	
		  		}
		  		catch(Exception ex)
		  		{
		  			log.WriteError("Exception : " + ex.getMessage() );
		  			
		  		}		  		
		  	}
	};
		
	     		
	}
	
	
}
