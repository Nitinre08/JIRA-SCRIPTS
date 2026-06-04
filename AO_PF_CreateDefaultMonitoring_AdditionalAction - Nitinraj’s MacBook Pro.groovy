package ANOP


import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor

def cfLA=ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Lead Analyst").first()
def val_LA= sourceIssue.getCustomFieldValue(cfLA) as ApplicationUser
issue.summary = 'Post Withdrawal Default Monitoring - ' + sourceIssue.getSummary()
Date today= new Date()
def newdate=today
newdate.setDate(15)
newdate.setMonth(0)
newdate.setYear(today.getYear()+1)
issue.setDueDate(newdate.toTimestamp())
issue.setReporter(val_LA)