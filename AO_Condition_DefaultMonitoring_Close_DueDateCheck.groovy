package ANOP

Date today= new Date()
def dueDate= issue.getDueDate() as Date

today >=dueDate-15
