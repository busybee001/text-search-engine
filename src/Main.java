import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Mini Search Engine");
        System.out.println("_____");
        System.out.print("Enter search query: ");
        String query = scanner.nextLine();
        System.out.println("Entered: " + query);
        scanner.close();
    }
}

