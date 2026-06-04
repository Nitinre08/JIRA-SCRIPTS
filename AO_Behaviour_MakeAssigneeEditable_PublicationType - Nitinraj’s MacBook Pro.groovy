package ANOP

def fieldVal = getFieldByName("Publication type").getValue().toString()
def existVal = underlyingIssue.getCustomFieldValue("Publication type").toString()
if(existVal == "Private")
{
   getFieldByName("Publication type").setReadOnly(true)
   getFieldByName("Publication type").setHelpText("FieldValue cannot be changed from/to value Private")
}
else if (getActionName() != "Create" && fieldVal == "Private" && existVal != "Private") 
{
    getFieldByName("Publication type").setError("FieldValue cannot be changed from/to value Private")
}
else
{
    getFieldByName("Publication type").setReadOnly(false)
    getFieldByName("Publication type").clearError()
} 