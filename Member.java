
public class Member
{
	public final int memberID;
	public final alias;
	public final emailAddress;
	private char[] password;

	public Member(int ID,String a, String email, char[] pword)
	{
		this.memberID = ID;
		this.alias = a;
		this.emailAddress = email;
		this.password = pword;
	}

	public boolean isPasswordCorrect(char[] testPW)
	{
		if (testPW.length()!= this.password.length())
		{
        		return false;
    		} else {
        		return Arrays.equals (testPW, this.password);
		}
	}

	public void resetPassword(char[] currentPW,char[] newPW)
	{
		if(isPasswordCorrect(currentPW))
		{
			this.password = newPW;
			System.out.println("Password changed successfully");
		}else{
			System.out.println("Unable to change password. Current password is incorrect. Please try again.");
		}
	}
}