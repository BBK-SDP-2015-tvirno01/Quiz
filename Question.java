
public class Question implements Serializable
{
	String question;
	ArrayList<String> answerSet;

	/**
	*constructor assigns question and answer set fields
	*We assume that the first answer in the list is the correct one
	*/
	public Question(String qu, Array<String> ans)
	{
		this.questions = qu;
		this.answerSet = ans;
	}
}