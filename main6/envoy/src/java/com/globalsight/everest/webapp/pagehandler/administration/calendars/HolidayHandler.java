/**
 *  Copyright 2009 Welocalize, Inc. 
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  
 *  You may obtain a copy of the License at 
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */
package com.globalsight.everest.webapp.pagehandler.administration.calendars;

import com.globalsight.everest.servlet.EnvoyServletException;
import com.globalsight.everest.servlet.util.ServerProxy;
import com.globalsight.everest.servlet.util.SessionManager;
import com.globalsight.everest.util.comparator.HolidayComparator;
import com.globalsight.everest.util.system.SystemConfigParamNames;
import com.globalsight.everest.util.system.SystemConfiguration;
import com.globalsight.everest.webapp.WebAppConstants;
import com.globalsight.everest.webapp.pagehandler.ControlFlowHelper;
import com.globalsight.everest.webapp.pagehandler.PageHandler;
import com.globalsight.everest.webapp.webnavigation.WebPageDescriptor;
import com.globalsight.calendar.Holiday;
import com.globalsight.util.GeneralException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class HolidayHandler extends PageHandler
    implements HolidayConstants
{
    private int m_holidaysPerPage = 10;
    
    public HolidayHandler()
    {
        try 
        {
            SystemConfiguration sysconfig = SystemConfiguration.getInstance();
            m_holidaysPerPage = sysconfig.getIntParameter(
                SystemConfigParamNames.CALENDERING_DISPLAY_PER_PAGE);
        }
        catch (Exception e)
        {
            // will use initialized defaults
        }
    }

    /**
     * Invokes this PageHandler
     *
     * @param p_pageDescriptor the page desciptor
     * @param p_request the original request sent from the browser
     * @param p_response the original response object
     * @param p_context context the Servlet context
     */
    public void invokePageHandler(WebPageDescriptor pageDescriptor,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  ServletContext context)
    throws ServletException, IOException,
        EnvoyServletException
    {
        HttpSession session = request.getSession(false);

        String action = request.getParameter(ACTION);
        
        if (REMOVE_HOL_ACTION.equals(action)) 
        {
            removeHoliday(request);
        }
        else if (SAVE_ACTION.equals(action)) 
        {
            SessionManager sessionMgr =
                (SessionManager)session.getAttribute(SESSION_MANAGER);
            if (request.getParameter("edit") != null)
            {
                saveHoliday(request, session);
                clearSessionExceptTableInfo(session, HOLIDAY_KEY);
            }
            else
            {
                createHoliday(request, session);
                clearSessionExceptTableInfo(session, HOLIDAY_KEY);
            }
        }
        initHolidayTable(request, session);
        // Call parent invokePageHandler() to set link beans and invoke JSP
        super.invokePageHandler(pageDescriptor, request,
                                response, context);
    }

    /**
     * Overide getControlFlowHelper so we can do processing
     * and redirect the user correctly.
     *
     * @return the name of the link to follow
     */
    public ControlFlowHelper getControlFlowHelper(
        HttpServletRequest p_request, HttpServletResponse p_response)
    {

        return new CalendarControlFlowHelper(p_request, p_response);
    }


    private void initHolidayTable(HttpServletRequest request,
                                     HttpSession session)
    throws EnvoyServletException
    {
        // Get list of holidays
        List holidays = CalendarHelper.getAllHolidays();

        Locale uiLocale = (Locale)session.getAttribute(
            WebAppConstants.UILOCALE);
        setTableNavigation(request, session, holidays, 
                            new HolidayComparator(uiLocale),
                            m_holidaysPerPage,
                            HOLIDAY_LIST, HOLIDAY_KEY);

    }


    /**
     * Save changes made to the holiday
     */
    private void saveHoliday(HttpServletRequest request, HttpSession session)
    throws EnvoyServletException
    {
        SessionManager sessionMgr =
            (SessionManager)session.getAttribute(SESSION_MANAGER);
        String id = (String)sessionMgr.getAttribute("id"); 
        CalendarHelper.modifyHoliday(request, session, Long.parseLong(id));
    }

    private void createHoliday(HttpServletRequest request, HttpSession session)
    throws EnvoyServletException
    {
        CalendarHelper.createHoliday(request, session);
    }

    private void removeHoliday(HttpServletRequest request)
    throws EnvoyServletException
    {
        String id = (String)request.getParameter("id");
        CalendarHelper.removeHoliday(Long.parseLong(id));
    }

}