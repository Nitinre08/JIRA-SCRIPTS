package ANOP
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.worklog.WorklogImpl2
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.user.ApplicationUser
import groovy.transform.Field
/*
Niharika Sarabu
*/
  
//@Field MutableIssue issue = event.issue as MutableIssue
def laCF = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_11709")
def votersCF = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_12330")
def chairCF = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_22700") //PACR
def guestsCF = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_12308")
def worklogAddedCF = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_22201")
  
@Field CustomField cfSpentbyJr = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_21505")
@Field CustomField cfSpentbySr =ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_21506")
@Field Date latestMeetingDate
@Field String logMonth
@Field MutableIssue issueIO
@Field MutableIssue issue

issue = event.issue as MutableIssue // Issues.getByKey("") as MutableIssue
log.warn("Event Triggered by Issue - " + issue.key)
def cfBulkCommittee = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_24001")
@Field def timeSplit = 1

if(issue.getCustomFieldValue(cfBulkCommittee) != null)
{
    //List of Session tickets in JQL
    String strJQL = "type=\"Bulk Committee Action\" and issueFunction in linkedIssuesOf(\"key=$issue.key\")"
    def issueBulkCommittee = Issues.getByKey(Issues.search(strJQL)[0].key) as MutableIssue
    log.warn("Bulk Committee ticket is - "  + issueBulkCommittee)
    String strSessionCountJQL = "issueFunction in linkedIssuesOf(\"key = $issueBulkCommittee.key\") and type = \"Session\""
    log.warn(strSessionCountJQL)
    timeSplit = Issues.search(strSessionCountJQL).size()
    log.warn(timeSplit)
}
log.warn("TimeSplit is - " + timeSplit)

/*Issues.search(strJQL).each {
    iss -> iss
  
    //def issueIO = issue.parentObject.getInwardLinks().find { t -> t.getSourceObject().issueType.name.startsWith("IO ")}.sourceObject as MutableIssue
  
    issue = Issues.getByKey(iss.getKey()) as MutableIssue */
  
if(!(issue.issueType.name == "Session" || issue.issueType.name == "Meeting session"))
{
    return;
}
log.warn("Ticket $issue.issueType.name has been closed - $issue.key")
if(issue.getCustomFieldValue(worklogAddedCF).toString() == "Yes")
{
    log.warn("Worklog is already added to this Session - " + issue.key)
    return
}  
latestMeetingDate = issue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_18500"))
if(latestMeetingDate != null)
{
    logMonth = "Rating Efforts - " + (latestMeetingDate as Date).toMonth().toString() + "-" + (latestMeetingDate as Date).toYear()
    log.warn("Log Month - " + logMonth)
}
else
{
    log.warn("Latest Meeting Date is EMPTY , so no work log added for the session:" + issue.getKey())
    return;
}
log.warn("Latest Meeting Date - " + latestMeetingDate.toString())


@Field ArrayList loggedUsers = new ArrayList<ApplicationUser>()
loggedUsers = []
log.warn(loggedUsers)

//JM-2472
def referenceDate = new Date("06/01/2025")
if(latestMeetingDate.compareTo(referenceDate) >= 0)
{
    log.warn("Latest Meeting Date is post/on Jun.01.2025")
    loggedUsers.add(issue.getCustomFieldValue(laCF) as ApplicationUser)
    loggedUsers.add(issue.getCustomFieldValue(chairCF) as ApplicationUser)
}
log.warn("Logged Users -" + loggedUsers)

issueIO = issue.parentObject.getInwardLinks().find { t -> t.getSourceObject().issueType.name.startsWith("IO ")}.sourceObject as MutableIssue
log.warn("Internal Order - " + issueIO.getKey())
def mettingType= ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Meeting Type").first()
def valMttingType= issue.parentObject.getCustomFieldValue(mettingType)

if(issue.parentObject.issueType.name == "Committee" || valMttingType.toString().equalsIgnoreCase("Committee"))
{
    getTicketsInfoAndLog(laCF, 270)
    getTicketsInfoAndLog(chairCF, 270)
    getTicketsInfoAndLog(votersCF, 120)
    getTicketsInfoAndLog(guestsCF, 90)
}
if(issue.parentObject.issueType.name == "Monitoring Review" || valMttingType.toString().equalsIgnoreCase("Monitoring Review"))
{
    getTicketsInfoAndLog(laCF, 180)
    getTicketsInfoAndLog(chairCF, 180)
    getTicketsInfoAndLog(votersCF, 90)
    getTicketsInfoAndLog(guestsCF, 60)
}
if(issue.parentObject.issueType.name == "Discussion" || valMttingType.toString().equalsIgnoreCase("Discussion"))
{
    getTicketsInfoAndLog(laCF, 120)
    getTicketsInfoAndLog(chairCF, 90)
    getTicketsInfoAndLog(votersCF, 90)
    getTicketsInfoAndLog(guestsCF, 60)
}

fixTimeSpents()
issue.setCustomFieldValue(worklogAddedCF,"Yes")
ComponentAccessor.getIssueManager().updateIssue(issue.assignee, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
issue.reindex()
//}
//Instructions to fix the Time Spent Jr & Time Spent Sr field values as per all worklogs
  
public void getTicketsInfoAndLog(CustomField customFieldObj, int logTime)
{
    log.warn("Inside getTicketsInfoAndLog...")
    def logAssignee = issue.getCustomFieldValue(customFieldObj)
    for(user in logAssignee)
    {
        user = user as ApplicationUser
        log.warn(user.getUsername().toString())
        log.warn(loggedUsers.contains(user))
        if(user.getUsername().toString().contains("committee"))
            continue;
        if(!loggedUsers.contains(user))
        {
            def issueSRTT = getSRTTIssue(user)
            if(issueSRTT != null)
            {
                userWorkLog(issueSRTT, issueIO, logTime, latestMeetingDate)
                loggedUsers.add(user)
            }
            else
            {
                log.warn("No SRTT Ticket found for : " + user.getUsername())
            }
        }
        else
        {
            log.warn("User worklog is already added/will be added manually - " + user)
        }
    }
}
  
public MutableIssue getSRTTIssue(ApplicationUser userToLog)
{
    String jql = "project = SRTT and issuetype = Sub-task and summary ~ '$logMonth' and assignee = " + userToLog.getUsername()
    log.warn(jql)
    def searchService = ComponentAccessor.getComponent(SearchService)
    def queryParser = ComponentAccessor.getComponent(JqlQueryParser)
    def sb = new StringBuffer()
    def user = ComponentAccessor.getUserManager().getUserByName("SA_Jira")
    //your query goes here
    def query = queryParser.parseQuery("project = SRTT and issuetype = Sub-task and summary ~ '$logMonth' and assignee = $userToLog.username")
  
    //gets results of query
    def search = searchService.search(user, query, PagerFilter.getUnlimitedFilter())
  
    log.warn(search.results)
    if(search.results.size() > 0)
    {
        log.warn("SRTT Issue Key - " + search.results.first().key)
        return (ComponentAccessor.getIssueManager().getIssueObject(search.results.first().key))
    }
    else
        return null
  
    /*
    log.warn(Issues.count(jql))
    if(Issues.count(jql) > 0)
    {
        log.warn("SRTT Issue Key - " + Issues.search(jql)[0].getKey())
        return (Issues.getByKey(Issues.search(jql)[0].getKey()) as MutableIssue) //ComponentAccessor.getIssueManager().getIssueObject(Issues.search(jql)[0].getKey())
    }
    else
        return null */
}
  
public void userWorkLog(MutableIssue issue,MutableIssue issueToLog,int logMinutes,def latestMeetingDate)
{
    def worklogManager = ComponentAccessor.getWorklogManager();
  
    Long timeSpent = ((logMinutes*60) as Integer)/timeSplit as Long
    log.warn("Time Spent after split $issue.assignee.displayName - " + timeSpent)
    Long cfOriginalEst = timeSpent
    if(issueToLog.getOriginalEstimate()!= null)
    {
        cfOriginalEst= issueToLog.getOriginalEstimate()
    }
  
    //log.warn(issueToLog.getTimeSpent() + timeSpent)
    def worklog = new WorklogImpl2(issue, null, issue.assignee.key,"Automatic Tracking - Time Spent on $issueToLog $issueToLog.summary", (latestMeetingDate as Date) , null, null, timeSpent , null)
    def worklog2 = new WorklogImpl2(issueToLog, null, issue.assignee.key, "Automatic Tracking - Time Spent on Session/Meeting Session", (latestMeetingDate as Date) , null, null, timeSpent, null)
    worklogManager.create(issue.assignee, worklog, issue.getOriginalEstimate(), true)
    worklogManager.create(issue.assignee, worklog2, (cfOriginalEst-timeSpent), true)
      
    //To update the total TimeSpent
    issue.setTimeSpent(issue.getTimeSpent() == null ? timeSpent : issue.getTimeSpent() + timeSpent)
    issueToLog.setTimeSpent(issueToLog.getTimeSpent() == null ? timeSpent : issueToLog.getTimeSpent() + timeSpent)
  
    // To update the Time Spent By Juniors & Time Spent by Seniors fields in both IO & SRTT
    Double val_timeSpentJr= issueToLog.getCustomFieldValue(cfSpentbyJr) as Double
    Double val_timeSpentSr= issueToLog.getCustomFieldValue(cfSpentbySr) as Double
  
    Double val_timeSpentJr_SRTT= issue.getCustomFieldValue(cfSpentbyJr) as Double
    Double val_timeSpentSr_SRTT= issue.getCustomFieldValue(cfSpentbySr) as Double
  
    log.warn("Junior Time - " + val_timeSpentJr)
    log.warn("Senior Time - " + val_timeSpentSr)
    if(issue.assignee.active)
    {
        if(issue.assignee.isMemberOfGroup("L_4_Professional") || issue.assignee.isMemberOfGroup("L_5_Junior"))
        {
            log.warn("Junior")
            issue.setCustomFieldValue(cfSpentbyJr, val_timeSpentJr_SRTT == null ? Double.parseDouble(timeSpent.toString()) : val_timeSpentJr_SRTT + Double.parseDouble(timeSpent.toString()))
            issueToLog.setCustomFieldValue(cfSpentbyJr, val_timeSpentJr == null ? Double.parseDouble(timeSpent.toString()) : val_timeSpentJr + Double.parseDouble(timeSpent.toString()))
        }
        else
        {
            log.warn("senior")
            issue.setCustomFieldValue(cfSpentbySr, val_timeSpentSr_SRTT == null ? Double.parseDouble(timeSpent.toString()) : val_timeSpentSr_SRTT + Double.parseDouble(timeSpent.toString()))
            issueToLog.setCustomFieldValue(cfSpentbySr, val_timeSpentSr == null ? Double.parseDouble(timeSpent.toString()) : val_timeSpentSr + Double.parseDouble(timeSpent.toString()))
          
        }
        log.warn("Junior New Value - " + issueToLog.getCustomFieldValue(cfSpentbyJr))
        log.warn("Senior New Value - " + issueToLog.getCustomFieldValue(cfSpentbySr))
    }
      
    ComponentAccessor.getIssueManager().updateIssue(issue.assignee, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
    ComponentAccessor.getIssueManager().updateIssue(issue.assignee, issueToLog, EventDispatchOption.DO_NOT_DISPATCH, false)
  
    log.warn("Work log added in : " + issue.getKey())
    log.warn("Work log added in : " + issueToLog.getKey())
  
    issue.reindex()
    issueToLog.reindex()
}

  
public void fixTimeSpents()
{
    log.warn("Current Total Time Spent - " + issueIO.getTimeSpent())
  
    Long tobeaddedTimeSpent
    def allWorkLogs = ComponentAccessor.getWorklogManager().getByIssue(issueIO)
    Long sumOfAllWLs = 0
    def issueToLog = issueIO
  
    Double val_timeSpentJr= 0;
    Double val_timeSpentSr= 0;
    for(wl in allWorkLogs)
    {
        log.warn(wl.getTimeSpent())
        def timeSpent = wl.getTimeSpent()
        log.warn(wl.authorObject)
        if(wl.authorObject == null)
        {
            log.warn("User is null.")
            continue;
        }
        if(!wl.authorObject.active)
        {
            log.warn("User $wl.authorObject.displayName is inactive, so no adjustment in TimeSpent.")
            return
        }
        if(wl.comment.contains("Adjusting Total time spent"))
        {
            log.warn("Adjusting Time Spent doesn't need to be calculated")
            continue;
        }
          
        sumOfAllWLs = sumOfAllWLs + wl.getTimeSpent()
        if(wl.authorObject.isMemberOfGroup("L_4_Professional") || wl.authorObject.isMemberOfGroup("L_5_Junior"))
        {
            val_timeSpentJr = val_timeSpentJr == null ? Double.parseDouble(timeSpent.toString()) : val_timeSpentJr + Double.parseDouble(timeSpent.toString())
        }
        else
        {
            val_timeSpentSr = val_timeSpentSr == null ? Double.parseDouble(timeSpent.toString()) : val_timeSpentSr + Double.parseDouble(timeSpent.toString())
           }
  
    }
  
    log.warn("Junior New Value - " + val_timeSpentJr)
    log.warn("Senior New Value - " + val_timeSpentSr)
    if(issueIO.getCustomFieldValue(cfSpentbyJr) as Double != val_timeSpentJr as Double && val_timeSpentJr != 0 )
    {
        log.warn("Updating Junior Time Spent from - " + issueIO.getCustomFieldValue(cfSpentbyJr) + " to - " + val_timeSpentJr)
        issueToLog.setCustomFieldValue(cfSpentbyJr, val_timeSpentJr)
    }
    if(issueIO.getCustomFieldValue(cfSpentbySr) as Double != val_timeSpentSr as Double && val_timeSpentSr != 0)
    {
        log.warn("Updating Senior Time Spent - " + issueIO.getCustomFieldValue(cfSpentbySr) + " to -" + val_timeSpentSr)
        issueToLog.setCustomFieldValue(cfSpentbySr, val_timeSpentSr)
    }
          
    log.warn("Sum of All Workflogs - " + sumOfAllWLs)
  
    tobeaddedTimeSpent = sumOfAllWLs - issueIO.getTimeSpent()
    log.warn("Tobe Added Total Time Spent - " + tobeaddedTimeSpent)
    if(tobeaddedTimeSpent > 0)
    {
        def worklog = new WorklogImpl2(issueIO, null, issueIO.assignee.key, "Automatic Time Tracking - Adjusting Total time spent", (latestMeetingDate as Date) , null, null, tobeaddedTimeSpent, null)
        ComponentAccessor.getWorklogManager().create(issueIO.assignee, worklog, issueIO.getOriginalEstimate(), true)
        issueIO.setTimeSpent(issueIO.getTimeSpent() + (tobeaddedTimeSpent as Long))
  
        log.warn("New Total Time Spent - " + issueIO.getTimeSpent())
    }
    else
    {
        log.warn("Time Spent is uptodate - " + issueIO.getTimeSpent())
    }
  
    ComponentAccessor.getIssueManager().updateIssue(issueIO.assignee , issueIO, EventDispatchOption.DO_NOT_DISPATCH, false)
    issueIO.reindex()
}
