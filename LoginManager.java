
import java.rmi.Remote;
import java.rmi.RemoteException;

public class LoginManager
{
	private QuizService qServer;

	public LoginManager(QuizService qServer)
	{
		this.qServer = qServer;
	}

	//returns playerID number
	private int login()
	{
		String strA = "";
		String strB = "";
		char[] pWord;
		Boolean memberVerified = false;
		int result = -1;

		System.out.println("Register or proceed to login");
		
		System.out.println("1. Register");

		System.out.println("2. Login");

		try
		{

		if(Integer.parseInt(System.console().readLine())==1)
		{
			
			System.out.println("Please enter your email address");
			strA = System.console().readLine();
			System.out.println("Please enter your password");
			pWord = System.console().readLine().toCharArray();
			System.out.println("Please enter your Quiz Master alias");
			strB = System.console().readLine();

			result = qServer.createMember(strB,strA,pWord);
			
		}

		while(!memberVerified)
		{
			System.out.println("Please enter your email address and password");
			strA = System.console().readLine();
			System.out.println("Password");
			strB = System.console().readLine();
			
			result = qServer.authenticateMember(strA,strB.toCharArray());

			memberVerified = result>=0;
		}
		
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
		
		return result;
	}
}