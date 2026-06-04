package ANOP

def reasonForDecline = getFieldById(getFieldChanged())

def selectedOption = reasonForDecline.getValue() as String

//def reasonForDeclineSelection = reasonForDecline.getValue()

def commnt=getFieldById('comment')
if (selectedOption.equalsIgnoreCase("Other (please precise in a comment)")){
    commnt.setRequired(true)
}
else{
    commnt.setRequired(false)
}