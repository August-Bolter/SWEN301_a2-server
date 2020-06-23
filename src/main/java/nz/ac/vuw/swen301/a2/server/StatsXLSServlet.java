package nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonObject;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsXLSServlet extends HttpServlet {
    @Override
    /** Returns the log statistics (table format) as an excel file. The table has days represented as columns, and the rows represent loggers, log levels and threads. The cells at the intersection of rows and columns represent the number of log events for the respective category */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (LogsServlet.logs != null) {
            if (LogsServlet.logs.size() > 0) {
                resp.setContentType("application/vnd.ms-excel"); //Since output will be an excel file
                resp.setCharacterEncoding("UTF-8");

                ArrayList<String> dates = new ArrayList<String>(); //List of log dates
                ArrayList<String> rows = new ArrayList<String>(); //List of log values (logger, level and thread)

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
                        rows.add(log.get("logger").toString().substring(1, log.get("logger").toString().length()-1)); //Adding the date to the list
                        loggerIndex++;
                    }
                }

                /* Adding the level to the list */
                int levelIndex = loggerIndex;
                for (JsonObject log : logs) {
                    if (!rows.contains(log.get("level").toString().substring(1, log.get("level").toString().length() - 1))) {
                        rows.add(log.get("level").toString().substring(1, log.get("level").toString().length() - 1)); //Adding the date to the list
                        levelIndex++;
                    }
                }

                /* Adding the thread to the list */
                int threadIndex = levelIndex;
                for (JsonObject log : logs) {
                    if (!rows.contains(log.get("thread").toString().substring(1, log.get("thread").toString().length() - 1))) {
                        rows.add(log.get("thread").toString().substring(1, log.get("thread").toString().length() - 1)); //Adding the date to the list
                        threadIndex++;
                    }
                }

                String[][] results = new String[dates.size() + 1][rows.size() + 1]; //List of CSV table values (number of log values (logger, thread, level) that match date)
                /* Since <tr> = row and <th> = header */
                results[0][0] = "name";

                /* Adding the timestamp to the CSV table */
                int index = 1;
                int logIndex = 0; //For determining whether we are at the end of the table
                for (String date : dates) {
                    results[index][0] = date; //Adding the date to the CSV table
                    index++;
                }

                /* Adding the logger to the CSV table */
                index = 1;
                for (int i = 0; i < loggerIndex; i++) {
                    results[0][index] = rows.get(i); //<td> = data
                    index++;
                }

                /* Adding the level to the CSV table */
                for (int i = loggerIndex; i < levelIndex; i++) {
                    results[0][index] = rows.get(i);
                    index++;
                }

                /* Adding the thread to the CSV table */
                for (int i = levelIndex; i < threadIndex; i++) {
                    results[0][index] = rows.get(i);
                    index++;
                }

                /* Determining CSV table values */
                for (JsonObject log : logs) {
                    /* Calculating the CSV value for the logger */
                    int row = rows.indexOf(log.get("logger").toString().substring(1, log.get("logger").toString().length() - 1)); //Getting row index of CSV table based off list
                    int col = dates.indexOf(log.get("timestamp").toString().substring(1, 11)); //Getting col index of CSV table based off list
                    /* If no value is previously in the cell then the value should be 1. */
                    if (results[col + 1][row + 1] == null) {
                        results[col + 1][row + 1] = "1";
                    }
                    /* Otherwise it should be old value + 1 */
                    else {
                        results[col + 1][row + 1] = Integer.toString(Integer.parseInt(results[col + 1][row + 1]) + 1);
                    }

                    /* Calculating the CSV value for the level */
                    row = rows.indexOf(log.get("level").toString().substring(1, log.get("level").toString().length() - 1));
                    /* If no value is previously in the cell then the value should be 1. */
                    if (results[col + 1][row + 1] == null) {
                        results[col + 1][row + 1] = "1";
                    }
                    /* Otherwise it should be old value + 1 */
                    else {
                        results[col + 1][row + 1] = Integer.toString(Integer.parseInt(results[col + 1][row + 1]) + 1);
                    }

                    /* Calculating the CSV value for the thread */
                    row = rows.indexOf(log.get("thread").toString().substring(1, log.get("thread").toString().length() - 1));
                    /* If no value is previously in the cell then the value should be 1. */
                    if (results[col + 1][row + 1] == null) {
                        results[col + 1][row + 1] = "1";
                    }
                    /* Otherwise it should be old value + 1 */
                    else {
                        results[col + 1][row + 1] = Integer.toString(Integer.parseInt(results[col + 1][row + 1]) + 1);
                    }
                }

                /* Sending 2d array */
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet();

                for (int row = 0; row < rows.size()+1; row++) {
                    for (int col = 0; col < dates.size()+1; col++) {

                    }
                }
            }
        }
    }
}
