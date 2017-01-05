/**
 *  Remember Your Stuff
 *
 *  Author: me@dvdln.com
 *  Date: 2013-03-07
 *
 *  Receive a notification when you leave a location without your stuff.
 */

preferences
{
	section("When all these people leave home") {
		input "people", "capability.presenceSensor", multiple: true
	}
	section("And any of these are left behind") {
		input "things", "capability.presenceSensor", multiple: true
	}
	section("Notifications") {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values:["Yes", "No"]], required: false
		input "phone", "phone", title: "Send a text message?", required: false
	}
}

def installed()
{
	subscribe(people, "presence", presence)
}

def updated()
{
	unsubscribe()
	subscribe(people, "presence", presence)
}

def presence(evt)
{
	if (evt.value == "not present") {
        checkPeople()
	}
}

private checkPeople()
{
	for (person in people) {
    	if (person.currentPresence == "present") {
        	return
        }
    }
    
    checkMyStuff()
}

private checkMyStuff()
{
	for (thing in things) {
		if (thing.currentPresence == "present") {
			send("You left ${thing.displayName} behind!")
		}
	}
}

private send(msg)
{
	if (sendPushMessage != "No") {
		sendPush(msg)
	}

	if (phone) {
		sendSms(phone, msg)
	}
}
