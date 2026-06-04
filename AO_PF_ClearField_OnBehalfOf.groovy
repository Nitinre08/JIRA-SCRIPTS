import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.event.type.EventDispatchOption

def onBhf= ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("On Behalf of").first()

issue.setCustomFieldValue(onBhf,null)
ComponentAccessor.getIssueManager().updateIssue(Users.getLoggedInUser(), issue, EventDispatchOption.DO_NOT_DISPATCH, false)

issue.reindex()