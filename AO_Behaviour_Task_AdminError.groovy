package ANOP

if(getFieldByName("Admin Error").getValue().toString() == "Yes")
{
    getFieldByName("Admin Error Date").setHidden(false)
    getFieldByName("Admin Error Date").setRequired(true)
}
else
{
    getFieldByName("Admin Error Date").setHidden(true)
    getFieldByName("Admin Error Date").setRequired(false)
}