package ANOP

import com.atlassian.jira.component.ComponentAccessor
def currentField = getFieldById(getFieldChanged()) // field this behaviour script is defined on
def CfotherField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Why?").first()

String valCfotherField=underlyingIssue.getCustomFieldValue(CfotherField) as String

   
def otherField = getFieldByName('Why?')
if (currentField.value=="Yes") {
   otherField.setHidden(true) 
   otherField.setRequired(false) 
   otherField.setFormValue("") 
} 
else if (currentField.value=="No") {
    otherField.setHidden(false) 
    otherField.setRequired(true) 
    if(valCfotherField!=null){
  		 otherField.setFormValue(""+valCfotherField)
    }
   
}