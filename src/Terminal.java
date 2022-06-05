import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class Terminal {

    Parser parser;

    // Parameterized Constructor.
    public Terminal(Parser newParser)
    {
        parser = newParser;
    }

    public void echo()
    {
        System.out.println(parser.getArgs()[0]);
    }

    public String pwd()
    {
        return System.getProperty("user.dir");
    }

    public void cd()
    {
        String directory = System.getProperty("user.dir");

        // If there are no arguments, make the current path equals home directory.
        if (parser.getArgs().length == 0)
        {
            String home = System.getProperty("user.home") + "\\Desktop";
            System.setProperty("user.dir", home);
        }

        // If the argument is "..", go back.
        else if (parser.getArgs()[0].equals(".."))
        {
            int lastSlash = directory.lastIndexOf('\\');

            String previous = directory.substring(0, lastSlash);

            System.setProperty("user.dir", previous);
        }
        else
        {
            String newDirectory = "";

            if (parser.getArgs()[0].length() >= 3) {
                // Checking if they are in the same drive.
                String drive = parser.getArgs()[0].substring(0, 3);

                if (directory.substring(0, 3).equals(drive)) {
                    newDirectory = parser.getArgs()[0];
                }

                else {
                    newDirectory = directory + "\\" + parser.getArgs()[0];
                }
            }
            else
            {
                newDirectory = directory + "\\" + parser.getArgs()[0];
            }
            File file = new File(newDirectory);
            // Checking if the path is actually correct.
            if (file.exists()) {
                System.setProperty("user.dir", newDirectory);
            }
        }
    }

    public void ls()
    {
        String current = System.getProperty("user.dir");

        File directory = new File(current);

        String[] content = directory.list();

        // Sorting alphabetically.
        Arrays.sort(content);

        System.out.println(Arrays.toString(content));
    }



    public void lsReverse()
    {
        File currFile = new File(pwd());
        String[] filesList = currFile.list();
        Collections.reverse(Arrays.asList(filesList));

        System.out.println(Arrays.toString(filesList));

    }

    public void mkdir(String[] args)
    {

        for(String s: args)
        {
            if(new File(s).exists())
            {
                System.out.println("Dir already exists");
                continue;
            }
            // case 1: arg is path
            if(s.contains("\\"))
            {

                File f = new File(s);

                // check if this path exists
                if(f.getParentFile().exists())
                {
                    String newDirName = f.getName();
                    String parentPath = f.getParentFile().getPath();
                    File newDir = new File(parentPath + "\\" + newDirName);
                    newDir.mkdir();
                }
                else
                {
                    System.out.println("parent path doesn't exist");
                }

            }
            else
            {

                // case 3: normal case make folder normally

                File newDir = new File(pwd() + "\\" + s);
                newDir.mkdir();

            }
        }
    }

    public void rmdir(String[] s)
    {
        for(String args: s)
        {
            if(args.charAt(0) == '*')
            {
                File currDir = new File(pwd());
                File[] dirs = currDir.listFiles();

                for(File f: dirs)
                {
                    if(f.isDirectory())
                    {
                        if(f.delete())
                        {
                        	System.out.println("hug");
                        }

                    }
                }
            }
            else
            {
                File currDir = new File(".\\" + args);

                if(currDir.isDirectory())
                {
                    if(!currDir.delete())
                    {
                        System.out.println("dir isn't empty");
                    }

                }
                else {
                	System.out.println("Folder not found.");
                }
            }
        }
    }

    public void touch(String[] s)
    {
    	
        for(String path: s)
        {
            File f = new File(pwd() + "\\" + path);

            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void cp(String[] args) throws IOException {
        
    	System.out.println(args[0]);
    	System.out.println(args[1]);
    	
    	File source = new File(args[0]);
        File destination = new File(args[1]);

        if (!source.exists()) {
            System.out.println("cp: cannot stat '" + source.getName() + "': No such file or directory");
            return;
        }
        if (!destination.exists())
            destination.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        FileWriter fileWriter = new FileWriter(destination);
        String line;
        while ((line = br.readLine()) != null)
            fileWriter.write(line + "\n");
        fileWriter.close();
    }

    public void cp_r(String[] args) throws IOException {

        File sourceFile = new File(args[1]);
        if (!sourceFile.isDirectory()) {
            System.out.println("bash: cd: -r: invalid option");
            return;
        }

        Path source = Paths.get(args[1]);
        Path destination = Paths.get(args[2]);

        Stream<Path> files = Files.walk(source);

        files.forEach(file -> {
            try {
                Files.copy(file, destination.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        files.close();
    }

    public void rm(String[] args) {
        File file = new File(args[0]);
        if (!file.delete())
            System.out.println("rm: cannot remove '" + file.getName() + "': No such file or directory");
    }

    public void cat(String[] args) throws IOException {

        for (String fileName : args) {
            File file = new File(fileName);

            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                String line;
                while ((line = br.readLine()) != null)
                    System.out.println(line);
            }
            else
                System.out.println("cat: " + file.getName() + ": No such file or directory\n");
        }
    }

    public void chooseCommandAction() throws IOException {
        String commandName = parser.getCommandName();
        if (commandName.equals("echo")){
            echo();
        }
        else if (commandName.equals("pwd")){
            System.out.println(pwd());
        }
        else if (commandName.equals("cd")){
            cd();
        }
        else if (commandName.equals("ls")){
            if(parser.args.length == 0)
            {
                ls();
            }
            else
            {
                if(parser.args[0].equals("-r") )
                {
                    lsReverse();
                }
            }
        }
        else if(commandName.equals("mkdir"))
        {
            mkdir(parser.args);
        }
        else if(commandName.equals("rmdir"))
        {
            rmdir(parser.args);
        }
        else if(commandName.equals("touch"))
        {
            touch(parser.args);
        }
        else if (commandName.equals("cp")) {
            if (parser.args[0].equals("-r"))
                cp_r(parser.args);
            else
                cp(parser.args);
        }
        else if (commandName.equals("rm"))
            rm(parser.args);
        else if (commandName.equals("cat"))
            cat(parser.args);
        else
            System.out.println(commandName + ": command not found");
    }

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub

        Parser.commands.put("echo", 1);
        Parser.commands.put("pwd", 1);
        Parser.commands.put("cd", 1);
        Parser.commands.put("ls", 1);
        Parser.commands.put("ls -r", 1);
        Parser.commands.put("mkdir", 1);
        Parser.commands.put("rmdir", 1);
        Parser.commands.put("touch", 1);
        Parser.commands.put("cp", 1);
        Parser.commands.put("cp -r", 1);
        Parser.commands.put("rm", 1);
        Parser.commands.put("cat", 1);

        Scanner scan = new Scanner(System.in);
        String input = "";
        Parser parser = new Parser();
        while(true)
        {
            System.out.print("> ");
            input = scan.nextLine();
            if (input.equals("exit")) {
                break;
            }
            if (parser.parse(input))
            {
                Terminal terminal = new Terminal(parser);
                terminal.chooseCommandAction();
            }
        }
    }
}