# jitterbit-azuresastokengenerator

Plugin to generate sas token for azure services.

### Usage
Set the right values to the plugin variables, then execute the plugin:

```
<trans>
resourceuri = "https://yourNameSpaceHere.servicebus.windows.net/yourTopicOrQueueHere/messages";

$azuresastokengenerator.key = $project.common.azure.sharedaccesskey;
$azuresastokengenerator.keyname = $project.common.azure.sharedaccesskeyname;
$azuresastokengenerator.resourceuri = resourceuri;
$azuresastokengenerator.howlonginhours= 1;

RunPlugin("<TAG>plugin:https://github.com/anyei/jitterbit-azuresastokengenerator</TAG>");

//the resulting sas token
WriteToOperationLog($azuresastokengenerator.sastoken);

</trans>

```
