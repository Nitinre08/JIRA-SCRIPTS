package ANOP

import com.atlassian.jira.issue.MutableIssue

if(getActionName() == "Create")
{
    def issue = Issues.getByKey(getFieldByName("Related Monitoring IO").value.toString()) as MutableIssue
    getFieldByName("Publication type").setFormValue(issue.getCustomFieldValue("Publication type").getValue())
    getFieldByName("Publication type").setReadOnly(true)
}