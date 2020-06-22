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
            ArrayList<String> dates = new ArrayList<String>();
            ArrayList<String> rows = new ArrayList<String>();
            String[][] results = new String[LogsServlet.logs.size()+1][LogsServlet.logs.size()+1];
            results[0][0] = "name\t";

            List<JsonObject> logs = new ArrayList<JsonObject>(LogsServlet.logs);
            Collections.reverse(logs);

            int index = 1;
            int logIndex = 0;
            for (JsonObject log : logs) {
                if (logIndex+1 == log.size()) {
                    results[index][0] = log.get("timestamp").toString().substring(1, 11) + "\n";
                }
                else {
                    results[index][0] = log.get("timestamp").toString().substring(1, 11) + "\t";
                }
                index++;
                dates.add(log.get("timestamp").toString().substring(1, 11));
            }

            index = 1;
            logIndex = 0;
            for (JsonObject log : logs) {
                if (!rows.contains(log.get("logger").toString().substring(1, log.get("logger").toString().length()-1))) {
                    if (logIndex+1 == log.size()) {
                        results[0][index] = log.get("logger").toString().substring(1, log.get("logger").toString().length() - 1) + "\n";
                    }
                    else {
                        results[0][index] = log.get("logger").toString().substring(1, log.get("logger").toString().length() - 1) + "\t";
                    }
                    rows.add(log.get("logger").toString().substring(1, log.get("logger").toString().length()-1));
                    index++;
                }
                logIndex++;
            }

            logIndex = 0;
            for (JsonObject log : logs) {
                if (!rows.contains(log.get("level").toString().substring(1, log.get("level").toString().length()-1))) {
                    if (logIndex+1 == log.size()) {
                        results[0][index] = log.get("level").toString().substring(1, log.get("level").toString().length() - 1) + "\n";
                    }
                    else {
                        results[0][index] = log.get("level").toString().substring(1, log.get("level").toString().length() - 1) + "\t";
                    }
                    rows.add(log.get("level").toString().substring(1, log.get("level").toString().length()-1));
                    index++;
                }
            }

            logIndex = 0;
            for (JsonObject log : logs) {
                if (!rows.contains(log.get("thread").toString().substring(1, log.get("thread").toString().length()-1))) {
                    if (logIndex+1 == log.size()) {
                        results[0][index] = log.get("thread").toString().substring(1, log.get("thread").toString().length() - 1) + "\n";
                    }
                    else {
                        results[0][index] = log.get("thread").toString().substring(1, log.get("thread").toString().length() - 1) + "\t";
                    }
                    rows.add(log.get("thread").toString().substring(1, log.get("thread").toString().length()-1));
                    index++;
                }
            }

            for (JsonObject log : logs) {
                int row = rows.indexOf(log.get("logger").toString().substring(1, log.get("logger").toString().length()-1));
                int col = dates.indexOf(log.get("timestamp").toString().substring(1, 11));
                String endChars = "";
                if (col+1 == dates.size()) {
                    endChars = "\n";
                }
                else {
                    endChars = "\t";
                }
                if (results[col+1][row+1] == null) {
                    results[col+1][row+1] = "1" + endChars;
                }
                else {
                    results[col+1][row+1] = Integer.toString(Integer.parseInt(results[col+1][row+1].replaceAll(endChars, "")) + 1) + endChars;
                }

                row = rows.indexOf(log.get("level").toString().substring(1, log.get("level").toString().length()-1));
                if (results[col+1][row+1] == null) {
                    results[col+1][row+1] = "1" + endChars;
                }
                else {
                    results[col+1][row+1] = Integer.toString(Integer.parseInt(results[col+1][row+1].replaceAll(endChars, "")) + 1) + endChars;
                }

                row = rows.indexOf(log.get("thread").toString().substring(1, log.get("thread").toString().length()-1));
                if (results[col+1][row+1] == null) {
                    results[col+1][row+1] = "1" + endChars;
                }
                else {
                    results[col+1][row+1] = Integer.toString(Integer.parseInt(results[col+1][row+1].replaceAll(endChars, "")) + 1) + endChars;
                }
            }

            String output = Arrays.stream(results).flatMap(Arrays::stream).collect(Collectors.joining());
            String formattedOutput = output.replaceAll("null", "0");
            writer.append(formattedOutput);

        }
    }
}
