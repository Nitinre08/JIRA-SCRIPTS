package ANOP

import com.atlassian.jira.component.ComponentAccessor

def otherField1 = getFieldByName("How was it solved?")
def otherField2 = getFieldByName("Why?")
def otherField3 = getFieldByName("Did Scope have access to?")
def otherField4 = getFieldByName("Date of Invitation Email") //
def otherField5 = getFieldByName("Issuer participation")

def CfotherField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Issuer participation").first()
def valCfotherField=underlyingIssue.getCustomFieldValue(CfotherField)

otherField1.setHidden(true)
otherField2.setHidden(true)
otherField4.setHidden(true)

otherField1.setRequired(false)
otherField2.setRequired(false)
otherField4.setRequired(false)

if(valCfotherField.toString()=="Yes"){
    otherField3.setHidden(false)
    otherField3.setRequired(true)
}
else //if(valCfotherField.toString()=="No"){
{
    otherField3.setHidden(true)
    otherField3.setRequired(false)
}