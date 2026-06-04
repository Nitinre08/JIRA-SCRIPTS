package ANOP

import java.util.ArrayList
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor

def Cfguests= ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Guests").first()
def currentField= getFieldById(getFieldChanged()) 
if(null!=underlyingIssue.getCustomFieldValue(Cfguests)) {
    currentField.setReadOnly(false)

    List<ApplicationUser> votersVal= underlyingIssue.getCustomFieldValue(Cfguests) as List<ApplicationUser>
    List<String> voterUsers= new ArrayList()
    votersVal.each { 
        voterUsers.add(it.getUsername())
    }
    
    def currentValue = currentField.getValue()
    //currentField.setDescription("Hi: "+currentValue.class)
    if(currentValue.class.typeName == "java.lang.String"){
        if(!voterUsers.contains(currentValue)){
            currentField.setError("Adding of new Guests is not Possible here, Use this only for Removal of Guests")
        }
        if(currentValue==null ||currentValue=="" ){
                currentField.clearError() 
            }
    }
    
   else{
        currentValue.each{
            if(!voterUsers.contains(it)){
             currentField.setError("Adding of new Guests is not Possible here, Use this only for Removal of Guests")
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