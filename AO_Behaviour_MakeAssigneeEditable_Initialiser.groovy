package ANOP

import java.util.ArrayList

def fieldDP = getFieldByName('Designated Party')
List dpsList1 = new ArrayList(['IAO-57311','IAO-235997','IAO-57316','IAO-235998','IAO-57320','IAO-189328'])
List dpsList2 = new ArrayList(['IAO-57310','IAO-235996','IAO-57315','IAO-57314','IAO-57317','IAO-236000'])
if(getActionName() == "Rating Fulfilled" || getActionName() == "In Progress")
{
   // getFieldById("comment").setFormValue(fieldDP.getValue().toString())
    
    fieldDP.setHidden(true)
    def field = getFieldByName('How many of the following apply:')
    if(dpsList1.contains(fieldDP.getValue().toString()))
    {
        field.setFieldOptions(['Non-standard transaction','Non-fully amortising debt relying on refinancing','Concentrated source of revenues','Market risk as the predominant risk'])
    }
    if(dpsList2.contains(fieldDP.getValue().toString()))
    {
        field.setFieldOptions(['Head-line risk','Non-diversified business','New business-product-entity','Existence of debt acceleration covenants','Susceptibility to event risk (e.g., war, environmental, political).'])
    }
}
if(getActionName() == "Designate Analysts" || getActionName() == "Edit Designated Analysts")
{
    getFieldByName("Designated Involved Analyst(s)").setDescription("Before you nominate the involved analyst, please review whether the conditions for the involved analyst nomination are met: <br>" + 
                            "The involved analyst is performing at least two of the five points below: <br>" +
                            "1. Reviewing the complete set of legal documents <br>" +
                            "2. Undertaking the entire quantitative analysis and model runs <br>" +
                            "3. Preparing or presenting to the Rating Committee <br>" +
                            "4. Acting as Rating Analyst for RAC, (whether a Rating committee is required or not) <br>" +
                            "5. Replacing the Lead Analyst during his/her absence while analytical work must be performed for a period of at least one calendar month.")
}
if(getActionName() == "Remove Involved Analyst(s)")
{
    getFieldByName("Involved Analyst(s) (Remove)").setDescription("Delete the participants that you would like to remove from the session, then click \"Remove Participants\".")
}