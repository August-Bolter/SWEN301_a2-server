package nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** This class is used for obtaining log statistics in CSV format. */
public class StatsCSVServlet extends HttpServlet {
    /** Returns a csv-encoded log statistics, the table has days represented as columns, and the rows represent loggers, log levels and threads. The cells at the intersection of rows and columns represent the number of log events for the respective category
     *  @param req The users request
     *  @param resp The servlets response */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (LogsServlet.logs.size() > 0) {
            resp.setContentType("text/csv");
            PrintWriter writer = resp.getWriter();

            writer.append("name\t");
            List<JsonObject> logs = new ArrayList<JsonObject>();
            for (JsonObject log : LogsServlet.logs) {
                logs.add(log);
            }
            Collections.reverse(logs);
            int index = 0;
            for (JsonObject log : logs) {
                writer.append(log.get("timestamp").toString().substring(1, 11));
                index++;
                if (index == logs.size()) {
                    writer.append("\n");
                }
                else {
                    writer.append("\t");
                }
            }

            for (JsonObject log : logs) {

            }

        }
    }
}
