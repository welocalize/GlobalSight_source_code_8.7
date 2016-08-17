<%@ page 
    contentType="text/html; charset=UTF-8"
    errorPage="/envoy/common/error.jsp"
    import="com.globalsight.everest.webapp.webnavigation.LinkHelper,
        com.globalsight.everest.webapp.pagehandler.PageHandler,
        com.globalsight.everest.webapp.WebAppConstants,
        com.globalsight.everest.servlet.util.SessionManager,
        com.globalsight.util.edit.EditUtil,
        java.util.*"
    session="true"
%>
<%@ include file="/envoy/common/header.jspIncl" %>
<%@ include file="/envoy/common/navigation.jspIncl" %>
<%@ include file="/envoy/wizards/guides.jspIncl" %>
<%@ include file="/includes/compatibility.jspIncl" %>
<jsp:useBean id="next" scope="request"
 class="com.globalsight.everest.webapp.javabean.NavigationBean"/>
 <jsp:useBean id="cancel" scope="request"
 class="com.globalsight.everest.webapp.javabean.NavigationBean" />
<jsp:useBean id="refresh" scope="request"
 class="com.globalsight.everest.webapp.javabean.NavigationBean" />
<%
ResourceBundle bundle = PageHandler.getBundle(session);
SessionManager sessionMgr = (SessionManager)session.getAttribute(
  WebAppConstants.SESSION_MANAGER);

String urlNext = next.getPageURL();
String urlRefresh = refresh.getPageURL() +
    "&" + WebAppConstants.TERMBASE_ACTION +
    "=" + WebAppConstants.TERMBASE_ACTION_REFRESH;
String urlCancel = cancel.getPageURL() +
    "&" + WebAppConstants.TERMBASE_ACTION +
    "=" + WebAppConstants.TERMBASE_ACTION_CANCEL_IMPORT_TEST;

// Perform error handling, then clear out session attribute.
String errorScript = "";
String error = (String)sessionMgr.getAttribute(WebAppConstants.TERMBASE_ERROR);
if (error != null)
{
  errorScript = "var error = new Error();" +
    "error.message = '" + EditUtil.toJavascript(bundle.getString("lb_import_error")) + "';" + 
    "error.description = '" + EditUtil.toJavascript(error) +
    "'; showError(error);";
}
sessionMgr.removeElement(WebAppConstants.TERMBASE_ERROR);
%>
<HTML>
<HEAD>
<TITLE><%=bundle.getString("lb_snippet_testing_import")%></TITLE>
<STYLE>
#idProgressContainer { border: solid 1px <%=skin.getProperty("skin.list.borderColor")%>; z-index: 1; 
                 position: absolute; top: 42; left: 20; width: 400; }
#idProgressBar { background-color: #a6b8ce; z-index: 0;
                 border: solid 1px <%=skin.getProperty("skin.list.borderColor")%>; 
                 position: absolute; top: 42; left: 20; width: 0; }
#idProgress    { text-align: center; z-index: 2; font-weight: bold; }
#idMessagesHeader { position: absolute; top: 72; left: 20; font-weight: bold;}
#idMessages    { position: absolute; overflow: auto; z-index: 0; 
                 top: 102; left: 20; height: 80; width: 400; }
#idLinks       { position: absolute; left: 20; top: 314; z-index: 1; }
</STYLE>
<SCRIPT SRC="/globalsight/includes/setStyleSheet.js"></SCRIPT>
<%@ include file="/envoy/wizards/guidesJavascript.jspIncl" %>
<%@ include file="/envoy/common/warning.jspIncl" %>
<SCRIPT SRC="envoy/terminology/management/protocol.js"></SCRIPT>
<SCRIPT>
var needWarning = false;
var objectName = "";
var guideNode = "snippetimport";
var helpFile = "<%=bundle.getString("help_import_snippet_files2")%>";

var WIDTH = 400;

eval("<%=errorScript%>");

function showMessage(message)
{
    var div = document.createElement("DIV");
    div.innerHTML = message;
    idMessages.appendChild(div);

    if (idMessages.style.pixelHeight < 80)
    {
      idMessages.style.pixelHeight = 80;
    }

    idMessages.style.pixelHeight += 40;

    if (idMessages.style.pixelHeight > 200)
    {
      idMessages.style.pixelHeight = 200;
    }

    div.scrollIntoView(false);
}

function showProgress(entryCount, percentage, message)
{
  idProgress.innerText = entryCount.toString(10) + " snippets (" +
    percentage.toString(10) + "%)";

  //idProgressBar.style.pixelWidth = Math.round((percentage / 100) * WIDTH);
  idProgressBar.style.width = Math.round((percentage / 100) * WIDTH);
  idProgressBar.style.height = 15;
  idProgressBar.innerHTML='&nbsp';
  
  if (message != null && message != "")
  {
    showMessage(message);
  }
}

function copyToClipboard()
{
  var range = document.body.createTextRange();
  range.moveToElementText(idMessages);
  window.clipboardData.setData("Text", range.text);
  window.status = "Messages copied to clipboard.";
}

function doCancel()
{
    var url = "<%=urlCancel%>";
        
    url += "&<%=WebAppConstants.TERMBASE_ACTION%>" +
           "=<%=WebAppConstants.TERMBASE_ACTION_SET_IMPORT_OPTIONS%>";

    // don't allow back button to move to this page
    window.location.href = "<%=urlCancel%>";
}

function doNext()
{
    var url = "<%=urlNext%>";
        
    url += "&<%=WebAppConstants.TERMBASE_ACTION%>" +
           "=<%=WebAppConstants.TERMBASE_ACTION_START_IMPORT%>";

    window.location.href = url;
}

function doRefresh()
{
    window.location.href = "<%=urlRefresh%>";
}

function done()
{
  idPleaseWait.style.visibility = 'hidden';

  // Cancel button remains cancel
  //idCancelOk.value = "";
  //idCancelOk.onclick = doCancel;

  idRefreshResult.value = "Continue with import";
  idRefreshResult.onclick = doNext;
  idRefreshResult.focus();
}
</SCRIPT>
<%@ include file="/envoy/common/shortcutIcon.jspIncl" %>
</HEAD>
<BODY LEFTMARGIN="0" RIGHTMARGIN="0" TOPMARGIN="0" MARGINWIDTH="0"
  MARGINHEIGHT="0" CLASS="standardText" oncontextmenu="return false"
  ONLOAD="loadGuides()">

<DIV ID="contentLayer" 
    STYLE=" POSITION: ABSOLUTE; Z-INDEX: 8; TOP: 108px; LEFT: 20px; RIGHT: 20px;">

<SPAN CLASS="mainHeading" id="idHeading"><%=bundle.getString("lb_snippet_testing_import2")%></SPAN><BR/>
<SPAN CLASS="standardTextItalic" id="idPleaseWait">
  <%=bundle.getString("msg_snippet_testing")%>
</SPAN>

<DIV id="idProgressContainer">
  <DIV id="idProgress">0%</DIV>
</DIV>
<DIV id="idProgressBar"></DIV>

<BR>
<DIV id="idMessagesHeader" class="header"><%=bundle.getString("lb_import_msg")%></DIV>
<DIV id="idMessages"></DIV>

<DIV id="idLinks">
  <INPUT TYPE="BUTTON" VALUE="<%=bundle.getString("lb_cancel")%>"
   id="idCancelOk" onclick="doCancel()"> 
  <INPUT TYPE="BUTTON" VALUE="<%=bundle.getString("lb_refresh")%>"
   id="idRefreshResult" onclick="doRefresh()"> 
</DIV>

<IFRAME id="idFrame" src="<%=urlRefresh%>" style="display:none"></IFRAME>

</DIV>
</BODY>
</HTML>
