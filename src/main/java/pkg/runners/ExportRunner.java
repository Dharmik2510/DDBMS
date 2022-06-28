package pkg.runners;

import pkg.export.ExportSAVModel;

import java.io.IOException;
import java.util.Scanner;

public class ExportRunner
{
    public void exportRunner(Scanner scanner) throws IOException
    {
        while (true)
        {
            System.out.println("Select: \n 1 For Exporting with values \n 2 For Exporting without values \n 3 To Go Back ");
            System.out.println("--");
            String option = scanner.nextLine();
            if (option.equals("3")) {
                break;
            }
            doExportOperation(option, scanner);
        }
    }

    private void doExportOperation(String option, Scanner scanner) throws IOException {

        ExportSAVModel exportSAVModel = new ExportSAVModel();
        switch (option)
        {
            case "1":
                System.out.println("\n Write the Database Name for which you want to Export");
                System.out.println("--");
                exportSAVModel.exportWithValues(scanner.nextLine().toLowerCase());
                break;

            case "2":
                System.out.println("\n Write the Database Name for which you want to Export");
                System.out.println("--");
                exportSAVModel.exportWithOutValues(scanner.nextLine().toLowerCase());
                break;

            default:
                System.out.println("Input not recognized : I guess you should try again");
                break;
        }
    }
}
