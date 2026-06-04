package ANOP

import com.atlassian.jira.component.ComponentAccessor
import java.time.format.DateTimeFormatter;  
def CfotherField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Issuer participation invite date").first()
//def valCfotherField=underlyingIssue.getCustomFieldValue(CfotherField)
DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  

def currentField = getFieldById(getFieldChanged()) // field this behaviour script is defined on
def otherField = getFieldByName("Issuer participation invite date")
if (currentField.value=="Yes") {
   otherField.setHidden(false) 
   otherField.setRequired(true) 
    if(null!=underlyingIssue.getCustomFieldValue(CfotherField)){
        Date valCfotherField = underlyingIssue.getCustomFieldValue(CfotherField) as Date
  		 otherField.setFormValue(dtf.format(valCfotherField.toLocalDate()))
    }
} 
else {
    otherField.setHidden(true) 
    otherField.setRequired(false)
    otherField.setFormValue("") 
}