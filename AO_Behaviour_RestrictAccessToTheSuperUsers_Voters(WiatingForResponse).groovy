package ANOP

import java.util.ArrayList
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor

def CfvotersWating= ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Voters (Waiting for response)").first()
def currentField= getFieldById(getFieldChanged()) 
if(null!=underlyingIssue.getCustomFieldValue(CfvotersWating)) {
    currentField.setReadOnly(false)

    List<ApplicationUser> votersVal= underlyingIssue.getCustomFieldValue(CfvotersWating) as List<ApplicationUser>
    List<String> voterUsers= new ArrayList()
    votersVal.each { 
        voterUsers.add(it.getUsername())
    }
    
    def currentValue = currentField.getValue()
    
    if(currentValue.class.typeName == "java.lang.String"){
        if(!voterUsers.contains(currentValue)){
            currentField.setError("Adding of new Voters is not Possible here, Use this only for Removal of Voters")
        }
        if(currentValue==null ||currentValue=="" ){
                currentField.clearError() 
            }
    }
    
   else{
        currentValue.each{
            if(!voterUsers.contains(it)){
             currentField.setError("Adding of new Voters is not Possible here, Use this only for Removal of Voters")
            }
            else{
                currentField.clearError()
            }
        }
    }

} 

else{
    currentField.setReadOnly(true)
}