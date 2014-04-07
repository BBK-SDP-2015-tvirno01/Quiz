
import java.util.*;
import java.io.*;
import java.text.*;
import java.util.concurrent.atomic.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

//houses data structures and implements methods to be called remotely by clients
public class QuizServer extends UnicastRemoteObject implements QuizService
{
	private HashSet<Quiz> quizSet;
	private ArrayList<Member> memberList;
	private AtomicInteger IDgenerator;

	public QuizServer() throws RemoteException
	{
		super();
		this.readFiles();

		Flusher hookWriter = new Flusher(this);
		Thread(hook) = new Thread(hookWriter);
		Runtime.getRuntime().addShutdownhook(hook);
	}

	/**
	*Reads serialized data structures from file
	*Member data stored in text file called Members.txt in current file directory
	*Quiz data stored in text file called QuizList.txt in current file directory
	*@throws FileNotFoundException if contacts.txt does not exist in the current directory. 
	*In this instance the assumption is that this is a new Contact Manager and that the new empty data structures are created
	*@throws ClassNotFoundException if the serialized class read from file is not of the expected type
	*@throws IOException
	*/
	private void readFiles()
	{
		ObjectInputStream imptM = null;
		ObjectInputStream imptQ = null;
		try
		{
			imptM = new ObjectInputStream(new FileInputStream(".Members.txt"));
			this.MemberList = (ArrayList<Member>) imptM.readObject();
			imptQ = new ObjectInputStream(new FileInputStream(".QuizList.txt"));
			this.meetingList = (HashSet<Meeting>) imptQ.readObject();
		}catch(FileNotFoundException ex){
			this.IDgenerator = new AtomicInteger();
			this.contactList = new HashSet<Contact>();
			this.meetingList = new HashSet<Meeting>();
		}catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try
			{
				imptM.close();
				imptQ.close();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}

	

	/**
	*Quiz server member fileds/data structures are all serializable and are flattened 
	*using ObjectOutputStream() to contacts.txt in the current file location
	*@throws IOexception 
	*/
	public void flush()
	{
		FileOutputStream saveFile = null;
		try
		{
			saveFile = new FileOutputStream(".Members.txt");
			ObjectOutputStream exptM = new ObjectOutputStream(saveFile);
			expt.writeObject(this.memberList);


			saveFile = new FileOutputStream(".QuizList.txt");
			ObjectOutputStream exptQ = new ObjectOutputStream(saveFile);
			expt.writeObject(this.quizSet);	
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try
			{
				saveFile.close();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}

}