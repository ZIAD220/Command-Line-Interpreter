import java.util.*;

public class Parser {
    String commandName;

    //Hashmap contains the name of commands.
    public  static HashMap<String, Integer> commands = new HashMap<String, Integer>();

    String[] args;
    // Constructor.
		/*
		public Parser()
		{
			args = new String[100];
			for (int i = 0; i < 100; i++)
				args[i] = "";

		}
		*/

    public boolean parse(String input)
    {
        ArrayList<String> newArgs = new ArrayList<>();
        StringBuilder str = new StringBuilder(input);

        int estQ = str.indexOf("\""),
                secQ = str.indexOf("\"", estQ+1);

        if((estQ != -1 && secQ != -1))
        {
            // eliminate white space in Dirs' names
            while(str.substring(estQ, secQ).contains(" ") )
            {
                str.replace(str.indexOf(" ", estQ), str.indexOf(" ", estQ)+1, "$");
                while(str.substring(estQ, secQ).contains(" ") )
                {
                    str.replace(str.indexOf(" ", estQ), str.indexOf(" ", estQ)+1, "$");
                }

                estQ = str.indexOf("\"", secQ+1);
                secQ = str.indexOf("\"", estQ+1);

                if((estQ == -1 || secQ == -1))
                {
                    break;
                }
            }
        }

        for(String s: str.toString().split(" "))
        {
            if(s.contains("\\"))
            {

                // replace all $ with white space
                s = s.replace("$", " ").replace("\"", "");

                newArgs.add(s.trim());

            }
            // case 2: arg has a space in folder name
            else if(s.contains("\""))
            {
                String ss = s;
                ss = ss.replace("\"", "").replace("$", " ");

                newArgs.add(ss.trim());

            }
            else
            {
                newArgs.add(s.trim());
            }
        }

        
        
        args = new String[newArgs.size()-1];
        String words[] = new String[newArgs.size()-1];
        words = newArgs.toArray(words);

        if (commands.containsKey(words[0]) == false){
            return false;
        }

        commandName = words[0];
        int idx = 0;
        for (int i = 1; i < words.length; i++)
        {
            args[idx] = words[i];
            idx++;
        }
        return true;
    }

    public String getCommandName()
    {
        return commandName;
    }

    public String[] getArgs()
    {
        return args;
    }
}