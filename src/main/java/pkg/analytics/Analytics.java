package pkg.analytics;

import pkg.queryengine.QueryEngine;
import pkg.usersession.UserSession;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;


import static pkg.utils.Constants.*;

public class Analytics {

    private static final String QUIT = "quit";
    private static final String QUERIES = "queries";
    private static final String UPDATE = "update";

    public void executeQuery() throws IOException {
        String query = null;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please enter a query (enter 'quit' to exit):");
            query = scanner.nextLine();
            if (query.equalsIgnoreCase(QUIT)) {
                System.out.println("Exiting...");
                break;
            }
            if (query.trim().isEmpty()) {
                System.out.println("Invalid query");
                continue;
            }
            System.out.println("Executing query: " + query);
            String[] queryParts = query.split(" ");
            String queryCheck = queryParts[1];

            switch(queryCheck){
                case QUERIES:
                    queryCount();
                    break;
                case UPDATE:
                    queryUpdateCount(queryParts[2]);
                    break;
                default:
                    System.out.println("Invalid query");
            }

        }
    }

   public void queryCount() throws IOException {

        String generalLogFilePath = original + LOGS_FILE_PATH + QUERY_LOG_FILE_NAME + ".txt";

        BufferedReader br = new BufferedReader(new FileReader(generalLogFilePath));

        String line;
        String[] lineParts;
        HashMap<String, Integer> userQueries = new HashMap<>();
        HashSet<String> users = new HashSet();
        List<String> queries = new ArrayList<>();
        String userName;
        String db;
        String vm;
        List<String> resultList = new ArrayList<>();

        while ((line = br.readLine()) != null) {

            String list;
            if(line.contains("@")) {
                 lineParts = line.split("@")[1]
                         .split(" ");
                if(!lineParts[2].equals("USE")) {
                    userName = lineParts[0]
                            .toLowerCase(Locale.ROOT)
                            .replaceAll("[^a-zA-Z0-9]", "");
                    db = lineParts[1]
                            .toLowerCase(Locale.ROOT)
                            .replaceAll("[^a-zA-Z0-9]", "");
                    vm = lineParts[2]
                            .toLowerCase(Locale.ROOT)
                            .replaceAll("[^a-zA-Z0-9]", "");
                    queries.add(lineParts[0]
                            .toLowerCase(Locale.ROOT)
                            .replaceAll("[^a-zA-Z0-9]", ""));
                    users.add(userName);

                    list = userName.concat(",").concat(db).concat(",").concat(vm);

                    resultList.add(list);
                }
            }
        }
        for(int i=0;i<= queries.size(); i++) {

            for(String s : resultList)
                userQueries.put(s,Collections.frequency(resultList, s));
        }
        userQueries.entrySet().forEach(entry -> {
            System.out.println("User " + entry.getKey().split(",")[0] + " submitted " + entry.getValue() + " for Database " + entry.getKey().split(",")[1] + " on " + entry.getKey().split(",")[2] );
        });
    }


    public void queryUpdateCount(String database) throws IOException {

        String generalLogFilePath = original + LOGS_FILE_PATH + QUERY_LOG_FILE_NAME + ".txt";

        BufferedReader br = new BufferedReader(new FileReader(generalLogFilePath));
        String tableName;
        String line;
        String[] lineParts;
        String validLineParts;
        List<String> resultList = new ArrayList<>();
        HashMap<String, Integer> updateQuery = new HashMap<>();

        while ((line = br.readLine()) != null) {

            if (line.contains("@")) {
                lineParts = line.split("@")[1]
                        .split(" ");
                if (lineParts[1].equals(database) && lineParts[3].equals("UPDATE") && !lineParts[3].equals("USE")){

                    validLineParts = line.split(":")[3].replaceAll("[^a-zA-Z0-9]", "");
                    if(validLineParts.equals("valid")) {
                        tableName = lineParts[3]
                                .toLowerCase(Locale.ROOT)
                                .replaceAll("[^a-zA-Z0-9]", "");

                        resultList.add(tableName);
                    }

                }
            }
        }
        for(int i=0;i<= resultList.size(); i++) {

            for(String s : resultList)
                updateQuery.put(s,Collections.frequency(resultList, s));
        }

        updateQuery.entrySet().forEach(entry -> {
            System.out.println("Total " + entry.getValue() + " Update operations are performed on " +  entry.getKey());
        });

    }
}
