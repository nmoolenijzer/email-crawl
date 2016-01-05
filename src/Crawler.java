import java.util.*;
import java.io.*;
import java.net.URL;

public class Crawler {
	
	private HashSet<String> emails;
	private URL initURL;
	private InputStream inputStream;
	private BufferedReader bufferedReader;
	private BufferedReader inputHTML;
	
	public Crawler(String file) {
		emails = new HashSet<String>();
		try {
			initURL = new URL(file);
			inputStream = initURL.openStream();
			inputHTML = new BufferedReader(new InputStreamReader(inputStream));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error loading website.");
		}
	}
	
	public void addEmail(String newArg) {
		emails.add(newArg);
	}
	
	public void printEmails() {
		for (String email : emails) {
			System.out.println(email);
		}
	}
	
	public void readEmails() throws IOException {
		int curr = inputHTML.read();
		while (curr != -1) {
			String currWord = "";
			while ((char)curr == ' ') {
				curr = inputHTML.read();
			}
			while ((char)curr != ' ' && curr != 10 && curr != 9 && curr != -1) {
				currWord += (char)curr;
				curr = inputHTML.read();
			}
			System.out.println(currWord);
			if (currWord.contains("@")) {
				this.addEmail(currWord);
			}
			curr = inputHTML.read();
			
		}
	}
	
	public static void main(String args[]) {
		
		if (args.length == 0) {
			System.err.println("No website provided. Try again with a website.");
		}
		
		else {
			Crawler myCrawler = new Crawler("http://jana.com/contact");
			try {
				myCrawler.readEmails();
				myCrawler.printEmails();
			} catch (IOException e) {
				System.err.println("Reading emails failed. Please try again.");
			}
		}
		
	}
	
}
