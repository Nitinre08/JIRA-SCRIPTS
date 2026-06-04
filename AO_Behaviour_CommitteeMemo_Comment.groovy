package ANOP

import com.atlassian.jira.component.ComponentAccessor;
def otherField1 = getFieldByName("How was it solved?")
def otherField2 = getFieldByName("Why?")
def otherField3 = getFieldByName("Did Scope have access to?")
def otherField4 = getFieldByName("Select Date")
otherField1.setHidden(true)
otherField2.setHidden(true)
//otherField3.setHidden(true)
otherField4.setHidden(true)

otherField1.setRequired(false)
otherField2.setRequired(false)
//otherField3.setRequired(false)
otherField4.setRequired(false)

//otherField3.setRequired(false)
def CfotherField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Issuer participation").first()
def valCfotherField=underlyingIssue.getCustomFieldValue(CfotherField)

if(valCfotherField.toString()=="Yes"){
    otherField3.setHidden(false)
    otherField3.setRequired(true)
}
else //if(valCfotherField.toString()=="No"){
{
    otherField3.setHidden(true)
    otherField3.setRequired(false)
}

def Cfs= ComponentAccessor.getCustomFieldManager().getCustomFieldObjects(underlyingIssue)
Cfs.each{
    if(it.isEditable() && !getFieldByName(it.name).isHidden()){
        def st = getFieldByName(it.name)
       // if(!it.name.equalsIgnoreCase("Associate Analyst Voting Approval date"))
        //{
        !it.hasValue(underlyingIssue)? st.setRequired(true) && st.setHidden(false): st.setRequired(false) && st.setHidden(true)
       // }

        if(it.name.equalsIgnoreCase("Model or Scorecard Used")||it.name.equalsIgnoreCase("Associate analyst approval reason")|| it.name.equalsIgnoreCase("Associate Analyst Voting Approval date")){
             st.setRequired(false) 
             st.setHidden(false)
        }
		//st.getValue()== null? st.setRequired(true) && st.setHidden(false): st.setRequired(false) && st.setHidden(true)
        //st.setDescription(""+st.getValue())
    }
}
