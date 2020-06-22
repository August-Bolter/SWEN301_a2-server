package nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** This class is used for obtaining log statistics in CSV format. */
public class StatsCSVServlet extends HttpServlet {
    /** Returns a csv-encoded log statistics, the table has days represented as columns, and the rows represent loggers, log levels and threads. The cells at the intersection of rows and columns represent the number of log events for the respective category
     *  @param req The users request
     *  @param resp The servlets response */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (LogsServlet.logs.size() > 0) {
            resp.setContentType("text/csv"); //Since output will be CSV text
            PrintWriter writer = resp.getWriter();
            ArrayList<String> dates = new ArrayList<String>(); //List of log dates
            ArrayList<String> rows = new ArrayList<String>(); //List of log values (logger, level and thread)
            String[][] results = new String[LogsServlet.logs.size()+1][LogsServlet.logs.size()+1]; //List of CSV table values (number of log values (logger, thread, level) that match date)
            results[0][0] = "name\t";

            List<JsonObject> logs = new ArrayList<JsonObject>(LogsServlet.logs); //Deep copy of logs
            Collections.reverse(logs); //So they can be reversed without affecting original list

            /* Adding the timestamp to the list */
            for (JsonObject log : logs) {
                if (!dates.contains(log.get("timestamp").toString().substring(1, 11))) {
                    dates.add(log.get("timestamp").toString().substring(1, 11)); //Adding the date to the list
                }
            }

            /* Adding the logger to the list */
            int loggerIndex = 0;
            for (JsonObject log : logs) {
                if (!rows.contains(log.get("logger").toString().substring(1, log.get("logger").toString().length()-1))) {
                    rows.add(log.get("logger").toString().substring(1, 11)); //Adding the date to the list
                    loggerIndex++;
                }
            }

            /* Adding the level to the list */
            int levelIndex = loggerIndex;
            for (JsonObject log : logs) {
                if (!rows.contains(log.get("level").toString().substring(1, log.get("level").toString().length()-1))) {
                    rows.add(log.get("level").toString().substring(1, log.get("level").toString().length()-1)); //Adding the date to the list
                    levelIndex++;
                }
            }

            /* Adding the thread to the list */
            int threadIndex = levelIndex;
            for (JsonObject log : logs) {
                if (!rows.contains(log.get("thread").toString().substring(1, log.get("thread").toString().length()-1))) {
                    rows.add(log.get("thread").toString().substring(1, log.get("thread").toString().length()-1)); //Adding the date to the list
                    threadIndex++;
                }
            }


            /* Adding the timestamp to the CSV table */
            int index = 1;
            int logIndex = 0; //For determining whether we are at the end of the table
            for (String date : dates) {
                if (logIndex+1 == dates.size()) { //We are at the end of the table so add \n
                    results[index][0] = date + "\n"; //Adding the date to the CSV table
                }
                else { //Otherwise add \t
                    results[index][0] = date + "\t";
                }
                index++;
            }

            /* Adding the logger to the CSV table */
            index = 1;
            logIndex = 0;
            for (String logger : rows) {
                if (logIndex == loggerIndex) {
                    results[0][index] = logger + "\n";
                    break;
                }
                else {
                    results[0][index] = logger + "\t";
                }
                index++;
                logIndex++;
            }

            /* Adding the level to the CSV table */
            logIndex = 0;
            for (String level : rows) {
                if (logIndex == levelIndex) {
                    results[0][index] = level + "\n";
                    break;
                }
                else {
                    results[0][index] = level + "\t";
                }

                index++;
            }

            /* Adding the thread to the CSV table */
            logIndex = 0;
            for (String thread : rows) {
                if (logIndex == threadIndex) {
                    results[0][index] = thread + "\n";
                }
                else {
                    results[0][index] = thread + "\t";
                }
                index++;
                logIndex++;
            }

            /* Determining CSV table values */
            for (JsonObject log : logs) {
                /* Calculating the CSV value for the logger */
                int row = rows.indexOf(log.get("logger").toString().substring(1, log.get("logger").toString().length()-1)); //Getting row index of CSV table based off list
                int col = dates.indexOf(log.get("timestamp").toString().substring(1, 11)); //Getting col index of CSV table based off list
                String endChars = "";
                /* Checking if we are the end of the CSV table */
                if (col+1 == dates.size()) {
                    endChars = "\n";
                }
                else {
                    endChars = "\t";
                }
                /* If no value is previously in the cell then the value should be 1. */
                if (results[col+1][row+1] == null) {
                    results[col+1][row+1] = "1" + endChars;
                }
                /* Otherwise it should be old value + 1 */
                else {
                    results[col+1][row+1] = (Integer.parseInt(results[col + 1][row + 1].replaceAll(endChars, "")) + 1) + endChars;
                }

                /* Calculating the CSV value for the level */
                row = rows.indexOf(log.get("level").toString().substring(1, log.get("level").toString().length()-1));
                if (results[col+1][row+1] == null) {
                    results[col+1][row+1] = "1" + endChars;
                }
                else {
                    results[col+1][row+1] = (Integer.parseInt(results[col + 1][row + 1].replaceAll(endChars, "")) + 1) + endChars;
                }

                /* Calculating the CSV value for the thread */
                row = rows.indexOf(log.get("thread").toString().substring(1, log.get("thread").toString().length()-1));
                if (results[col+1][row+1] == null) {
                    results[col+1][row+1] = "1" + endChars;
                }
                else {
                    results[col+1][row+1] = (Integer.parseInt(results[col + 1][row + 1].replaceAll(endChars, "")) + 1) + endChars;
                }
            }

            /* Converting 2d array into String that doesn't include the array formatting (excludes the brackets and the commas) */
            String output = Arrays.stream(results).flatMap(Arrays::stream).collect(Collectors.joining());
            String formattedOutput = output.replaceAll("null", "0");
            writer.append(formattedOutput);

        }
    }
}
