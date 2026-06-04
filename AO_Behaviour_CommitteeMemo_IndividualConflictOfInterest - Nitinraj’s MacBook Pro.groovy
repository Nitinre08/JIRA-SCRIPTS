package ANOP

import com.atlassian.jira.component.ComponentAccessor


def CfotherField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("How was it solved?").first()
def valCfotherField=underlyingIssue.getCustomFieldValue(CfotherField)

def currentField = getFieldById(getFieldChanged()) // field this behaviour script is defined on
def otherField = getFieldByName("How was it solved?")
if (currentField.value=="Yes") {
   otherField.setHidden(false) 
   otherField.setRequired(true) 
     if(valCfotherField!=null){
  		 otherField.setFormValue(valCfotherField)
    }
} 
else {
    otherField.setHidden(true) 
    otherField.setRequired(false) 
    otherField.setFormValue("") 
}