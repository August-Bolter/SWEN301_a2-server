package nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsPNGServlet extends HttpServlet {
    @Override
    /** Returns a PNG image of a bar-chart where the bars represent different the number of logs at different levels. */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (LogsServlet.logs != null) {
            if (LogsServlet.logs.size() > 0) {
                resp.setContentType("image/png"); //Since output will be a PNG image
                Map<String, Integer> levelToLogs = new HashMap<String, Integer>(); //Storing levels and logs relationship
                List<JsonObject> logs = new ArrayList<JsonObject>(LogsServlet.logs); //Deep copy of logs

                /* Storing initial values in map */
                String level = "";
                for (JsonObject log : logs) {
                    level = log.get("level").toString().substring(1, log.get("level").toString().length() - 1);
                    levelToLogs.put(level, 0);
                }

                /* Storing the number of logs for a level in the map */
                for (JsonObject log : logs) {
                    level = log.get("level").toString().substring(1, log.get("level").toString().length() - 1);
                    if (levelToLogs.containsKey(level)) {
                        levelToLogs.put(level, levelToLogs.get(level) + 1);
                    }
                }

                /* Storing the map in a dataset */
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                for (Map.Entry<String, Integer> entry : levelToLogs.entrySet()) {
                    dataset.addValue(new Double(entry.getValue()), "level", entry.getKey());
                }
                /* Creating a vertical bar chart without a legend, tooltip or url */
                JFreeChart barChart = ChartFactory.createBarChart("Number of logs for each level", "Level", "Number of logs", dataset, PlotOrientation.VERTICAL, false, true, false);

                /* Exporting bar chart as a PNG to the response stream */
                OutputStream out = resp.getOutputStream();
                ChartUtilities.writeChartAsPNG(out, barChart, Toolkit.getDefaultToolkit().getScreenSize().width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2);
                out.close();
            }
        }
    }
}
