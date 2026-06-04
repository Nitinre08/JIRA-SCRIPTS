package ANOP

//get custom fields by name
def DesignatedParty = getFieldByName("Designated Party")
def USABSRating = getFieldByName("US ABS Rating")
def Broadassetclass = getFieldByName("Broad asset class")
def Assetclass = getFieldByName("Asset class")
def Subassetclass = getFieldByName("Sub-asset class")
def Subsubassetclass = getFieldByName("Sub-sub-asset class")
def ScopeSector = getFieldByName("Scope Sector")
def EDHECGroup = getFieldByName("EDHEC Group")
def EDHECAsset = getFieldByName("EDHEC Asset")
def EDHECSector = getFieldByName("EDHEC Sector")
def Originators = getFieldByName("Originators")
def Sponsors = getFieldByName("Sponsors")
def Leadarrangingbanks = getFieldByName("Lead arranging banks")
def MainSponsors = getFieldByName("Main Sponsors")
def Issuer = getFieldByName("Issuer")
def Issuercontact = getFieldByName("Issuer contact")
def AddClientlegalentities = getFieldByName("Add Client legal entities")
def Solicitation = getFieldByName("Solicitation")

def DesignatedPartySelection = DesignatedParty.getValue()
def SolicitationSelection = Solicitation.getValue()
def OriginatorsSelection = Originators.getValue()
def SponsorsSelection = Solicitation.getValue()
def LeadarrangingbanksSelection = Solicitation.getValue()

//compare designated party value is "Structured Finance" or not
if (DesignatedPartySelection == "IAO-57320"){
    USABSRating.setFormValue(13702)
    USABSRating.setHidden(false)
}
else{
    USABSRating.setFormValue("")
    USABSRating.setHidden(true)
}

//Default hide fields
Broadassetclass.setHidden(true) // New Lines Start 15/3/23
Assetclass.setHidden(true)
Subassetclass.setHidden(true)
Subsubassetclass.setHidden(true)
Issuercontact.setHidden(true)  // New Line ends 15/3/23
/* Start- Pro Old changed on 15/03/2023
ScopeSector.setHidden(true)
EDHECGroup.setHidden(true)
EDHECAsset.setHidden(true)
EDHECSector.setHidden(true)
MainSponsors.setHidden(true)
Issuer.setHidden(true)
Issuercontact.setHidden(true)

End Pro Old changed on 15/03/2023 */

//if Designated Party is Structured Finance then make Broad asset class field mandatory
if (DesignatedPartySelection == "IAO-57320"){
    Broadassetclass.setRequired(true)
}

//If Designated Party is Project Finance, display all the realted fields and hide the fields related to Structured Finance
if ((DesignatedPartySelection == "IAO-57316")||(DesignatedPartySelection == "IAO-235998")) {
    ScopeSector.setRequired(true)
	Broadassetclass.setHidden(true)
    Broadassetclass.setRequired(false)
    Assetclass.setHidden(true)
    Subassetclass.setHidden(true)
    Subsubassetclass.setHidden(true)
    Originators.setHidden(true)
    Originators.setRequired(false)
    Sponsors.setHidden(true)
    Sponsors.setRequired(false)
    Leadarrangingbanks.setHidden(true)
    Leadarrangingbanks.setRequired(false)
    ScopeSector.setHidden(false)
    EDHECGroup.setHidden(false)
    EDHECAsset.setHidden(false)
    EDHECSector.setHidden(false)
    MainSponsors.setRequired(true)
    MainSponsors.setHidden(false)
    Issuer.setHidden(false)
    Issuercontact.setHidden(false)
}
else if (DesignatedPartySelection == "IAO-57320" || DesignatedPartySelection == "IAO-189328"){
    Broadassetclass.setHidden(false)
    Broadassetclass.setRequired(true)
    Assetclass.setHidden(false)
    Subassetclass.setHidden(false)
    Subsubassetclass.setHidden(false)
    Originators.setHidden(false)
    Sponsors.setHidden(false)
    Leadarrangingbanks.setHidden(false)
    ScopeSector.setRequired(false)
    MainSponsors.setRequired(false)
    MainSponsors.setHidden(true)
    Issuer.setHidden(true)
    Issuercontact.setHidden(true)
    ScopeSector.setHidden(true)
    EDHECGroup.setHidden(true)
    EDHECAsset.setHidden(true)
    EDHECSector.setHidden(true)
    /*
    Broadassetclass.setHidden(false)
    Assetclass.setHidden(false)
    Subassetclass.setHidden(false)
    Subsubassetclass.setHidden(false)
    Originators.setHidden(false)
    Sponsors.setHidden(false)
    Leadarrangingbanks.setHidden(false)
    ScopeSector.setRequired(false)
    MainSponsors.setRequired(false)*/
}