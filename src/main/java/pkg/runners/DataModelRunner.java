package pkg.runners;

import pkg.erd.ERDModel;

import java.io.IOException;
import java.util.Scanner;

public class DataModelRunner
{
    public void dataModelRunner(Scanner scanner) throws IOException {
        while (true)
        {
            System.out.println("Select: \n 1 To Generate ERD  \n 2 To Go Back ");
            ERDModel erdModel = new ERDModel();
            String option = scanner.nextLine();

            if(option.equals("2")) {
                break;
            }

            switch (option) {
                case "1":
                    System.out.println("\n Write the Database Name for which you want to Generate an ERD");
                    String databaseName = scanner.nextLine();
                    erdModel.generateERDiagram(databaseName);
                    break;
                default:
                    System.out.println("\n Invalid Option");
            }
        }
    }
}
