package pkg.export;

import java.io.*;
import java.util.*;

import static pkg.utils.Constants.DB_FILE_PATH;
import static pkg.utils.Constants.original;

public class ExportSAVModel {

    static String base_file_path = original+DB_FILE_PATH;
    public static final String dump_file_path = original+"sql_dumps/";
    static List<String> columns_values;

    //Export structure along with values
    public void exportWithValues(String database_name) throws IOException {
        ArrayList<String> fileList = new ArrayList<>();

        if (checkIfExist(database_name)) {

            //adding table files present in each table directory
            addTableFiles(database_name, fileList);

            //Initializing dump file name
            String dumpFileName = database_name + "~With_Values~" + System.currentTimeMillis() + ".sql";

            //File path where all sql dump files will be stored
            String dumpFilePath = dump_file_path + dumpFileName;


            try (FileWriter fileWriter = new FileWriter(dumpFilePath)) {


                //For every file(table) present in the list check the file with the ~metadata keyword
                for (String fileName : fileList) {
                    if (fileName.contains("_metadata")) {
                        //Current file present in the given database
                        String filePath = base_file_path + database_name + "/" + fileName.split("_")[0] + "/" + fileName;
                        columns_values = new ArrayList<>();
                        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

                            //Here we will return the final query generated after considering different constraints
                            StringBuilder createQuery = getFinalQuery(fileName, br);

                            //Final Query to be written in a file
                            String final_query = createQuery.substring(0, createQuery.length() - 1) + "\n);\n\n";


                            String columns = columns_values.toString().replace("[", "").replace("]", "");


                            //Writing into file
                            fileWriter.write(final_query);

                            // ----------------------  To Write Values----------------------------------------------

                            String insert_values_path = base_file_path + database_name + "/" + fileName.split("_")[0] + "/" + fileName.split("_")[0] + ".txt";


                            try (BufferedReader br1 = new BufferedReader(new FileReader(insert_values_path))) {

                                String current_executing_lines = "";
                                while ((current_executing_lines = br1.readLine()) != null) {
                                    StringBuilder insert_query = new StringBuilder("INSERT INTO " + fileName.split("_")[0] + "(" + columns + ") values (");

                                    if (current_executing_lines.isEmpty() || current_executing_lines.trim().equals("") || current_executing_lines.trim().equals("\n"))
                                        current_executing_lines = br1.readLine();

                                    String column_string = current_executing_lines.replaceAll("~", ",");

                                    insert_query.append(column_string).append(" ) ");


                                    insert_query = new StringBuilder(insert_query.substring(0, insert_query.length() - 1) + ";\n\n");
                                    fileWriter.write(insert_query.toString());
                                }

                            }
                        }
                    }
                }
                fileWriter.close();
                System.out.println("Database Successfully Exported. Please Check resources/sql_dumps/" + dumpFileName + " !!");


            }


        } else {
            System.out.println("\nDatabase not Found !! Please Enter Again.");
        }
    }

    public void exportWithOutValues(String database_name) throws IOException {
        ArrayList<String> fileList = new ArrayList<>();
        if (checkIfExist(database_name)) {
            addTableFiles(database_name, fileList);
            //Initializing dump file name
            String dumpFileName = database_name + "~Without_Values~" + System.currentTimeMillis() + ".sql";

            //File path where all sql dump files will be stored
            String dumpFilePath = dump_file_path + dumpFileName;

            //Initializing file writer object to write into respective file
            try (FileWriter fileWriter = new FileWriter(dumpFilePath)) {

                //For every file(table) present in the list check the file with the ~metadata keyword
                for (String fileName : fileList) {
                    if (fileName.contains("_metadata")) {
                        //Current file present in the given database
                        String filePath = base_file_path + database_name + "/" + fileName.split("_")[0] + "/" + fileName;
                        columns_values = new ArrayList<>();

                        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                            StringBuilder createQuery = getFinalQuery(fileName, br);
                            //Final Query to be written in a file
                            String final_query = createQuery.substring(0, createQuery.length() - 1) + "\n);\n";

                            //Writing into file
                            fileWriter.write(final_query);
                        }

                    }

                }
                System.out.println("Database Successfully Exported. Please Check resources/sql_dumps/" + dumpFileName + " !!");


            }
        } else {
            System.out.println("Given Database : " + database_name + " Not Found ----  Please Select Database Again ");
        }

    }

    private StringBuilder getFinalQuery(String fileName, BufferedReader br) throws IOException {
        String current_executing_line;
        StringBuilder createQuery = new StringBuilder("Create table " + fileName.split("_")[0] + " ( ");
        while ((current_executing_line = br.readLine()) != null) {
            //This if statement will check if current executing line is empty or next line as a content
            if (ifNotMeaningFulLine(current_executing_line))
                //read next line in the file
                current_executing_line = br.readLine();


            if (!(current_executing_line.contains("PK") || current_executing_line.contains("FK"))) {
                StringBuilder query_except_key = new StringBuilder();
                //Getting the query formed (By checking if key column is present or not )
                getFormattedQueryWithoutKeys(current_executing_line, createQuery, query_except_key);

            } else if (current_executing_line.contains("PK")) {
                // For Primary Key
                createQuery.append("\n").append("PRIMARY KEY ( ").append(current_executing_line.split("~")[1]).append(" ),\n");

            } else {
                // For Foreign key
                createQuery.append("FOREIGN KEY ( ").append(current_executing_line.split("~")[1]).append(" ) ").append(" REFERENCES ").append(current_executing_line.split("~")[3]).append("( ").append(current_executing_line.split("~")[2]).append(" )\n");
            }
        }
        return createQuery;
    }

    private void addTableFiles(String database_name, ArrayList<String> fileList) {
        File[] fetchedTableDirectories = new File(base_file_path + database_name).listFiles(File::isDirectory);
        assert fetchedTableDirectories != null;

        for (File file : fetchedTableDirectories) {

            for (String individual_file : Objects.requireNonNull(file.list())) {
                //add files present in the database in the file list
                addFiles(fileList, individual_file);
            }
        }
    }

    private StringBuilder getFormattedQueryWithoutKeys(String current_executing_line, StringBuilder createQuery, StringBuilder query_without_key) {

        String[] attributes_in_currentLine = current_executing_line.split("~");
        String constraint = "";
        if (attributes_in_currentLine.length == 3) {
            constraint = current_executing_line.split("~")[2];
            if (constraint.equals("notnull")) {
                constraint = " NOT NULL ";
            }

        }
        createQuery.append("\n\t").append(current_executing_line.split("~")[0]).append(" ").append(current_executing_line.split("~")[1]).append(constraint).append(",");
        columns_values.add(current_executing_line.split("~")[0]);
        return createQuery;
    }

    private boolean ifNotMeaningFulLine(String current_executing_line) {
        return current_executing_line.isEmpty() || current_executing_line.trim().equals("") || current_executing_line.trim().equals("\n");
    }

    private void addFiles(ArrayList<String> fileList, String fetchedFile) {
        //Storing file name in list of files
        fileList.add(fetchedFile);

    }

    private boolean checkIfExist(String database_name) {
        System.out.println(base_file_path + database_name);
        return (new File(base_file_path + database_name).exists());
    }
}

