/**
 *  Awesome Alarm
 *
 *  Author: David Lane
 *  Date: 2014-01-25
 */
preferences {
	section("Alarm") {
    	input "enabled", "bool", title: "Use alarm"
    	input "time", "time", title: "Time to get up"
    }
    
	section("Kill Switch") {
		input "switches", "capability.switch", title: "Turns off with", multiple: true
	}
    
	section("Pushover") {
		input "pushUser", "text", title: "User or group ID"
		input "pushApp", "text", title: "Application ID"
        input "pushDevice", "text", title: "Device name", required: false
	}
}

/**
 * Installed handler.
 */
def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

/**
 * Update handler.
 */
def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
    unschedule()
    
	initialize()
}

/**
 * Initialize alarm.
 */
def initialize() {
	startAlarm()
	//schedule time, "startAlarm"
}

/**
 * Start alarm process.
 */
def startAlarm() {
	state.on = checkSwitches()
    
    if (!enabled) {
    	log.debug "Alarm is not enabled"
    } else if (state.on) {
    	log.debug "A switch is on; not starting alarm"
    } else {
		log.debug "Starting alarm"
	    subscribe switches, "switch.on", "stopAlarm"
	    pollAlarm()
    }
}

/**
 * Poll alarm to see if we should keep going.
 */
def pollAlarm() {
	if (state.on) {
    	log.debug "Polling: a switch was turned on; cancelling"
    } else {
    	log.debug "Polling: switches are still off"
        
    	if (sendAlert()) {
        	runIn(30, "pollAlarm")
        }
    }
}

/**
 * Send alarm!
 */
def sendAlert() {
	log.debug "SEND ALERT?!"
	//def postBody = "[token:'${pushApp}',user:'${pushUser}',message:'Hit the kill switch to disable alarm!']"
	//httpPost "https://api.pushover.net/1/messages.json", postBody
    return true
}

/**
 * Stop sending alarm when a switch is turned on.
 */
def stopAlarm(evt) {
	log.debug "A switch was triggered"
    
	if (evt.value == "off") {
    	log.debug "Switch is still off; doing nothing"
    } else {
    	log.debug "Switch is on; stopping alarm"
        
        state.on = true
        unsubscribe switches
    }
}

/**
 * Check current switch status.
 */
def checkSwitches() {
	for (device in switches) {
    	if ("on" == device.latestValue("switch")) {
        	log.debug "At least one switch is currently on"
        	return true
        }
    }
    log.debug "All switches are turned off"
    return false
}
