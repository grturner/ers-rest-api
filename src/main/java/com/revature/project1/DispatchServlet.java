package com.revature.project1;

import com.revature.project1.service.ReimbService;
import com.revature.project1.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DispatchServlet extends HttpServlet {

    public void init() {}

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String[]> params = request.getParameterMap();
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
        String jsonResponse = null;
        if (uriDecon.length >= 3) {
            switch (uriDecon[2]) {
                case "users":
                    jsonResponse = UserService.getInstance()
                                              .processGet(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), params);
                    break;
                case "reimbursements":
                    jsonResponse = ReimbService.getInstance()
                                               .processGet(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), params);
                    break;
                default:
                    response.sendError(404);
            }
        } else {
            response.sendError(404, "Path not found");
        }
        PrintWriter out = response.getWriter();
        Logger.getGlobal().log(Level.INFO, "DispatchServlet.doGet() is responding with: ".concat(jsonResponse));
        out.println(jsonResponse);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> params = request.getParameterMap();
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = br.readLine();
        int returnCode = -1;
        if (uriDecon.length >= 3) {
            switch(uriDecon[2]) {
                case "users":
                    returnCode = UserService.getInstance().processPost(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), params, json);
                    break;
                case "reimbursements":
                    java.util.logging.Logger.getGlobal().log(Level.INFO, "DispatchServlet:doPost entering ReimbService with JSON: ".concat(json));
                    returnCode = ReimbService.getInstance().processPost(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), params, json);
            }
        }
        response.setStatus(returnCode);
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Logger.getGlobal().log(Level.INFO, "DispatchServlet:doPut Starting.");
        int returnCode = -1;
        Map<String, String[]> params = request.getParameterMap();
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = br.readLine();
        if (uriDecon.length >= 3) {
            switch (uriDecon[2]) {
                case "reimbursements":
                    Logger.getGlobal().log(Level.INFO, "DispatchServet:doPut entereing ReimbService with JSON: ".concat(json));
                    returnCode = ReimbService.getInstance().processPut(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), params, json);
            }
        }
        response.setStatus(returnCode);
    }
}
