package ANOP

import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.component.ComponentAccessor;
import static com.atlassian.jira.issue.IssueFieldConstants.ISSUE_TYPE

def issueTypesField = getFieldById(ISSUE_TYPE)
def removeIssueTypes = ComponentAccessor.constantsManager.allIssueTypeObjects.findAll { it.name in ["IO Scope Fund Analysis","Meeting","Committee", "Discussion","Monitoring Review", "Conflict check", "Committee Memo","Bulk Committee Action"] }
def requiredIssueTypes = []
requiredIssueTypes.addAll(ComponentAccessor.constantsManager.allIssueTypeObjects)
requiredIssueTypes.removeAll(removeIssueTypes)
issueTypesField.setFieldOptions(requiredIssueTypes) 


def field = getFieldByName("Conflict of interest type")
issueContext.getIssueType()
if (issueContext.getIssueType().name.equalsIgnoreCase("RO Release") && issueContext.getProjectObject().getKey().equalsIgnoreCase("AO")){
    field.setFieldOptions(['Blacklist', 'Greylist', 'Watchlist','No conflict','Missing information'])
}
else{
    field.setFieldOptions(['Blacklist', 'Greylist', 'Watchlist','No conflict'])
}
IssueManager issueManager = ComponentAccessor.getIssueManager();
def parent = getFieldById("parentIssueId");
String issueType = issueContext.issueType.name;

//SUBTASK MANAGEMENT
if(parent!=null) {

  Long parentIssueId = parent.getFormValue() as Long;
  Issue parentIssue = issueManager.getIssueObject(parentIssueId);
  if(parentIssue!=null){
    String parentIssueType = parentIssue.getIssueType().getName();
    def allIssueTypes = ComponentAccessor.constantsManager.allIssueTypeObjects;
    def availableIssueTypes = [];
    if(parentIssueType.equalsIgnoreCase("Committee Memo")) {
      availableIssueTypes.addAll(allIssueTypes.findAll { it.name in ["Memo Entities"] });
    } 
    /*else {
      availableIssueTypes.addAll(allIssueTypes.findAll { it.name in ["Sub-Documentation"] });
    }*/
    def issueTypeField = getFieldById(com.atlassian.jira.issue.IssueFieldConstants.ISSUE_TYPE);
    issueTypeField.setFieldOptions(availableIssueTypes);
  }
}

//JM-2495

def fieldEndorsement = getFieldById("customfield_24111") //getFieldByName("Endorsement")
def cfEndorseReason =  getFieldByName("Endorsement reason") //
def fieldConfig = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_24109").getRelevantConfig(underlyingIssue)
def selectListOptions = ComponentAccessor.getOptionsManager().getOptions(fieldConfig)
def optionsList = new ArrayList()
//getFieldByName("Notes").setFormValue(fieldEndorsement.getValue())
if(fieldEndorsement.getValue().toString() == "UK Endorsed")
{
    optionsList.addAll(["21a The rated entity/ instrument/ transaction is foreign and the lead analyst with the most expertise is located in Scope Ratings GmbH.",
    "21b Rating of the rated entity is dependent on the foreign parent company and the lead analyst with the most expertise is located in Scope Ratings GmbH.",
    "21c The lead analyst with the most expertise is located in Scope Ratings GmbH.",
    "21d(i) A CRA has only recently opened a UK office and the staff that have the experience to rate some UK entities or asset classes are not yet based in the UK.",
    "21.d(ii) As a consequence of a corporate action (a takeover or merger) the asset class are located outside the UK.",
    "21.d(iii) Absence of key analytical staff in the UK which could not reasonably have been foreseen or planned for.",
    "Deviation (other objective reason)"])
}
else if(fieldEndorsement.getValue().toString() == "EU Endorsed")
{
    optionsList.addAll(["21a The rated entity/ instrument/ transaction is foreign and the lead analyst with the most expertise is located in Scope Ratings UK Ltd.",
    "21b Rating of the rated entity is dependent on the foreign parent company and the lead analyst with the most expertise is located in Scope Ratings UK Ltd.",
    "21c The lead analyst with the most expertise is located in Scope Ratings UK Ltd.",
    "21d(i) A CRA has only recently opened a EUUK office and the staff that have the experience to rate some EU entities or asset classes are not yet based in the EU.",
    "21.d(ii) As a consequence of a corporate action (a takeover or merger) the asset class are located outside the EU.",
    "21.d(iii) Absence of key analytical staff in the EU which could not reasonably have been foreseen or planned for.",
    "Deviation (other objective reason)"])
}
// Filter the select list options based on the text field value
def filteredOptions = selectListOptions.findAll {it.value in optionsList }

cfEndorseReason.setFieldOptions(filteredOptions)
