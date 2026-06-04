package ANOP

import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.riadalabs.jira.plugins.insight.channel.external.api.facade.ObjectFacade
import com.riadalabs.jira.plugins.insight.services.model.ObjectBean

@WithPlugin('com.riadalabs.jira.plugins.insight')
@PluginModule ObjectFacade objectFacade
def currentField = getFieldById(getFieldChanged()) // field this behaviour script is defined on
def summmry= getFieldById('summary')
//def otherField = getFieldByName('Other Field Name')
def currentfieldval= currentField.value
def systemObject = objectFacade.loadObjectBean(currentfieldval as String) as ObjectBean
def name= objectFacade.loadObjectAttributeBean(systemObject.id, 5960).getObjectAttributeValueBeans()[0].getValue()
summmry.setFormValue(name)