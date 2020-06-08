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
import java.util.regex.Pattern;

public class LogsServlet extends HttpServlet {

    private List<JsonObject> logs;
    enum levels {
        OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL
    }

    public LogsServlet() {
        List<JsonObject> logs = new ArrayList<JsonObject>();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getParameter("limit") == null || req.getParameter("level") == null) {
            resp.sendError(400);
            return;
        }

        int limit;
        try {
            limit = Integer.parseInt(req.getParameter("limit"));
            if (limit < 0) {
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
                if(req.getParameter("level").equals(s)) {
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
        resp.setStatus(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(req.getParameter("LogEvent") == null) {
            resp.sendError(400);
        }
        String LogEvent = req.getParameter("LogEvent");
        Gson g = new Gson();
        JsonObject j = g.fromJson(LogEvent, JsonObject.class);
        boolean noErrorDetails;
        noErrorDetails = j.get("errorDetails") == null;
        if (j.get("id") == null || j.get("message") == null || j.get("timestamp") == null || j.get("thread") == null
        || j.get("logger") == null || j.get("level") == null) {
            resp.sendError(400);
            return;
        }
        if (j.get("id").isJsonNull() || j.get("message").isJsonNull() || j.get("timestamp").isJsonNull() || j.get("thread").isJsonNull()
                || j.get("logger").isJsonNull() || j.get("level").isJsonNull()) {
            resp.sendError(400);
            return;
        }
        if (noErrorDetails) {
            if (j.entrySet().size() != 6) {
                resp.sendError(400);
                return;
            }
        }
        else {
            if (j.entrySet().size() != 7) {
                resp.sendError(400);
                return;
            }
        }

        if (j.get("id").isJsonPrimitive()) {
            if (j.getAsJsonPrimitive().isString()) {
                Pattern UUID = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
                if(!UUID.matcher(j.get("id").toString()).matches()) {
                    resp.sendError(400);
                    return;
                }
                else {
                    if (logs.size() > 0) {
                        for (JsonObject jObj : logs) {
                            if(jObj.get("id").equals(j.get("id"))) {
                                resp.sendError(409);
                                return;
                            }
                        }
                    }
                }
            }
            else {
                resp.sendError(400);
                return;
            }
        }
        else {
            resp.sendError(400);
            return;
        }

        if (j.get("message").isJsonPrimitive() && j.get("thread").isJsonPrimitive() && j.get("logger").isJsonPrimitive()) {
            if (!j.get("message").getAsJsonPrimitive().isString() || !j.get("thread").getAsJsonPrimitive().isString() || !j.get("logger").getAsJsonPrimitive().isString()) {
                resp.sendError(400);
                return;
            }
        }
        else {
            resp.sendError(400);
            return;
        }

        if (j.get("timestamp").isJsonPrimitive()) {
            if (j.getAsJsonPrimitive().isString()) {
                if(!j.get("timestamp").toString().matches("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")) {
                    resp.sendError(400);
                    return;
                }
            }
            else {
                resp.sendError(400);
                return;
            }
        }
        else {
            resp.sendError(400);
            return;
        }

        boolean isValid = true;
        if (j.get("timestamp").isJsonPrimitive()) {
            if (j.getAsJsonPrimitive().isString()) {
                for (levels l : levels.values()) {
                    if(j.get("level").toString().equals(l.name())) {
                        isValid = true;
                        break;
                    }
                    isValid = false;
                }
                if (!isValid) {
                    resp.sendError(400);
                    return;
                }
            }
            else {
                resp.sendError(400);
                return;
            }
        }
        else {
            resp.sendError(400);
            return;
        }

        if (!noErrorDetails) {
            if (j.get("errorDetails").isJsonPrimitive()) {
                if (!j.getAsJsonPrimitive().isString()) {
                    resp.sendError(400);
                    return;
                }
            }
            else {
                resp.sendError(400);
                return;
            }
        }
        logs.add(j);
        resp.setStatus(201);
    }
}

//Maybe include descriptions with sendError() calls
