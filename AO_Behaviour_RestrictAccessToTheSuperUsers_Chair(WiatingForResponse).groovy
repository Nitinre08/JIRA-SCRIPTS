package ANOP

import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor

def CfchairWaiting= ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Chair (Waiting for response)").first()
def currentField= getFieldById(getFieldChanged()) 
if(null!=underlyingIssue.getCustomFieldValue(CfchairWaiting)) {
    currentField.setReadOnly(false)

    ApplicationUser chairVal= underlyingIssue.getCustomFieldValue(CfchairWaiting) as ApplicationUser
        
    def currentValue = currentField.getValue().toString()
    
    if(currentValue==null ||currentValue=="null" ){
            currentField.clearError() 
        }

    else if(!chairVal.getUsername().equalsIgnoreCase(currentValue)){
        currentField.setError("Adding of new Chair is not Possible here, Use this only for Removal of Chair")
    }
    
   
} 

else{
    currentField.setReadOnly(true)
}