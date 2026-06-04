package ANOP

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.option.Options
import com.atlassian.jira.user.ApplicationUser

def commnt=getFieldByName('By accepting this invitation, I confirm the following:')
//def customField = getFieldById(fieldChanged)

def cf = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(commnt.getFieldId())
def optionsManager = ComponentAccessor.getOptionsManager()

//Get list of options tied to the custom field
List<Options> options = optionsManager.getOptions(cf.getRelevantConfig(issueContext)) as List<Options>
List<Options> newoptions = new ArrayList()
newoptions.addAll(options)
//Filter option list with the condition specified
List<Options> optionsMap = options.findAll {
it.value == "I confirm that I have attended and passed the mandatory methodology training"
//it.value in("I have passed the necessary compliance trainings")
} as List<Options> 

newoptions.remove(optionsMap[0])


def loggedInUser= ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def cfGuest = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Guests (Waiting for response)").first()

if(null!=underlyingIssue.getCustomFieldValue(cfGuest)){
    List<ApplicationUser> cfVotersvalue=underlyingIssue.getCustomFieldValue(cfGuest) as List<ApplicationUser> 
    if(cfVotersvalue.contains(loggedInUser)){
        commnt.setFieldOptions(newoptions)
    }
}


if(getActionName() == "Change Participants")
{
    getFieldByName("Voters").setDescription("Delete the participants that you would like to remove from the session, then click \"Change Participants\".")
    getFieldByName("Guests").setDescription("Delete the participants that you would like to remove from the session, then click \"Change Participants\".")
    getFieldByName("Guests (Waiting for response)").setDescription("Delete the participants that you would like to remove from the session, then click \"Change Participants\".")
    getFieldByName("Voters (Waiting for response)").setDescription("Delete the participants that you would like to remove from the session, then click \"Change Participants\".")
}