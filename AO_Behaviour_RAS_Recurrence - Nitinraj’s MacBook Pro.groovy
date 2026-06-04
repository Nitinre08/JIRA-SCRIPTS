package ANOP

if(!issueContext.getIssueType().name.equalsIgnoreCase("Contract")){
    def recurrence = getFieldByName("Recurrence")
    recurrence.setFormValue(13335)
}