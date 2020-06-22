package nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsPNGServlet extends HttpServlet {
    @Override
    /** Returns a PNG image of a bar-chart where the bars represent different the number of logs at different levels. */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (LogsServlet.logs.size() > 0) {
            resp.setContentType("image/png"); //Since output will be a PNG image
            Map<String, Integer> levelToLogs = new HashMap<String, Integer>();
            List<JsonObject> logs = new ArrayList<JsonObject>(LogsServlet.logs);

            List<String> logLevels = new ArrayList<String>();
            for (LogsServlet.levels level : LogsServlet.levels.values()) {
                logLevels.add(level.name());
            }
            logs.sort((o1, o2) -> {
                if (logLevels.indexOf(o1.get("level").toString().substring(1, o1.get("level").toString().length()-1)) <
                        logLevels.indexOf(o2.get("level").toString().substring(1, o2.get("level").toString().length()-1))) {
                    return -1;
                }
                else if (logLevels.indexOf(o1.get("level").toString().substring(1, o1.get("level").toString().length()-1)) >
                        logLevels.indexOf(o2.get("level").toString().substring(1, o2.get("level").toString().length()-1))) {
                    return 1;
                }
                return 0;
            });

            String level = "";
            for (JsonObject log : logs) {
                level = log.get("level").toString().substring(1, log.get("level").toString().length()-1);
                levelToLogs.put(level, 0);
            }

            for (JsonObject log : logs) {
                level = log.get("level").toString().substring(1, log.get("level").toString().length()-1);
                if (levelToLogs.containsKey(level)) {
                    levelToLogs.put(level, levelToLogs.get(level) + 1);
                }
            }

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Integer> entry : levelToLogs.entrySet()) {
                dataset.addValue(new Double(entry.getValue()), "level", entry.getKey());
            }
            JFreeChart barChart = ChartFactory.createBarChart("Number of logs for each level", "Level", "Number of logs", dataset, PlotOrientation.VERTICAL, false, true, false);
        }
    }
}
