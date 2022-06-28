package pkg.erd;

import java.io.*;
import java.util.*;

import static pkg.utils.Constants.DB_FILE_PATH;
import static pkg.utils.Constants.original;

public class ERDModel
{
    static String base_file_path = original+DB_FILE_PATH;
    public static final String erd_path = original+"erdiagram/";


    public void generateERDiagram(String database_name) throws IOException {
        ArrayList<String> fileList = new ArrayList<>();
        if (checkIfExist(database_name))
        {


            //adding table files present in each table directory
            addTableFiles(database_name, fileList);

            String dump_file_name = database_name + "_" + System.currentTimeMillis() + ".txt";
            String dump_file_path = erd_path + dump_file_name;
            HashMap<String, StringBuilder> column_key_map;


            //Initializing file writer object to write into respective file
            try (FileWriter fileWriter = new FileWriter(dump_file_path)) {

                //Loop that will traverse through entire metadata file and will store all the keys along with
                //the column name in the Hashmap
                for (String fileName : fileList) {
                    if (fileName.contains("_metadata")) {
                        column_key_map = new HashMap<>();
                        String cardinality = "-";
                        fileWriter.append("+").append("-".repeat(40)).append("+\n");
                        try (BufferedReader br = new BufferedReader(new FileReader(base_file_path + database_name + "/" + fileName.split("_")[0]+"/"+fileName))) {
                            String current_executing_line;
                            while ((current_executing_line = br.readLine()) != null) {
                                if (current_executing_line.isEmpty() || current_executing_line.trim().equals("") || current_executing_line.trim().equals("\n"))
                                    current_executing_line = br.readLine();

                                //If file reading line contains PK
                                //add the key and column in the map
                                if (current_executing_line.contains("PK")) {
                                    String[] current_line_attributes = current_executing_line.split("~");
                                    column_key_map.put(current_line_attributes[1], new StringBuilder("PK"));

                                }
                                //If file reading line contains FK
                                //add the key and column in the map
                                else if (current_executing_line.contains("FK"))
                                {
                                    String[] current_line_attributes = current_executing_line.split("~");
                                    if (column_key_map.containsKey(current_line_attributes[1])) {
                                        column_key_map.get(current_line_attributes[1]).append(",").append("FK");
                                    } else {
                                        column_key_map.put(current_line_attributes[1], new StringBuilder("FK"));
                                    }

                                    //For Cardinality
                                    String file_path_for_cardinality = base_file_path + database_name + "/" +fileName.split("_")[0]  + "/" + fileName.split("_")[0] + ".txt";
                                    try(BufferedReader cardinality_reader = new BufferedReader(new FileReader(file_path_for_cardinality)))
                                    {
                                        String[] table_column_data;
                                        String current_line_in_table;
                                        List<String> key_values = new ArrayList<>();
                                        while ((current_line_in_table = cardinality_reader.readLine()) != null)
                                        {
                                            table_column_data = current_line_in_table.split("~");
                                            key_values.add(table_column_data[table_column_data.length-1]);
                                        }
                                        Set<String> unique_values = new HashSet<String>(key_values);
                                        if(unique_values.size()== key_values.size()) {
                                            cardinality = "One:One";
                                        } else {
                                            cardinality = "Many:One";
                                        }
                                    }

                                }

                            }
                        }
                        try (BufferedReader br = new BufferedReader(new FileReader(base_file_path + database_name  + "/" +  fileName.split("_")[0] + "/" + fileName))) {
                            String current_executing_line;
                            fileWriter.append(String.format("| %-40s |\n", fileName.split("_")[0].toUpperCase()));
                            fileWriter.append("+").append("-".repeat(19)).append("+").append("-".repeat(22)).append("+\n");
                            //loop which is responsible to enter the column name along with the keys in the table
                            while ((current_executing_line = br.readLine()) != null) {
                                if (current_executing_line.isEmpty() || current_executing_line.trim().equals("") || current_executing_line.trim().equals("\n"))
                                    current_executing_line = br.readLine();

                                if (!(current_executing_line.contains("PK") || current_executing_line.contains("FK"))) {
                                    fileWriter.append(String.format("| %-17s | %-20s |\n", column_key_map.get(current_executing_line.split("~")[0]) == null ?
                                                    "" : column_key_map.get(current_executing_line.split("~")[0]),
                                            current_executing_line.split("~")[0] + " " + current_executing_line.split("~")[1]));
                                }

                            }
                            fileWriter.append(String.format("| %-17s | %-20s |\n","Cardinality",cardinality));

                        }
                        fileWriter.append("+").append("-".repeat(42)).append("+\n");
                    }
                }
                System.out.println("ERD Successfully Generated. Please Check resources/erd/" + dump_file_name + " !!");
            }


        } else {
            System.out.println("Try again :you might have given wrong database name ");
        }
    }

    private void addTableFiles(String database_name, ArrayList<String> fileList)
    {
        File[] fetchedTableDirectories = new File(base_file_path + database_name).listFiles(File::isDirectory);
        assert fetchedTableDirectories != null;

        for (File file : fetchedTableDirectories) {

            for (String individual_file : Objects.requireNonNull(file.list())) {
                //add files present in the database in the file list
                addFiles(fileList, individual_file);
            }
        }
    }

    private boolean checkIfExist(String database_name) {
        System.out.println(base_file_path + database_name);
        return (new File(base_file_path + database_name).exists());
    }

    private void addFiles(ArrayList<String> fileList, String fetchedFile) {
        //Storing file name in list of files
        fileList.add(fetchedFile);

    }
}
