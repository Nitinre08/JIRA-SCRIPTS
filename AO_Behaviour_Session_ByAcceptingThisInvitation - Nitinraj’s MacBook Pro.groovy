package ANOP

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser

def cf_accptance = getFieldById(getFieldChanged())
def selectedOption = cf_accptance.getValue().toString()

def loggedInUser= ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
//def cfGuest = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Guests (Waiting for response)").first()
def cfvoter = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Voters (Waiting for response)").first()
def cfchair = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Chair (Waiting for response)").first()

if(underlyingIssue.getCustomFieldValue(cfvoter)){
    List<ApplicationUser> cfVotersvalue=underlyingIssue.getCustomFieldValue(cfvoter) as List<ApplicationUser> 
        if( cfVotersvalue.contains(loggedInUser)){
        if(!selectedOption.contains("I confirm that I have attended and passed the mandatory methodology training")){
            cf_accptance.setError("To proceed further, you must have all options ticked")
        }
        else{
          cf_accptance.clearError()  
        }
    }
}

if(underlyingIssue.getCustomFieldValue(cfchair)){
   ApplicationUser cfchairvalue = underlyingIssue.getCustomFieldValue(cfchair) as ApplicationUser
        if( cfchairvalue==loggedInUser){
        if(!selectedOption.contains("I confirm that I have attended and passed the mandatory methodology training")){
            cf_accptance.setError("To proceed further, you must have all options ticked")
        }
        else{
          cf_accptance.clearError()  
        }
    }
}

