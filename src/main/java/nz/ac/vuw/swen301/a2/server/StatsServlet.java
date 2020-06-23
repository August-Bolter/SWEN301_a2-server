package nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StatsServlet extends HttpServlet {
    @Override
    /** Returns an html table of log statistics, the table has days represented as columns, and the rows represent loggers, log levels and threads. The cells at the intersection of rows and columns represent the number of log events for the respective category
     *  @param req The users request
     *  @param resp The servlets response */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (LogsServlet.logs != null) {
            if (LogsServlet.logs.size() > 0) {
                resp.setContentType("text/html"); //Since output will be a html website
                PrintWriter writer = resp.getWriter();

                /* Adding basic HTML structure */
                writer.append("<html>");
                writer.append("<head>");
                writer.append("<title>");
                writer.append("Log statistics");
                writer.append("</title>");
                writer.append("</head>");
                writer.append("<body>");
                writer.append("<table>");

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
                results[0][0] = "<tr>" + "<th>" + "name\t" + "</th>";

                /* Adding the timestamp to the CSV table */
                int index = 1;
                int logIndex = 0; //For determining whether we are at the end of the table
                for (String date : dates) {
                    if (logIndex + 1 == dates.size()) { //We are at the end of the table so add \n
                        results[index][0] = "<th>" + date + "</th>" + "</tr>"; //Adding the date to the CSV table
                    } else { //Otherwise add \t
                        results[index][0] = "<th>" + date + "</th>";
                    }
                    logIndex++;
                    index++;
                }

                /* Adding the logger to the CSV table */
                index = 1;
                for (String logger : rows) {
                    results[0][index] = "<tr>" + "<td>" + logger + "</td>"; //<td> = data
                    index++;
                    if (index-1 == loggerIndex) {
                        break;
                    }
                }

                /* Adding the level to the CSV table */
                for (String level : rows) {
                    results[0][index] = "<tr>" + "<td>" + level + "</td>";
                    index++;
                    if (index-1 == levelIndex) {
                        break;
                    }
                }

                /* Adding the thread to the CSV table */
                for (String thread : rows) {
                    results[0][index] = "<tr>" + "<td>" + thread + "</td>";
                    index++;
                    if (index-1 == threadIndex) {
                        break;
                    }
                }

                /* Determining CSV table values */
                for (JsonObject log : logs) {
                    /* Calculating the CSV value for the logger */
                    int row = rows.indexOf(log.get("logger").toString().substring(1, log.get("logger").toString().length() - 1)); //Getting row index of CSV table based off list
                    int col = dates.indexOf(log.get("timestamp").toString().substring(1, 11)); //Getting col index of CSV table based off list
                    /* If no value is previously in the cell then the value should be 1. */
                    if (results[col + 1][row + 1] == null) {
                        if (col + 1 == dates.size()) { //Checking if we are at the end of the CSV table
                            results[col + 1][row + 1] = "<td>" + "1" + "</td>" + "</tr>";
                        } else {
                            results[col + 1][row + 1] = "<td>" + "1" + "</td>";
                        }
                    }
                    /* Otherwise it should be old value + 1 */
                    else {
                        if (col + 1 == dates.size()) {
                            results[col + 1][row + 1] = "<td>" + (Integer.parseInt(results[col + 1][row + 1].replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</tr>", "")) + 1) + "</td>" + "</tr>";
                        } else {
                            results[col + 1][row + 1] = "<td>" + (Integer.parseInt(results[col + 1][row + 1].replaceAll("<td>", "").replaceAll("</td>", "")) + 1) + "</td>";
                        }
                    }

                    /* Calculating the CSV value for the level */
                    row = rows.indexOf(log.get("level").toString().substring(1, log.get("level").toString().length() - 1));
                    /* If no value is previously in the cell then the value should be 1. */
                    if (results[col + 1][row + 1] == null) {
                        if (col + 1 == dates.size()) { //Checking if we are at the end of the CSV table
                            results[col + 1][row + 1] = "<td>" + "1" + "</td>" + "</tr>";
                        } else {
                            results[col + 1][row + 1] = "<td>" + "1" + "</td>";
                        }
                    }
                    /* Otherwise it should be old value + 1 */
                    else {
                        if (col + 1 == dates.size()) {
                            results[col + 1][row + 1] = "<td>" + (Integer.parseInt(results[col + 1][row + 1].replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</tr>", "")) + 1) + "</td>" + "</tr>";
                        } else {
                            results[col + 1][row + 1] = "<td>" + (Integer.parseInt(results[col + 1][row + 1].replaceAll("<td>", "").replaceAll("</td>", "")) + 1) + "</td>";
                        }
                    }

                    /* Calculating the CSV value for the thread */
                    row = rows.indexOf(log.get("thread").toString().substring(1, log.get("thread").toString().length() - 1));
                    /* If no value is previously in the cell then the value should be 1. */
                    if (results[col + 1][row + 1] == null) {
                        if (col + 1 == dates.size()) { //Checking if we are at the end of the CSV table
                            results[col + 1][row + 1] = "<td>" + "1" + "</td>" + "</tr>";
                        } else {
                            results[col + 1][row + 1] = "<td>" + "1" + "</td>";
                        }
                    }
                    /* Otherwise it should be old value + 1 */
                    else {
                        if (col + 1 == dates.size()) {
                            results[col + 1][row + 1] = "<td>" + (Integer.parseInt(results[col + 1][row + 1].replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</tr>", "")) + 1) + "</td>" + "</tr>";
                        } else {
                            results[col + 1][row + 1] = "<td>" + (Integer.parseInt(results[col + 1][row + 1].replaceAll("<td>", "").replaceAll("</td>", "")) + 1) + "</td>";
                        }
                    }
                }

                /* Converting 2d array into String that doesn't include the array formatting (excludes the brackets and the commas) */
                String output = Arrays.stream(results).flatMap(Arrays::stream).collect(Collectors.joining());
                String formattedOutput = output.replaceAll("null", "0");
                writer.append(formattedOutput);
                /* Closing tags */
                writer.append("</table>");
                writer.append("</body>");
                writer.append("</html>");
                resp.setStatus(200);

            }
        }
    }
}
