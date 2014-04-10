
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface QuizService extends Remote
{
	//create new member returns member ID
	public int createMember(String alias, String emailAdd,char[] pWord);

	//authenticate login credentials (return member ID if verified, 0 if not)
	public int authenticateMember(String emailAdd, char[] pWord);

	//creates new quiz
	public int makeNewQuiz(String quizName,int memberID);

	//adds question to a quiz
	public void addQuestion(int quizID,int playerID, String question, ArrayList<String> answerList);

	//returns the member details of the top score
	public String closeQuiz(int quizID,int memberID);

	//return quiz ID of a specified quiz
	public int getQuizID(String quizName);

	//return random quiz ID
	public int getRandomQuizID();

	//return list of quizzes containing search keyword
	public ArrayList<String> searchQuiz(String keyword);

	//return number of questions in quiz
	public int getNumberOfQuestions(int quizID);

	//return quiz name
	public String getQuizName(int quizID);

	//return question
	public String getQuestion(int quizID, int quNum);

	//return answer set
	public ArrayList<String> getAnswerSet(int quizID, int quNum);

	//return score for question 1 or 0
	public int checkAnswer(int quizID, int quNum, String answer);

	//add player score to the quiz leaderboard
	public void submitScore(int quizID, int playerID, int score);
	
	
}