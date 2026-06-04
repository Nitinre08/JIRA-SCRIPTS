package ANOP

if(getFieldByName("Editorial Note").getValue().toString() == "Yes")
{
    getFieldByName("Editorial Note Date").setHidden(false)
    getFieldByName("Editorial Note Date").setRequired(true)
}
else
{
    getFieldByName("Editorial Note Date").setHidden(true)
    getFieldByName("Editorial Note Date").setRequired(false)
}