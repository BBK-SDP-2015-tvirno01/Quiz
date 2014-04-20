
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface QuizService extends Remote
{
	//create new member returns member ID
	public int createMember(String alias, String emailAdd,char[] pWord) throws RemoteException;

	//authenticate login credentials (return member ID if verified, 0 if not)
	public int authenticateMember(String emailAdd, char[] pWord) throws RemoteException;

	//creates new quiz
	public int makeNewQuiz(String quizName,int memberID) throws RemoteException;

	//adds question to a quiz
	public void addQuestion(int quizID,int playerID, String question, ArrayList<String> answerList) throws RemoteException;

	//returns the member details of the top score
	public String closeQuiz(int quizID,int memberID) throws RemoteException;

	//return quiz ID of a specified quiz
	public int getQuizID(String quizName) throws RemoteException;

	//return random quiz ID
	public int getRandomQuizID() throws RemoteException;

	//return list of quizzes containing search keyword
	public ArrayList<String> searchQuiz(String keyword) throws RemoteException;

	//return number of questions in quiz
	public int getNumberOfQuestions(int quizID) throws RemoteException;

	//return quiz name
	public String getQuizName(int quizID) throws RemoteException;

	//return question
	public String getQuestion(int quizID, int quNum) throws RemoteException;

	//return answer set
	public ArrayList<String> getAnswerSet(int quizID, int quNum) throws RemoteException;

	//return score for question 1 or 0
	public int checkAnswer(int quizID, int quNum, String answer) throws RemoteException;

	//add player score to the quiz leaderboard
	public void submitScore(int quizID, int playerID, int score) throws RemoteException;
	
	//returns the player Alias for the ID given
	public String getAlias(int playerID) throws RemoteException;
	
}