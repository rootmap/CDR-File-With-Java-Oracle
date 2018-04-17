import java.io.IOException;
import java.sql.SQLException;
public class CDRProcess {

	public static void main(String[] args) throws IOException, SQLException  {
		ProcessorService ps=new ProcessorService();
		ps.ProcessSchduler();
		System.out.println("Process Started Loop......");
	}

}
