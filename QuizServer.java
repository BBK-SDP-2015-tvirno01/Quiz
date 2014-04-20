
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
	*@throws FileNotFoundException if contacts.txt does not exist in the current directory. In this instance the assumption is that this is a new Contact Manager and that the new empty data structures are created
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
			this.memberIDgenerator = (AtomicInteger) imptM.readObject();
			this.memberList = (ArrayList<Member>) imptM.readObject();
			imptQ = new ObjectInputStream(new FileInputStream(".QuizList.txt"));
			this.quizIDgenerator = (AtomicInteger) imptQ.readObject();
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
				if(!imptM.equals(null))
				{
					imptM.close();
				}
				if(!imptQ.equals(null))
				{
					imptQ.close();
				}
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
			exptM.writeObject(this.memberIDgenerator);
			exptM.writeObject(this.memberList);


			saveFile = new FileOutputStream(".QuizList.txt");
			ObjectOutputStream exptQ = new ObjectOutputStream(saveFile);
			exptQ.writeObject(this.quizIDgenerator);
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

	/**
	*Create a new member object and add it to the list of members which persists in the Server
	*@throws RemoteException
	*@param String alias entered by user
	*@param String email address entered by user
	*@param char[] password choen by user
	*@return Returns the newly created members ID number as an integer
	*/
	public int createMember(String alias,String emailAdd, char[] pWord)  throws RemoteException
	{
		int memberID = this.memberIDgenerator.incrementAndGet();
		Member newMember = new Member(memberID,alias, emailAdd, pWord);
		this.memberList.add(newMember);
		return memberID;
	}

	/**
	*Method for verifying a users login credentials
	*@throws RemoteException
	*@param String email address
	*@param char[] password
	*@return Returns the member ID of the user if the member exists in the member list and the password is correct. Returns -1 if member credentials cannot be verified (either because member does not exist or password is incorrect).
	*/
	public int authenticateMember(String emailAdd,char[] pWord) throws RemoteException
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

		return -1;
	}

	/**
	*Method for creating a new instance of a Quiz object. Quiz is added to the set of stored quizzes with a vacant question list, to be populated by the quiz setup client
	*@throws RemoteException
	*@param String Quiz Name as chosen by the quiz set up client
	*@param int Member ID of the quiz Setup client
	*@return int The Quiz ID of th newly created quiz
	*/
	public int makeNewQuiz(String quizName, int memberID)  throws RemoteException	
	{
		int quizID = this.quizIDgenerator.incrementAndGet();
		ArrayList<Question> questionSet = null;
		Quiz newQuiz = new Quiz(quizID,quizName, memberID, questionSet);
		this.quizSet.add(newQuiz);
		return quizID;
	}

	/**
	*Method to return a copy of a Quiz object in the quiz set with the input quiz ID
	*@throws RemoteException
	*@return Quiz stored in quiz list with corresponding quiz ID
	*@param int Quiz ID of the quiz to be returned
	*/
	public Quiz getQuiz(int quizID) throws RemoteException
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

	/**
	*Method to ad a question to an existing quiz
	*@throws RemoteException
	*@param int Quiz ID of the quiz the question is to be added to
	*@param int Member ID of the quiz set up client
	*@param String Question text to be added
	*@param ArrayList List of possible answers in the form of Strings
	*/
	public void addQuestion(int quizID, int memberID, String question, ArrayList<String> answerList)  throws RemoteException
	{
		Quiz editQuiz = this.getQuiz(quizID);
		if(memberID==editQuiz.creatorID)
		{
			Question newQuestion = new Question(question, answerList);
			editQuiz.addQuestion(newQuestion);
		}
	}

	/**
	*Method to remove a quiz from the quiz set. Only the quiz set up client who created the quiz can close a quiz.
	*@throws RemoteException
	*@param int Quiz ID of the quiz to be closed
	*@param int Member ID of the quiz set up client to verify that they are the creator of the quiz to be closed
	*@return String If close is successful the alias of the member who achieved the highest score is returned along with their score. If close is unsuccessful a message detailing the reason is returned.
	*/
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

	/**
	*Method to finad the alias chosen by a member given their member ID
	*@throws RemoteException
	*@param int Member ID of the member alias being queried
	*@return String the alias chosen by the member
	*/
	public String getAlias(int memberID) throws RemoteException
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

	/**
	*Method to return the quiz ID of a given quiz name
	*@throws RemoteException
	*@param String Quiz name being queried
	*@return int Quiz ID of the quiz being queried
	*/
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

	/**
	*Method to return a random quiz ID
	*@throws RemoteException
	*@return int Quiz ID of a random quiz in the quiz set
	*/
	public int getRandomQuizID() throws RemoteException
	{
		int quizID;

		do
		{
			quizID = (int) Math.random()*quizIDgenerator.get();
		}while(!this.doesQuizExist(quizID));

		return quizID;
	}

	/**
	*Method to check if quiz exists in the quiz set
	*@param int Quiz ID of the quiz being checked
	*@return boolean TRUE if quiz exists in quiz set, FALSE if not
	*/
	public boolean doesQuizExist(int quizID) throws RemoteException
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

	/**
	*Method to perform a wildcard search for a particular quiz given a search keyword which the quiz names returned will contain
	*@throws RemoteException
	*@param String search keyword
	*@return ArrayList of quiz names containing the search keyword
	*/
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

	/**
	*Method to find the number of questions for a given quiz
	*@throws RemoteException
	*@param int Quiz ID of the quiz being queried
	*@return int the number of questions in the list of questions for that quiz
	*/
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

	/**
	*Method to find the name of a quiz with given quiz ID
	*@throws RemoteException
	*@throws IllegalArgumentException if the given quiz ID does not exist in the quiz set
	*@param int Quiz ID of the quiz being queried
	*@return String the name of the quiz being queried
	*/
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

	/**
	*Method to return the question text of a given question for a given quiz ID
	*@throws IllegalArgumentException if the quiz ID does not exist in the quiz set
	*@throws RemoteException
	*@param int Quiz ID of the quiz being queried
	*@param int the question number of the question being queried in the given quiz
	*@return String Question text of the queried question is returned if the question is found. Returns null otherwise
	*/
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

	/**
	*Method to return the set of possible answers for a given question in a given quiz
	*@throws RemoteException
	*@throws IllegalArgumentException if the queried quiz does not exist
	*@param int Quiz ID of the quiz being queried
	*@param int Question number of the question being queried
	*@return ArrayList of possible answers for the given question. Returns null if the quiz does not exist or if the question is not found	
	*/
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

	/**
	*Method to check the score of an answer for a given question of a given quiz
	*@throws RemoteException
	*@throws IllegalArgumentException if the given quiz ID does not exist int the quiz set
	*@param int Quiz ID of the quiz being queried
	*@param int question number of the question being queried
	*@param String submitted answer to be checked
	*@return int Returns 1 if the answer is correct and 0 if the answer submitted is incorrect
	*/
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

	/**
	*Method for submitting a player's score for a given quiz. The member ID and score are added to the leaderboard of the quiz in question 
	*@throws RemoteException
	*@throws IllegalArgumentException of the quiz ID given does not exist in the quiz set
	*@param int Quiz ID of the quiz for which the score will be submitted
	*@param int Member ID of the player for which score is being submitted
	*@param int Score achieved by the player for this quiz
	*/
	public void submitScore(int quizID, int memberID, int score) throws RemoteException
	{
		try
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
			}else{
				throw new IllegalArgumentException();
			}
		}catch(IllegalArgumentException ex){
			System.out.println("Quiz not found. Scores aill not be submitted for quizzes that are already closed");
		}
	}
}