
import java.util.*;
import java.io.*;

public class Question implements Serializable
{
	String question;
	ArrayList<String> answerSet;

	/**
	*constructor assigns question and answer set fields
	*We assume that the first answer in the list is the correct one
	*/
	public Question(String qu, ArrayList<String> ans)
	{
		this.question = qu;
		this.answerSet = ans;
	}

	public String getQuestion()
	{
		return this.question;
	}

	public ArrayList<String> getAnswerSet()
	{
		return this.answerSet;
	}
}