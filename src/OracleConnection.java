
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;


public class OracleConnection {
	private Backlog logger;
	private Connection connection;
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	private Statement selectStatement;
	private Statement preparedStatement;
	public Statement getPreparedStatement() {
		return preparedStatement;
	}

	public void setPreparedStatement(Statement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}

	CallableStatement fetchStatement;
	public Statement getSelectStatement() {
		return selectStatement;
	}

	public void setSelectStatement(Statement selectStatement) {
		this.selectStatement = selectStatement;
	}

	private String connectionUrl;
	
	private Properties properties;
	private String dbType="";
	
	
	ArrayList<String> batchlist =  new ArrayList<String>();

	public OracleConnection(Backlog ml, Properties p){
		try
		{
			
			logger = ml;
			properties = p;
			dbType=p.getProperty("dbType");
			if(dbType.equalsIgnoreCase("Oracle")){
				Class.forName("oracle.jdbc.OracleDriver").newInstance();
		        this.connection = DriverManager.getConnection("jdbc:oracle:thin:@" + p.getProperty("database.hostName") + ":" + p.getProperty("database.port") + "/" + p.getProperty("database.systemId"), p.getProperty("database.dbname"), p.getProperty("database.password"));
		        
			}else{
				connectionUrl = "jdbc:sqlserver://" + p.getProperty("dbserver", "localhost") + ";";
				connectionUrl += "user=" + p.getProperty("dbuser", "vasuser") + ";";
				connectionUrl += "password=" + p.getProperty("dbpassword", "vaspwd")+ ";";
				connectionUrl += "DatabaseName=" + p.getProperty("database", "vas");
				
			}
			
			
		}
		catch(Exception ex)
		{			
			System.out.println("DB initialize problem: " + ex.getMessage());
			logger.WriteError("DB initialize problem: " + ex.getMessage());
		}
		
		
	}
	
	public boolean connect()
	{
		System.out.println("Trying to connect with database");
		try
		{
			if(dbType.equalsIgnoreCase("Oracle")==false){
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				connection= DriverManager.getConnection(connectionUrl);
				
			}
			if(dbType.equalsIgnoreCase("Oracle")){
				
				Class.forName("oracle.jdbc.OracleDriver").newInstance();
		        this.connection = DriverManager.getConnection("jdbc:oracle:thin:@" + this.properties.getProperty("database.hostName") + ":" + this.properties.getProperty("database.port") + "/" + this.properties.getProperty("database.systemId"), this.properties.getProperty("database.dbname"), this.properties.getProperty("database.password"));
		        
			}
			
			selectStatement = connection.createStatement();
			
			return true;
		}
		catch(Exception ex)
		{			
			logger.WriteInfo("connect: exception " + ex.getMessage());
			//logger.writeError("DB Connection problem: " + ex.getMessage());
			//logger.writeErrorForCheckConnection("Exception in connect: " + ex.getMessage());
			return false;
		}
	}
	
	public boolean checkConnection()
	{
		try
		{
			if(connection.isClosed())
			{
				disconnect();
				boolean connectiondone = connect();
				if(connectiondone)
				{
					logger.WriteInfo("checkConnection: reconnection done");
					logger.WriteError("Reconnection done.");
				}
				else
				{
					logger.WriteError("Reconnection failed");
				}
				return 	connectiondone;			
			}
			else
			{
				return true;
			}
		}
		
		
		catch(Exception ex)
		{
			logger.WriteError("checkConnection: exception" + ex.getMessage());
			logger.WriteError("Exception in checkConnection: " + ex.getMessage());
			return false;
		}		
	}
	
	public void disconnect()
	{
		try
		{
			selectStatement.close();
			connection.close();
		}
		catch(Exception ex)
		{			
			logger.WriteError("DB disconnection problem: " + ex.getMessage());
		}
	}

}
