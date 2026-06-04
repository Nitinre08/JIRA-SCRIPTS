package ANOP

def currentField = getFieldById(getFieldChanged()) // field this behaviour script is defined on
def otherField = getFieldByName("Did Scope have access to?")
def solicitaionField = getFieldByName("Solicitation")

def valCfotherField= currentField.getValue()
def solciValue= solicitaionField.getValue()
//currentField.setDescription(""+solciValue)
if(valCfotherField.toString()=="Yes"){
    otherField.setHidden(false)
    //otherField.setRequired(true)
}
else 
{
    otherField.setHidden(true)
    otherField.setRequired(false)
    otherField.setFormValue(null)
}
