/**
 *  Left Open
 *
 *  Author: David Lane <me@dvdln.com>
 *  Date: 2014-01-19
 */
preferences
{
	section("Monitor this door or window") {
		input "contact", "capability.contactSensor"
	}
	section("And notify me if it's open for more than this many seconds (default 60)") {
		input "openThreshold", "number", description: "Number of seconds", required: false
	}
	section("Via text message at this number (or via push notification if not specified") {
		input "phone", "phone", title: "Phone number (optional)", required: false
	}
}

def installed()
{
	log.trace "installed()"
	subscribe()
}

def updated()
{
	log.trace "updated()"
	unsubscribe()
	subscribe()
}

def subscribe()
{
	subscribe(contact, "contact.open", contactOpen)
	subscribe(contact, "contact.closed", contactClosed)
}

def getThreshold()
{
	return (openThreshold != null && openThreshold != "") ? openThreshold : 60;
}

def contactOpen(evt)
{
    if (checkThreshold()) {
    	contactTriggered();
    } else {
		runIn(getThreshold(), "contactOpenTooLong")
    }
}

def contactClosed(evt)
{
	log.trace "doorClosed($evt.name: $evt.value)"
}

def contactOpenTooLong()
{
    def triggered = checkThreshold();
    
	if (triggered) {
        sendMessage()
	} else if (triggered == null) {
        log.warn "called but contact was already closed"
	}
}

def checkThreshold()
{
	def contactState = contact.currentState("contact")
    
    if (contactState.value == "open") {
        def elapsed = now() - contactState.rawDateCreated.time
        def threshold = (getThreshold() - 1) * 1000
        
        return (elapsed >= threshold)
    }
    
    return null
}

void sendMessage()
{
	def msg = "${contact.displayName} has been left open for ${openThreshold} seconds."
	log.info msg
	if (phone) {
		sendSms phone, msg
	}
	else {
		sendPush msg
	}
}
