
public class Quiz
{
	public final String quName;
	public final HashSet<Question> quSet;
	public final ArrayList<QuizScore> quLeaderBoard;
	

	public Quiz(String name, HashSet qus)
	{
		this.quName = name;
		this.quSet = qus;	
	}

	public void addQuestion(Question qu)
	{
		this.quSet.add(qu);
	}

	public boolean removeQuestion(Question qu)
	{
		return this.quSet.remove(qu);
	}

	public Score getTopScore()
	{
		ScoreComparator<Score> cScore = new ScoreComparator<Score>();
		Collections.sort(this.quLeaderBoard,cScore);
		return this.quLeaderBoard.get(1);
	}
}