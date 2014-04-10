
public class Quiz
{
	public final int quizID;
	public final String quizName;
	public final int creatorID;
	public final ArrayList<Question> quSet;
	public final ArrayList<QuizScore> quLeaderBoard;
	

	public Quiz(int ID, String name,int creator, ArrayList<Question> qus)
	{
		this.quizID = ID;
		this.quizName = name;
		this.quSet = qus;
		this.creatorID = creator;	
	}

	public void addQuestion(Question qu)
	{
		this.quSet.add(qu);
	}

	public boolean removeQuestion(Question qu)
	{
		return this.quSet.remove(qu);
	}

	public void addScore(QuizScore newScore)
	{
		this.quLeaderBoard.add(newScore);
	}

	public ArrayList<QuizScore> getLeaderBoard()
	{
		ScoreComparator<QuizScore> cScore = new ScoreComparator<QuizScore>();
		Collections.sort(this.quLeaderBoard,cScore);
		return this.quLeaderBoard;
	}

	public QuizScore getTopScore()
	{
		ScoreComparator<QuizScore> cScore = new ScoreComparator<QuizScore>();
		Collections.sort(this.quLeaderBoard,cScore);
		return this.quLeaderBoard.get(1);
	}
}