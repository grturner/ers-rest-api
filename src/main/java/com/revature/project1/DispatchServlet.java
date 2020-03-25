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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String[]> params = request.getParameterMap();
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
        String jsonResponse = null;
        if (uriDecon.length >= 3) {
            switch (uriDecon[2]) {
                case "users":
                    UserService uService = new UserService();
                    jsonResponse = uService.processGet(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), params);
                    break;
                case "reimbursements":
                    ReimbService rService = new ReimbService();
                    jsonResponse = rService.processGet(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), params);
                    break;
                default:
                    response.sendError(404);
            }
        } else {
            response.sendError(404, "Path not found");
        }
        PrintWriter out = response.getWriter();
        out.println(jsonResponse);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> params = request.getParameterMap();
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = br.readLine();
        br.close();
        int returnCode = -1;
        String returnStr = "";
        if (uriDecon.length >= 3) {
            switch(uriDecon[2]) {
                case "users":
                    UserService uService = new UserService();
                    returnStr = uService.processPost(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), params, json);
                    if ( returnStr != null) {
                        if (returnStr.equals("200")) {
                            returnCode = 200;
                        } else {
                            returnCode = 200;
                            PrintWriter out = response.getWriter();
                            out.println(returnStr);
                        }
                    }
                    break;
                case "reimbursements":
                    Logger.getGlobal().log(Level.INFO,"DispatchServlet.doPost() entering reimbursement service");
                    ReimbService rService = new ReimbService();
                    returnCode = rService.processPost(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), params, json);
            }
        }
        if (returnCode == -1)
            response.sendError(404);
        else
            response.setStatus(returnCode);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int returnCode = -1;
        Map<String, String[]> params = request.getParameterMap();
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = br.readLine();
        br.close();
        if (uriDecon.length >= 3) {
            switch (uriDecon[2]) {
                case "reimbursements":
                    ReimbService service = new ReimbService();
                    returnCode = service.processPut(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), params, json);
            }
        }
        if (returnCode == -1)
            response.sendError(400);
        else
            response.setStatus(returnCode);
    }
}
