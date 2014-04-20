
import java.util.*;
import java.io.*;
import java.text.*;
import java.util.concurrent.atomic.*;
import java.rmi.server.*;
import java.rmi.*;

//houses data structures and implements methods to be called remotely by clients
public class QuizServer extends UnicastRemoteObject implements QuizService, Flushable
{
	private HashSet<Quiz> quizSet;
	private ArrayList<Member> memberList;
	private AtomicInteger quizIDgenerator;
	private AtomicInteger memberIDgenerator;

	public QuizServer() throws RemoteException
	{
		super();
		this.readFiles();

		Flusher hookWriter = new Flusher(this);
		Thread hook = new Thread(hookWriter);
		Runtime.getRuntime().addShutdownHook(hook);
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
			this.memberList = (ArrayList<Member>) imptM.readObject();
			imptQ = new ObjectInputStream(new FileInputStream(".QuizList.txt"));
			this.quizSet = (HashSet<Quiz>) imptQ.readObject();
		}catch(FileNotFoundException ex){
			this.quizIDgenerator = new AtomicInteger();
			this.memberIDgenerator = new AtomicInteger();
			this.quizSet = new HashSet<Quiz>();
			this.memberList = new ArrayList<Member>();
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
	*using ObjectOutputStream() to Members.txt & QuizList.txt in the current file location
	*@throws IOexception 
	*/
	public void flush()
	{
		FileOutputStream saveFile = null;
		try
		{
			saveFile = new FileOutputStream(".Members.txt");
			ObjectOutputStream exptM = new ObjectOutputStream(saveFile);
			exptM.writeObject(this.memberList);


			saveFile = new FileOutputStream(".QuizList.txt");
			ObjectOutputStream exptQ = new ObjectOutputStream(saveFile);
			exptQ.writeObject(this.quizSet);	
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
	public int createMember(String alias,String emailAdd, char[] pWord)  throws RemoteException
	{
		int memberID = this.memberIDgenerator.incrementAndGet();
		Member newMember = new Member(memberID,alias, emailAdd, pWord);
		this.memberList.add(newMember);
		return memberID;
	}
	public int authenticateMember(String emailAdd,char[] pWord)
	{
		for(Member m:this.memberList)
		{
			if(m.emailAddress.equals(emailAdd))
			{
				if(m.isPasswordCorrect(pWord))
				{
					return m.getMemberID();
				}
			}
		}

		return 0;
	}
	public int makeNewQuiz(String quizName, int memberID)  throws RemoteException	
	{
		int quizID = this.quizIDgenerator.incrementAndGet();
		ArrayList<Question> questionSet = null;
		Quiz newQuiz = new Quiz(quizID,quizName, memberID, questionSet);
		this.quizSet.add(newQuiz);
		return quizID;
	}
	public Quiz getQuiz(int quizID)
	{
		Quiz result = null;
		try
		{

			if(!doesQuizExist(quizID))
			{
				throw new IllegalArgumentException();
			}

			for(Quiz q:this.quizSet)
			{
			if(q.quizID==quizID)
				{
					result = q;
					return result;
				}
			}
		}catch(IllegalArgumentException ex){
			System.out.println("Quiz not found");
		}
		return result;
	}
	public void addQuestion(int quizID, int memberID, String question, ArrayList<String> answerList)  throws RemoteException
	{
		Quiz editQuiz = this.getQuiz(quizID);
		if(memberID==editQuiz.creatorID)
		{
			Question newQuestion = new Question(question, answerList);
			editQuiz.addQuestion(newQuestion);
		}
	}
	public String closeQuiz(int quizID, int memberID) throws RemoteException
	{
		String result = "";
		Quiz editQuiz = this.getQuiz(quizID);
		if(memberID==editQuiz.creatorID)
		{
			QuizScore topScore = editQuiz.getTopScore();
			if(!this.quizSet.remove(editQuiz))
			{
				result = "Quiz ID "+quizID+" not found";
			}else{
				result = this.getAlias(topScore.memberID)+" finished top of the quiz "+editQuiz.quizName+" scoring "+topScore.score+" out of "+this.getNumberOfQuestions(quizID);
			}
		}else{
			result = "You are not the owner of this quiz";
		}

		return result;
	}
	public String getAlias(int memberID)
	{
		for(Member m : this.memberList)
		{
			if(memberID==m.memberID)
			{
				return m.alias;
			}
		}

		return "Cannot find member ID "+memberID;
	}
	public int getQuizID(String quizName) throws RemoteException
	{
		try
		{

			for(Quiz q: this.quizSet)
			{
				if(quizName.equals(q.quizName))
				{
					return q.quizID;
				}
			}
			throw new IllegalArgumentException();

		}catch(IllegalArgumentException ex){
			System.out.println("Quiz name not found");
			return -1;
		}
		
	}
	public int getRandomQuizID() throws RemoteException
	{
		int quizID;

		do
		{
			quizID = (int) Math.random()*quizIDgenerator.get();
		}while(!this.doesQuizExist(quizID));

		return quizID;
	}
	private boolean doesQuizExist(int quizID)
	{
		for(Quiz q: this.quizSet)
		{
			if(quizID==q.quizID)
			{
				return true;
			}
		}

		return false;
	}
	public ArrayList<String> searchQuiz(String keyword) throws RemoteException
	{
		ArrayList result = new ArrayList<String>();

		for(Quiz q: quizSet)
		{
			if(q.quizName.contains(keyword))
			{
				result.add(q.quizName);
			}
		}

		return result;
	}
	public int getNumberOfQuestions(int quizID) throws RemoteException
	{
		try
		{
			if(doesQuizExist(quizID))
			{
				return getQuiz(quizID).quSet.size();
			}else{
				throw new IllegalArgumentException();
			}
		}catch(IllegalArgumentException ex){
			System.out.println("Quiz not found");
			return -1;
		}
	}
	public String getQuizName(int quizID) throws RemoteException
	{
		String result = null;
		try
		{
			if(doesQuizExist(quizID))
			{
				for(Quiz q:this.quizSet)
				{
					if(q.quizID==quizID)
					{
						result = q.quizName;
						return result;
					}
				}
			}else{
				throw new IllegalArgumentException();
			}
		}catch(IllegalArgumentException ex){
			System.out.println("Quiz not found");
		}
		return result;
	}
	public String getQuestion(int quizID, int quNum) throws RemoteException
	{
		String result = null;
		try
		{
			if(doesQuizExist(quizID))
			{
				for(Quiz q:this.quizSet)
				{
					if(quizID==q.quizID)
					{
						result = q.quSet.get(quNum).question; 
						return result;
					}
				}
			}else{
					throw new IllegalArgumentException();
			}
		}catch(IllegalArgumentException ex){
			System.out.println("Quiz not found");
		}
		return result;
	}
	public ArrayList<String> getAnswerSet(int quizID,int quNum) throws RemoteException
	{
		ArrayList<String> result = null;
		try
		{
			if(doesQuizExist(quizID))
			{
				for(Quiz q:this.quizSet)
				{
					if(quizID==q.quizID)
					{
						result = q.quSet.get(quNum).answerSet;
						return result; 
					}
				}
			}else{
					throw new IllegalArgumentException();
			}
		}catch(IllegalArgumentException ex){
			System.out.println("Quiz not found");
		}
		return result;
	}
	public int checkAnswer(int quizID, int quNum, String answer) throws RemoteException
	{
		int result = -1;
		try
		{
			if(doesQuizExist(quizID))
			{
				for(Quiz q:this.quizSet)
				{
					if(quizID==q.quizID)
					{
						if(answer.equals(q.quSet.get(quNum).answerSet.get(0)))
						{	
							result = 1;
						}else{
							result = 0;
						}
					}
				}
			}else{
					throw new IllegalArgumentException();
			}
		}catch(IllegalArgumentException ex){
			System.out.println("Quiz not found");
		}
		return result;
	}
	public void submitScore(int quizID, int memberID, int score) throws RemoteException
	{
		QuizScore newScore = new QuizScore(memberID,score);
		if(doesQuizExist(quizID))
		{
			for(Quiz q:this.quizSet)
			{
				if(quizID==q.quizID)
				{
					q.addScore(newScore);
				}
			}
		}
	}
}