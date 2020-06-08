package nz.ac.vuw.swen301.a2.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogsServlet extends HttpServlet {

    private List<JsonObject> logs;
    enum levels {
        OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL
    }

    public LogsServlet() {
        List<JsonObject> logs = new ArrayList<JsonObject>();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("limit") == null || req.getParameter("level") == null) {
            resp.sendError(400);
            return;
        }

        int limit = 0;
        try {
            limit = Integer.parseInt(req.getParameter("limit"));
            if (limit > Integer.MAX_VALUE || limit < 0) {
                resp.sendError(400);
                return;
            }
        }
        catch (NumberFormatException x) {
            resp.sendError(400);
            return;
        }
        String level = req.getParameter("level");
        boolean isValid = true;
        //Checking if level is valid and finding levels to consider
        List<String> levelsToConsider = new ArrayList<String>();
        for (levels l : levels.values()) {
            levelsToConsider.add(l.name());
            if (l.name().equals(level)) { //Case-sensitive
                isValid = true;
                break;
            }
            else {
                isValid = false;
            }
        }
        //If level isn't valid isn't then send 400 error
        if (!isValid) {
            resp.sendError(400);
            return;
        }

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        int numLogs = 0;
        List<JsonObject> output = new ArrayList<JsonObject>(limit);

        for (JsonObject j : logs) {
            for (String s : levelsToConsider) {
                if(j.get("level").equals(s)) {
                    if (numLogs != limit) {
                        output.add(j);
                        numLogs++;
                    }
                }
            }
        }
        output.sort((o1, o2) -> {
            try {
                Date dateo1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(o1.get("timestamp").toString());
                Date dateo2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(o2.get("timestamp").toString());
                if (dateo1.after(dateo2)) return 1;
                else if (dateo2.after(dateo1)) return -1;
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        for (JsonObject j : output) {
            out.println(j.toString());
        }
        out.close();
        resp.sendError(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getParameter("LogEvent") == null) {
            resp.sendError(400);
        }
        String LogEvent = req.getParameter("LogEvent");
        Gson g = new Gson();
        JsonObject j = g.fromJson(LogEvent, JsonObject.class);
        if (j.get("id") == null || j.get("message") == null || j.get("timestamp") == null || j.get("thread") == null
        || j.get("logger") == null || j.get("level") == null) {
            resp.sendError(400);
            return;
        }
        if (j.get("id").equals("") || j.get("message").equals("") || j.get("timestamp").equals("") || j.get("thread").equals("")
                || j.get("logger").equals("") || j.get("level").equals("")) {

        }
        if (j.entrySet().size() != 6) {
            resp.sendError(400);
        }

    }
}
