package ANOP

def currentField = getFieldById(getFieldChanged()) // field this behaviour script is defined on
def otherField = getFieldByName('Issuer Compliance Notice about Disclosure signed?')
def publ_type= currentField.value.toString()
// set errors to ensure dependant fields are populated
if (publ_type.equalsIgnoreCase("Private") ) {
    otherField.setRequired(true)
} 
else {
     otherField.setRequired(false)
}