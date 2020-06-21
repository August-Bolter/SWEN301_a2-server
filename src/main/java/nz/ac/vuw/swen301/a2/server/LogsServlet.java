package nz.ac.vuw.swen301.a2.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/** This class implements the doGet() and doPost() for a log event appender. Logs can be posted to the server using doPost()
 * and retrieved from the server using doGet() */
public class LogsServlet extends HttpServlet {

    public static List<JsonObject> logs; //Logs stored on the server
    enum levels { //All possible levels a log could have (ordered by priority)
        OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL
    }

    /** Creates a LogsServlet and initialises the logs list. */
    public LogsServlet() {
        logs = new ArrayList<JsonObject>();
    }

    @Override
    /** This method lets users request logs from the server, as part of the request the limit (amount of logs) and level
     * of the logs must be specified
     * @param req The users request
     * @param resp The servers response*/
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /* Check that limit and level are specified */
        if (req.getParameter("limit") == null || req.getParameter("level") == null) {
            resp.sendError(400);
            return;
        }
        int limit;
        try {
            limit = Integer.parseInt(req.getParameter("limit")); //Obtain the limit from the parameter
            if (limit < 0) { //Limit can't be negative
                resp.sendError(400);
                return;
            }
        }
        catch (NumberFormatException x) {
            resp.sendError(400); //Limit must be an integer
            return;
        }
        String level = req.getParameter("level");
        boolean isValid = true; //Is level a valid level (part of enum)
        /* Finding levels to consider based on level parameter given */
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
        /* If level isn't valid isn't then send 400 error */
        if (!isValid) {
            resp.sendError(400);
            return;
        }

        resp.setContentType("application/json"); //Content returned will be of type json
        PrintWriter out = resp.getWriter();

        int numLogs = 0; //Used to ensure that the number of logs returned doesn't exceed the limit specified
        List<JsonObject> output = new ArrayList<JsonObject>(limit);
        JsonArray jArray = new JsonArray();

        /* Get each log and make sure they meet the minimum required level */
        for (JsonObject j : logs) {
            for (String s : levelsToConsider) {
                if(j.get("level").toString().substring(1, j.get("level").toString().length()-1).equals(s)) {
                    if (numLogs != limit) { //If they do add them to the list and increase counter
                        output.add(j);
                        numLogs++;
                    }
                }
            }
        }
        /* Sort list of output logs by timestamp */
        output.sort((o1, o2) -> {
            try {
                Date dateO1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(o1.get("timestamp").toString().substring(1, o1.get("timestamp").toString().length()-1));
                Date dateO2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(o2.get("timestamp").toString().substring(1, o2.get("timestamp").toString().length()-1));
                if (dateO1.after(dateO2)) return -1;
                else if (dateO2.after(dateO1)) return 1;
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
        /* Number of logs being greater than 1 means a JsonArray must be returned not a JsonObject */
        if (output.size() > 1) {
            for (JsonObject j : output) {
                jArray.add(j);
            }
            out.print(jArray.toString());
        }
        /* Otherwise return a JsonObject */
        else if (output.size() == 1){
            out.print(output.get(0).toString());
        }
        out.close();
        resp.setStatus(200);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /* Checking that content type of post request is correct */
        if (req.getContentType() == null || !req.getContentType().startsWith("application/json")) {
            resp.sendError(400);
            return;
        }

        /* Converting body content into string */
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        /* Checking that content has been set */
        if(sb.toString().length() == 0) {
            resp.sendError(400);
            return;
        }

        Gson g = new Gson();
        /* Converting Json String to JsonObject */
        JsonObject j = g.fromJson(sb.toString(), JsonObject.class);
        boolean noErrorDetails; //Error details is an optional field (depends on level of log, and even then its still optional)
        noErrorDetails = j.get("errorDetails") == null;

        if (!noErrorDetails) { //If error details is present then check that it is a primitive (specifically a string)
            if (j.get("errorDetails").isJsonPrimitive()) {
                if (!j.get("errorDetails").getAsJsonPrimitive().isString()) {
                    if (resp.getStatus() != 400) { //Otherwise send response code 400
                        resp.sendError(400);
                    }
                    return;
                }
            }
            else {
                if (resp.getStatus() != 400) { //Otherwise send response code 400
                    resp.sendError(400);
                }
                return;
            }
        }

        /* Checking that the rest of the JsonObject fields exist */
        if (j.get("id") == null || j.get("message") == null || j.get("timestamp") == null || j.get("thread") == null
        || j.get("logger") == null || j.get("level") == null) {
            if (resp.getStatus() != 400) {
                resp.sendError(400);
            }
            return;
        }
        /* Checking that the rest of the JsonObject fields aren't JsonNull (null) */
        if (j.get("id").isJsonNull() || j.get("message").isJsonNull() || j.get("timestamp").isJsonNull() || j.get("thread").isJsonNull()
                || j.get("logger").isJsonNull() || j.get("level").isJsonNull()) {
            if (resp.getStatus() != 400) {
                resp.sendError(400);
            }
            return;
        }
        /* Checking that the JsonObject has no extra fields */
        if (noErrorDetails) {
            if (j.entrySet().size() != 6) {
                if (resp.getStatus() != 400) {
                    resp.sendError(400);
                }
                return;
            }
        }
        else {
            if (j.entrySet().size() != 7) {
                if (resp.getStatus() != 400) {
                    resp.sendError(400);
                }
                return;
            }
        }

        /* Checking that JsonObject fields are primitives (specifically strings) */
        if (j.get("id").isJsonPrimitive() && j.get("message").isJsonPrimitive() && j.get("timestamp").isJsonPrimitive() && j.get("thread").isJsonPrimitive() && j.get("logger").isJsonPrimitive() && j.get("level").isJsonPrimitive()) {
            if (!j.get("id").getAsJsonPrimitive().isString() || !j.get("message").getAsJsonPrimitive().isString() || !j.get("timestamp").getAsJsonPrimitive().isString() || !j.get("thread").getAsJsonPrimitive().isString() || !j.get("logger").getAsJsonPrimitive().isString() || !j.get("level").getAsJsonPrimitive().isString()) {
                if (resp.getStatus() != 400) {
                    resp.sendError(400);
                }
                return;
            }
        }
        else {
            if (resp.getStatus() != 400) {
                resp.sendError(400);
            }
            return;
        }
        /* Checking that id follows the specified UUID pattern */
        Pattern UUID = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"); //specified UUID pattern
        if(!UUID.matcher(j.get("id").toString().substring(1, j.get("id").toString().length()-1)).matches()) { //Have to use substring() to remove the leading and trailing "
            if (resp.getStatus() != 400) {
                resp.sendError(400);
            }
            return;
        }
        else {
            /* If id follows UUID pattern then check that it also doesn't already exist in the server database */
            if (logs.size() > 0) {
                for (JsonObject jObj : logs) {
                    if(jObj.get("id").toString().equals(j.get("id").toString())) {
                        if (resp.getStatus() != 400) {
                            resp.sendError(409);
                        }
                        return;
                    }
                }
            }
        }
        /* Checking that the timestamp follows the specified date format */
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            format.parse(j.get("timestamp").toString().substring(1, j.get("timestamp").toString().length()-1));
        } catch (ParseException e) { //If parsing fails that means timestamp doesn't follow the format
            if (resp.getStatus() != 400) {
                resp.sendError(400);
            }
        }

        boolean isValid = true;
        /* Checking that level field of JsonObject is part of the enum */
        for (levels l : levels.values()) {
            if(j.get("level").toString().substring(1, j.get("level").toString().length()-1).equals(l.name())) {
                isValid = true;
                break;
            }
            isValid = false;
        }
        if (!isValid) {
            if (resp.getStatus() != 400) {
                resp.sendError(400);
            }
            return;
        }

        if (resp.getStatus() != 400 && resp.getStatus() != 409) { //We don't want to send more than one status/error
            logs.add(j); //If log is completely valid (passes all the above checks) then add it to the list
            logs.sort((o1, o2) -> { //Sort logs by timestamp (this is to guarantee logs are sorted by timestamp when logs returned in doGet() are limited by limit parameter)
                try {
                    Date dateO1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(o1.get("timestamp").toString().substring(1, o1.get("timestamp").toString().length()-1));
                    Date dateO2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(o2.get("timestamp").toString().substring(1, o2.get("timestamp").toString().length()-1));
                    if (dateO1.after(dateO2)) return -1;
                    else if (dateO2.after(dateO1)) return 1;
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            });
            resp.setStatus(201); //Assume an error hasn't occurred previously then send success code
        }
    }
}
