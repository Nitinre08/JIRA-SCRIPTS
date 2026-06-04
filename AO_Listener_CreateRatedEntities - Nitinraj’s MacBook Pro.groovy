package ANOP

import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.issue.comments.Comment
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.riadalabs.jira.plugins.insight.channel.external.api.facade.ObjectFacade
import com.riadalabs.jira.plugins.insight.services.model.ObjectAttributeBean;
import com.riadalabs.jira.plugins.insight.services.model.ObjectBean;
import com.atlassian.jira.config.ResolutionManager;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.TransitionValidationResult;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexingService;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.workflow.TransitionOptions;
/*This Script will Create subtask for all the Insight object value of type "Legal Entity"
 
*/
@WithPlugin("com.riadalabs.jira.plugins.insight")


     
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
if(issue == null)
{
    MutableIssue issue = event.getIssue() as MutableIssue
}
if(issue.getIssueType().getName().contains("IO") && (!issue.getIssueType().getName().equalsIgnoreCase("IO Credit Rating Issuer Based") || !issue.getIssueType().getName().equalsIgnoreCase("IO RAS Issuer Based")))
{
//custom fields
    def cfSubsidiariesRating = customFieldManager.getCustomFieldObjectsByName("Subsidiaries Rating").first()
    def CfEntity = customFieldManager.getCustomFieldObjectsByName("Issuer").first()
    def CfSubsidiaries =customFieldManager.getCustomFieldObjectsByName("Subsidiaries").first()
    def CfApplyToAll = customFieldManager.getCustomFieldObjectsByName("Apply to all subsidiaries").first()
    def CfContact =  customFieldManager.getCustomFieldObjectsByName("Contact").first()
    def CfRatingNotificationInsider = customFieldManager.getCustomFieldObjectsByName("Notification insider").first()
    def CfPublicationType = customFieldManager.getCustomFieldObjectsByName("Publication type").first()
    def cfIssuerRating = customFieldManager.getCustomFieldObjectsByName("Issuer Rating").first()
    def cfOutstDebt = customFieldManager.getCustomFieldObjectsByName("Outstanding debt by class").first()
    def cfIndivDebt = customFieldManager.getCustomFieldObjectsByName("Individual debt").first()
    def cfFutureDebt = customFieldManager.getCustomFieldObjectsByName("Future Debt").first()
    def cfIOOriginator = customFieldManager.getCustomFieldObjectsByName("IO Originator").first()
    def cfinvolvedAnalyst = customFieldManager.getCustomFieldObjectsByName("Involved Analyst(s)").first()
    def cfinvolvedAnalystwaiting = customFieldManager.getCustomFieldObjectsByName("Involved Analyst(s) (waiting for acceptance)").first()
    def cfLeadAnalyst = customFieldManager.getCustomFieldObjectsByName("Lead Analyst").first()
    def cfLeadAnalysttwaiting = customFieldManager.getCustomFieldObjectsByName("Lead Analyst (waiting for acceptance)").first()
 
    //
    ApplicationUser user= ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
    ApplicationUser adminuser= Users.getByName("SA_Jira")
    List<String> subSummary= new ArrayList()
    List<String> NewSummary= new ArrayList()
    List<ObjectBean> mylist= new ArrayList()
    subSummary.add("List")
 
    def existingSubtasks = issue.getSubTaskObjects()
    existingSubtasks.each() {
        subSummary.add(it.getSummary()) 
    }
 
    List<CustomField> cfFromParent = [cfIssuerRating, cfOutstDebt, cfIndivDebt, cfFutureDebt, CfContact, CfRatingNotificationInsider, CfPublicationType, cfIOOriginator]
 
    ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(Users.getByName("SA_Jira"));
 
    Class objectFacadeClass =  ComponentAccessor.getPluginAccessor().getClassLoader().loadClass("com.riadalabs.jira.plugins.insight.channel.external.api.facade.ObjectFacade");
    Class objectTypeAttributeFacadeClass = ComponentAccessor.getPluginAccessor().getClassLoader().loadClass("com.riadalabs.jira.plugins.insight.channel.external.api.facade.ObjectTypeAttributeFacade");
    ObjectFacade objectFacade = ComponentAccessor.getOSGiComponentInstanceOfType(objectFacadeClass)
    String insightValues=""
    def allCfFields= customFieldManager.getCustomFieldObjects(issue)
    for (CustomField cf : allCfFields){
        if(cf.getCustomFieldType().getName().equalsIgnoreCase("Assets object") && issue.getCustomFieldValue(cf)){
        ArrayList myValue = issue.getCustomFieldValue(cf) as ArrayList
        myValue.each {
        ObjectBean insightField= it as ObjectBean
            if (objectFacade.loadObjectBean(insightField.getId()).getObjectTypeId()==1) {
                //def myAttrValue= insightField
                mylist.add(insightField)
                 
        }
         
        }
        }
    }
 
    if(mylist.size!=0){
        Set<ObjectBean> set = new HashSet<>(mylist);
        mylist.clear();
        mylist.addAll(set);
     
        mylist.each{
        def myAttrValue2= objectFacade.loadObjectBean(it.getId())
        String issuerSummary = myAttrValue2.name.toString() + "(" + objectAttribute(it, 49)+")"
        NewSummary.add(issuerSummary)   
            if(!subSummary.contains(issuerSummary)){
                createSubtask(issuerSummary, issue, cfFromParent, user)
                log.warn("SubTask Issue Crated")
                CommentManager commentMgr = ComponentAccessor.getCommentManager()
                
                // Chnages as per requirment JM-884
                if(issue.getStatus().name.equalsIgnoreCase("In progress")){
				    //JM-1931
                    if(issue.getCustomFieldValue("Publication type").toString() != "Private")
                    {
                        log.warn("Moving to Rotation Review")
                        transitIssue(issue,231, user) // Rotation Review
                    }
                    // Update Involved Analyst Waiting - Start
                    
                    List<ApplicationUser> involedAnalystWaiting= new ArrayList()
                    if(issue.getCustomFieldValue(cfinvolvedAnalystwaiting)){
                        involedAnalystWaiting.addAll(issue.getCustomFieldValue(cfinvolvedAnalystwaiting))
                    }
                    if(issue.getCustomFieldValue(cfinvolvedAnalyst)){   
                        involedAnalystWaiting.addAll(issue.getCustomFieldValue(cfinvolvedAnalyst))
                        issue.setCustomFieldValue(cfinvolvedAnalystwaiting,involedAnalystWaiting)
                        issue.setCustomFieldValue(cfinvolvedAnalyst,null)
                        ComponentAccessor.getIssueManager().updateIssue(adminuser, issue, EventDispatchOption.DO_NOT_DISPATCH,false)
                        involedAnalystWaiting.each { sendMail(it,issue) }                          
                         
                    } //// Update Involved Analyst Waiting - End
 
                    //Update Lead Analyst Waiting - Start
                    if(issue.getCustomFieldValue(cfLeadAnalyst) && null==issue.getCustomFieldValue(cfLeadAnalysttwaiting)){ 
                        ApplicationUser leadanalyst=  issue.getCustomFieldValue(cfLeadAnalyst) as ApplicationUser
                        issue.setCustomFieldValue(cfLeadAnalysttwaiting,issue.getCustomFieldValue(cfLeadAnalyst))
                        issue.setCustomFieldValue(cfLeadAnalyst,null)
                        ComponentAccessor.getIssueManager().updateIssue(adminuser, issue, EventDispatchOption.DO_NOT_DISPATCH,false)                           
                        sendMail(leadanalyst,issue)
                        commentMgr.create(issue, adminuser, "Legal Enities are updated in ticket, new attestations are requested to Analyst", false)
                    } //// Update Lead  Analyst Waiting - End

                    
 
                }
            }
        }
    }
         
    ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(user);
 
    List<MutableIssue> existingNewSubtasks = issue.getSubTaskObjects() as  List<MutableIssue>
    existingNewSubtasks.each() {
        def resolutionManager = ComponentAccessor.getOSGiComponentInstanceOfType(ResolutionManager.class)
        if(!NewSummary.contains(it.getSummary())){
        it.setResolution(resolutionManager.getResolutionByName("Canceled"))
         
        }else{
            it.setResolution(resolutionManager.getResolutionByName("Done"))
        }
        ComponentAccessor.getIssueManager().updateIssue(adminuser, (MutableIssue)it, EventDispatchOption.DO_NOT_DISPATCH,false)
    }
 
}
 
//function: get Insight Attribute
String objectAttribute(Object object, int attributeId) {
    def objectFacade =ComponentAccessor.getOSGiComponentInstanceOfType( ComponentAccessor.getPluginAccessor().getClassLoader().loadClass("com.riadalabs.jira.plugins.insight.channel.external.api.facade.ObjectFacade"))
    def objectAttribute = objectFacade.loadObjectAttributeBean(object.getId(), attributeId)
 
    if(objectAttribute){
    def objectAttributeValue = objectAttribute.getObjectAttributeValueBeans()
    return objectAttributeValue[0].value
}
}
 
//function: create subtask
void createSubtask(String summary, Issue parentIssue,  List<CustomField> cfFromParent, ApplicationUser user){
    MutableIssue newSubTask = ComponentAccessor.getIssueFactory().getIssue()
    newSubTask.setSummary(summary)
    newSubTask.setParentObject(parentIssue)
    newSubTask.setProjectObject(parentIssue.getProjectObject())
    newSubTask.setReporter(parentIssue.reporter)
    for (cf in cfFromParent) {
        newSubTask.setCustomFieldValue(cf, cf.getValue(parentIssue))
    }
    newSubTask.setIssueTypeId(ComponentAccessor.getConstantsManager().getAllIssueTypeObjects().find {
    it.getName() == "Rated Entity"
    }.id)
    def newIssueParamsissuer = ["issue": newSubTask] as Map<String, Object>   
        ComponentAccessor.getIssueManager().createIssueObject(user, newIssueParamsissuer)
    ComponentAccessor.getSubTaskManager().createSubTaskIssueLink(parentIssue, newSubTask, user)
}
 
 
// Methiod to Transit Issue (Requirement Ticket : JM-884)
 
public static void transitIssue(MutableIssue issue, int actionId, ApplicationUser loggedInUserName) {
    if(!issue.getStatus().name.equalsIgnoreCase("TEAM APPROVAL")){
 
        IssueService issueService = ComponentAccessor.getIssueService();
        TransitionOptions transitionOptions = new TransitionOptions.Builder().skipConditions().skipPermissions()
                .skipValidators().build();
 
        ConstantsManager contManager = ComponentAccessor.getConstantsManager();
        //Status statusID = contManager.getStatusByNameIgnoreCase(statusName);// ("Finalized");
        IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService.class);
        IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
 
        TransitionValidationResult validationResult = issueService.validateTransition(loggedInUserName, issue.getId(),
                actionId, issueInputParameters, transitionOptions);
        if (validationResult.isValid()) {
            issueService.transition(loggedInUserName, validationResult);
            //issue.setStatusId(statusID.getId());
            ComponentAccessor.getIssueManager().updateIssue(loggedInUserName, issue, EventDispatchOption.ISSUE_UPDATED,
                    false);
            try {
                issueIndexingService.reIndex(validationResult.getIssue());
            } catch (IndexException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
 
        } else {
            Collection<String> errors = validationResult.getErrorCollection().getErrorMessages();
            for (String error : errors) {
                // Logger.getLogger(error);
 
            }
        }
    }
 
}
 
// Method to send email
import com.atlassian.mail.Email;
import com.atlassian.mail.queue.SingleMailQueueItem;
 
public void sendMail(ApplicationUser user, MutableIssue issue){
        String emilId= user.getEmailAddress()
        Email email = new Email(emilId);
        //email.setCc("another_email_address@company.com");
        email.setSubject("Scope ANOP -New legal entity Added in IO: "+issue.getKey() );
        def baseurl = com.atlassian.jira.component.ComponentAccessor.getApplicationProperties().getString("jira.baseurl")
        String issueLink= baseurl+"/browse/"+issue.key
        String msgbody= "Dear ${user.getDisplayName()},<br><br> New Legal Entity is added for the following Internal Order: <br><br>  $issue.summary : $issueLink  <br><br> As you are one of the Analyst, Please approve or reject directly on the Internal Order.<br><br> <br> Best Regards, <br>Your Rating Operations Team"
        email.setMimeType("text/html");
        email.setBody(msgbody);
        SingleMailQueueItem smqi = new SingleMailQueueItem(email);
        ComponentAccessor.getMailQueue().addItem(smqi);
        log.warn "Email Sent"
}